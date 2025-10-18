using Cassandra;
using CassandraSession = Cassandra.ISession;

namespace BeautySalonAPI.Data;

public class CassandraContext : IDisposable
{
    private readonly ICluster _cluster;
    private readonly CassandraSession _session;

    public CassandraContext(IConfiguration configuration)
    {
        var contactPoints = configuration["Cassandra:ContactPoints"] ?? "localhost";
        var port = int.Parse(configuration["Cassandra:Port"] ?? "9042");
        var keyspace = configuration["Cassandra:Keyspace"] ?? "beauty_salon";
        var datacenter = configuration["Cassandra:LocalDatacenter"] ?? "datacenter1";

        _cluster = Cluster.Builder()
            .AddContactPoints(contactPoints.Split(','))
            .WithPort(port)
            .WithQueryOptions(new QueryOptions().SetConsistencyLevel(ConsistencyLevel.LocalQuorum))
            .Build();

        _session = _cluster.Connect(keyspace);
    }

    public CassandraSession Session => _session;

    public void Dispose()
    {
        _session?.Dispose();
        _cluster?.Dispose();
    }
}
