package com.beautysalon.config;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Runs Cassandra CQL migrations at application startup using files under
 * classpath: db/migration (e.g., V1__create_keyspace.cql, V2__create_tables.cql ...)
 *
 * This runner maintains a schema_migrations table to ensure idempotency.
 */
@Component
@ConditionalOnProperty(name = "migrations.enabled", havingValue = "true", matchIfMissing = true)
public class CassandraMigrationRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(CassandraMigrationRunner.class);

    private static final String MIGRATIONS_LOCATION = "classpath:/db/migration/V*.cql";
    private static final String MIGRATIONS_TABLE = "schema_migrations";

    private final CqlSession session;
    private final Environment env;

    public CassandraMigrationRunner(CqlSession session, Environment env) {
        this.session = session;
        this.env = env;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("[MIGRATIONS] Starting Cassandra migrations from {}", MIGRATIONS_LOCATION);
        
        // Load migration resources
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources(MIGRATIONS_LOCATION);
        if (resources.length == 0) {
            log.info("[MIGRATIONS] No migration files found. Skipping.");
            return;
        }

        // Sort by filename ascending (V1..Vn)
        List<Resource> sorted = Arrays.stream(resources)
                .sorted(Comparator.comparing(r -> Objects.requireNonNull(r.getFilename())))
                .collect(Collectors.toList());

        // Phase 1: If first migration creates keyspace and hasn't been applied yet, run it BEFORE creating migrations table
        // This avoids failures when the session is configured with a non-existent keyspace
        boolean ranEarlyKeyspace = false;
        String earlyVersion = null;
        String earlyDescription = null;
        if (!sorted.isEmpty()) {
            Resource first = sorted.get(0);
            String filename = Objects.requireNonNull(first.getFilename());
            String version = extractVersion(filename);
            String description = extractDescription(filename);
            String cqlFirst = readResourceAsString(first);
            if (containsCreateKeyspace(cqlFirst)) {
                log.info("[MIGRATIONS] Detected keyspace creation in V{} ({}). Applying early before creating migrations table...", version, description);
                executeCqlBatch(cqlFirst);
                ranEarlyKeyspace = true;
                earlyVersion = version;
                earlyDescription = description;
            }
        }

        // Switch to target keyspace and ensure metadata table exists
        useKeyspace(targetKeyspace());
        createMigrationsTableIfNotExists();

        // Load already applied versions
        Set<String> applied = fetchAppliedVersions();

        // If we ran the keyspace migration early, switch to the keyspace and mark it as applied
        if (ranEarlyKeyspace && earlyVersion != null && !applied.contains(earlyVersion)) {
            useKeyspace(targetKeyspace());
            session.execute(SimpleStatement.newInstance(
                    "INSERT INTO " + qualifiedMigrationsTable() + " (version, description, script, installed_on) VALUES (?, ?, ?, toTimestamp(now()))",
                    earlyVersion, earlyDescription, sorted.get(0).getFilename()));
            applied.add(earlyVersion);
            log.info("[MIGRATIONS] Early keyspace migration V{} recorded as applied.", earlyVersion);
        }

        for (Resource resource : sorted) {
            String filename = Objects.requireNonNull(resource.getFilename());
            String version = extractVersion(filename); // e.g., 1, 2, 3 ...
            String description = extractDescription(filename);

            if (applied.contains(version)) {
                log.info("[MIGRATIONS] Skipping already applied migration V{} ({})", version, description);
                continue;
            }

            log.info("[MIGRATIONS] Applying migration V{} ({}) from {}", version, description, filename);
            String cql = readResourceAsString(resource);
            executeCqlBatch(cql);

            // Record as applied
            session.execute(SimpleStatement.newInstance(
                    "INSERT INTO " + qualifiedMigrationsTable() + " (version, description, script, installed_on) VALUES (?, ?, ?, toTimestamp(now()))",
                    version, description, filename));
        
            log.info("[MIGRATIONS] Migration V{} applied successfully.", version);
        }

        log.info("[MIGRATIONS] All migrations completed.");
    }

    private void createMigrationsTableIfNotExists() {
        String cql = "CREATE TABLE IF NOT EXISTS " + qualifiedMigrationsTable() + " (" +
                "version text PRIMARY KEY, " +
                "description text, " +
                "script text, " +
                "installed_on timestamp" +
                ")";
        session.execute(SimpleStatement.newInstance(cql));
    }

    private Set<String> fetchAppliedVersions() {
        try {
            return session.execute("SELECT version FROM " + qualifiedMigrationsTable())
                    .all().stream()
                    .map(row -> row.getString("version"))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        } catch (Exception e) {
            log.warn("[MIGRATIONS] Could not fetch applied versions, assuming none applied: {}", e.getMessage());
            return new LinkedHashSet<>();
        }
    }

    private static String extractVersion(String filename) {
        // V1__desc.cql -> 1
        int start = filename.indexOf('V') + 1;
        int underscore = filename.indexOf("__");
        if (start < 1 || underscore < 0) return filename;
        return filename.substring(start, underscore);
    }

    private static String extractDescription(String filename) {
        int underscore = filename.indexOf("__");
        int dot = filename.lastIndexOf('.')
                ;
        if (underscore < 0 || dot < 0) return filename;
        return filename.substring(underscore + 2, dot).replace('_', ' ');
    }

    private static String readResourceAsString(Resource resource) throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    private static List<String> splitCqlStatements(String cql) {
        // Simple splitter on semicolons; good enough for our scripts
        List<String> stmts = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inSingleQuote = false;
        for (char ch : cql.toCharArray()) {
            if (ch == '\'') {
                inSingleQuote = !inSingleQuote;
            }
            if (ch == ';' && !inSingleQuote) {
                stmts.add(current.toString());
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }
        if (current.length() > 0) {
            stmts.add(current.toString());
        }
        return stmts;
    }

    private static String firstLine(String text) {
        int idx = text.indexOf('\n');
        return idx > 0 ? text.substring(0, idx) : text;
    }

    private static boolean containsCreateKeyspace(String cql) {
        return cql.toUpperCase(Locale.ROOT).contains("CREATE KEYSPACE");
    }

    private void executeCqlBatch(String cql) {
        List<String> statements = splitCqlStatements(cql);
        for (String stmt : statements) {
            String trimmed = stmt.trim();
            if (trimmed.isEmpty()) continue;
            log.debug("[MIGRATIONS] Executing: {}", firstLine(trimmed));
            session.execute(SimpleStatement.newInstance(trimmed));
        }
    }

    private String targetKeyspace() {
        String ks = env.getProperty("spring.cassandra.keyspace-name", "").trim();
        if (ks.isEmpty()) {
            String envKs = Optional.ofNullable(System.getenv("CASSANDRA_KEYSPACE")).orElse("").trim();
            if (!envKs.isEmpty()) return envKs;
            return "beauty_salon";
        }
        return ks;
    }

    private String qualifiedMigrationsTable() {
        return targetKeyspace() + "." + MIGRATIONS_TABLE;
    }

    /**
     * Execute USE keyspace statement to switch to target keyspace after creation
     */
    private void useKeyspace(String keyspace) {
        try {
            session.execute(SimpleStatement.newInstance("USE " + keyspace));
            log.info("[MIGRATIONS] Switched to keyspace: {}", keyspace);
        } catch (Exception e) {
            log.warn("[MIGRATIONS] Could not switch to keyspace {}: {}", keyspace, e.getMessage());
        }
    }
}
