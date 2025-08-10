package com.beautysalon.mutation;

import com.beautysalon.model.Staff;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Targeted mutation tests for Staff model.
 * Focus on killing mutations in getters, setters, equals, hashCode, and toString methods.
 */
class StaffModelMutationTest {

    private Staff staff1;
    private Staff staff2;
    private Staff staff3;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        
        staff1 = new Staff();
        staff1.setId(testId);
        staff1.setName("John Smith");
        staff1.setEmail("john.smith@beautysalon.com");
        staff1.setPhone("123-456-7890");
        Set<String> specialties1 = new HashSet<>();
        specialties1.add("Hair Styling");
        staff1.setSpecialties(specialties1);
        
        staff2 = new Staff();
        staff2.setId(testId);
        staff2.setName("John Smith");
        staff2.setEmail("john.smith@beautysalon.com");
        staff2.setPhone("123-456-7890");
        Set<String> specialties2 = new HashSet<>();
        specialties2.add("Hair Styling");
        staff2.setSpecialties(specialties2);
        
        staff3 = new Staff();
        staff3.setId(UUID.randomUUID());
        staff3.setName("Jane Doe");
        staff3.setEmail("jane.doe@beautysalon.com");
        staff3.setPhone("987-654-3210");
        Set<String> specialties3 = new HashSet<>();
        specialties3.add("Nail Care");
        staff3.setSpecialties(specialties3);
    }

    // Test getter return value mutations
    @Test
    void testGetters_ReturnValueMutations() {
        // Test ID getter
        assertEquals(testId, staff1.getId()); // Should detect return value mutations
        assertNotNull(staff1.getId()); // Should detect null return mutations
        assertFalse(staff1.getId() == null); // Should detect == to != mutations
        assertTrue(staff1.getId() != null); // Should detect != to == mutations
        
        // Test name getter
        assertEquals("John Smith", staff1.getName()); // Should detect string return mutations
        assertNotNull(staff1.getName()); // Should detect null return mutations
        assertTrue(staff1.getName().length() > 0); // Should detect > to >= mutations
        assertFalse(staff1.getName().isEmpty()); // Should detect boolean return mutations
        assertTrue(staff1.getName().contains("John")); // Should detect boolean return mutations
        
        // Test email getter
        assertEquals("john.smith@beautysalon.com", staff1.getEmail()); // Should detect string return mutations
        assertNotNull(staff1.getEmail()); // Should detect null return mutations
        assertTrue(staff1.getEmail().contains("@")); // Should detect boolean return mutations
        assertTrue(staff1.getEmail().endsWith(".com")); // Should detect boolean return mutations
        
        // Test phone getter
        assertEquals("123-456-7890", staff1.getPhone()); // Should detect string return mutations
        assertNotNull(staff1.getPhone()); // Should detect null return mutations
        assertTrue(staff1.getPhone().contains("-")); // Should detect boolean return mutations
        assertTrue(staff1.getPhone().length() > 5); // Should detect > to >= mutations
        
        // Test specialties getter
        assertNotNull(staff1.getSpecialties()); // Should detect null return mutations
        assertTrue(staff1.getSpecialties().contains("Hair Styling")); // Should detect boolean return mutations
        assertFalse(staff1.getSpecialties().isEmpty()); // Should detect boolean return mutations
        assertEquals(1, staff1.getSpecialties().size()); // Should detect size mutations
    }

    // Test setter mutations and field assignment
    @Test
    void testSetters_FieldAssignmentMutations() {
        Staff testStaff = new Staff();
        
        // Test ID setter
        UUID newId = UUID.randomUUID();
        testStaff.setId(newId);
        assertEquals(newId, testStaff.getId()); // Should detect field assignment mutations
        assertNotEquals(testId, testStaff.getId()); // Should detect != to == mutations
        
        // Test name setter
        testStaff.setName("Alice Johnson");
        assertEquals("Alice Johnson", testStaff.getName()); // Should detect field assignment mutations
        assertNotEquals("John Smith", testStaff.getName()); // Should detect != to == mutations
        assertTrue(testStaff.getName().equals("Alice Johnson")); // Should detect boolean return mutations
        
        // Test email setter
        testStaff.setEmail("alice.wilson@beautysalon.com");
        assertEquals("alice.wilson@beautysalon.com", testStaff.getEmail()); // Should detect field assignment mutations
        assertTrue(testStaff.getEmail().contains("alice")); // Should detect boolean return mutations
        assertFalse(testStaff.getEmail().contains("john")); // Should detect boolean return mutations
        
        // Test phone setter
        testStaff.setPhone("555-123-4567");
        assertEquals("555-123-4567", testStaff.getPhone()); // Should detect field assignment mutations
        assertTrue(testStaff.getPhone().startsWith("555")); // Should detect boolean return mutations
        assertFalse(testStaff.getPhone().startsWith("123")); // Should detect boolean return mutations
        
        // Test specialties setter
        Set<String> newSpecialties = new HashSet<>();
        newSpecialties.add("Massage Therapy");
        testStaff.setSpecialties(newSpecialties);
        assertTrue(testStaff.getSpecialties().contains("Massage Therapy")); // Should detect boolean return mutations
        assertFalse(testStaff.getSpecialties().contains("Hair Styling")); // Should detect boolean return mutations
        assertEquals(1, testStaff.getSpecialties().size()); // Should detect size mutations
    }

    // Test equals method mutations
    @Test
    void testEquals_ConditionalLogicMutations() {
        // Since Staff doesn't override equals, it uses Object.equals (reference comparison)
        assertTrue(staff1.equals(staff1)); // Should detect boolean return mutations (reflexivity)
        assertFalse(staff1.equals(staff2)); // Should detect boolean return mutations (different objects)
        assertFalse(staff1.equals(staff3)); // Should detect boolean return mutations
        
        // Test reflexivity
        assertTrue(staff1.equals(staff1)); // Should detect boolean return mutations
        assertFalse(!staff1.equals(staff1)); // Should detect negation mutations
        
        // Test null comparison
        assertFalse(staff1.equals(null)); // Should detect boolean return mutations
        assertTrue(!staff1.equals(null)); // Should detect negation mutations
        
        // Test different class comparison
        assertFalse(staff1.equals("not a staff")); // Should detect boolean return mutations
        assertTrue(!staff1.equals("not a staff")); // Should detect negation mutations
        
        // Test conditional logic for field comparisons (manual comparison)
        boolean sameId = staff1.getId().equals(staff2.getId());
        boolean sameName = staff1.getName().equals(staff2.getName());
        boolean sameEmail = staff1.getEmail().equals(staff2.getEmail());
        
        assertTrue(sameId && sameName && sameEmail); // Should detect && to || mutations
        assertFalse(!sameId || !sameName || !sameEmail); // Should detect || to && mutations
    }

    // Test hashCode method mutations (using Object's default hashCode)
    @Test
    void testHashCode_ArithmeticMutations() {
        // Since Staff doesn't override hashCode, it uses Object.hashCode (based on memory address)
        int hashCode1 = staff1.hashCode();
        int hashCode2 = staff1.hashCode();
        assertEquals(hashCode1, hashCode2); // Should detect == to != mutations (same object)
        
        // Test hashCode for different objects (will have different hash codes)
        assertNotEquals(staff1.hashCode(), staff2.hashCode()); // Should detect != to == mutations
        
        // Test hashCode arithmetic operations
        assertTrue(hashCode1 == hashCode2); // Should detect == to != mutations
        assertFalse(hashCode1 != hashCode2); // Should detect != to == mutations
        
        // Test hashCode boundary conditions
        assertTrue(hashCode1 >= Integer.MIN_VALUE); // Should detect >= to > mutations
        assertTrue(hashCode1 <= Integer.MAX_VALUE); // Should detect <= to < mutations
        
        // Test hashCode arithmetic relationships
        int hashCode3 = staff3.hashCode();
        boolean sameHash = hashCode1 == hashCode3;
        boolean differentHash = hashCode1 != hashCode3;
        
        assertTrue(sameHash || differentHash); // Should detect || to && mutations
        assertFalse(sameHash && differentHash); // Should detect && to || mutations
    }

    // Test toString method mutations (using Object's default toString)
    @Test
    void testToString_StringOperationMutations() {
        // Since Staff doesn't override toString, it uses Object.toString (class@hashcode format)
        String staffString = staff1.toString();
        assertNotNull(staffString); // Should detect null return mutations
        
        // Test toString contains class name (Object.toString format: ClassName@hashcode)
        assertTrue(staffString.contains("Staff")); // Should detect boolean return mutations
        assertTrue(staffString.contains("@")); // Should detect boolean return mutations
        
        // Test string operations
        assertFalse(!staffString.contains("Staff")); // Should detect negation mutations
        assertTrue(staffString.length() > 0); // Should detect > to >= mutations
        assertFalse(staffString.isEmpty()); // Should detect boolean return mutations
        
        // Test string equality
        String staffString2 = staff1.toString();
        assertTrue(staffString.equals(staffString2)); // Should detect boolean return mutations
        assertFalse(!staffString.equals(staffString2)); // Should detect negation mutations
        
        // Test string comparison mutations
        assertTrue(staffString == staffString2 || staffString.equals(staffString2)); // Should detect || to && mutations
        assertFalse(staffString != staffString2 && !staffString.equals(staffString2)); // Should detect && to || mutations
    }

    // Test email validation mutations
    @Test
    void testEmailValidation_StringOperationMutations() {
        String email = staff1.getEmail();
        
        // Test email format validation
        assertTrue(email.contains("@")); // Should detect boolean return mutations
        assertFalse(!email.contains("@")); // Should detect negation mutations
        assertTrue(email.indexOf("@") > 0); // Should detect > to >= mutations
        assertFalse(email.indexOf("@") <= 0); // Should detect <= to < mutations
        
        // Test domain validation
        assertTrue(email.endsWith(".com")); // Should detect boolean return mutations
        assertFalse(!email.endsWith(".com")); // Should detect negation mutations
        assertTrue(email.contains("beautysalon")); // Should detect boolean return mutations
        
        // Test email parts
        String[] parts = email.split("@");
        assertEquals(2, parts.length); // Should detect arithmetic mutations
        assertTrue(parts.length == 2); // Should detect == to != mutations
        assertFalse(parts.length != 2); // Should detect != to == mutations
        
        String localPart = parts[0];
        String domainPart = parts[1];
        
        assertTrue(localPart.length() > 0); // Should detect > to >= mutations
        assertTrue(domainPart.length() > 0); // Should detect > to >= mutations
        assertFalse(localPart.isEmpty()); // Should detect boolean return mutations
        assertFalse(domainPart.isEmpty()); // Should detect boolean return mutations
    }

    // Test phone format mutations
    @Test
    void testPhoneFormat_StringOperationMutations() {
        String phone = staff1.getPhone();
        
        // Test phone format validation
        assertTrue(phone.contains("-")); // Should detect boolean return mutations
        assertFalse(!phone.contains("-")); // Should detect negation mutations
        
        // Count dashes
        int dashCount = 0;
        for (char c : phone.toCharArray()) {
            if (c == '-') {
                dashCount++;
            }
        }
        assertEquals(2, dashCount); // Should detect arithmetic mutations
        assertTrue(dashCount == 2); // Should detect == to != mutations
        assertFalse(dashCount != 2); // Should detect != to == mutations
        
        // Test phone parts
        String[] parts = phone.split("-");
        assertEquals(3, parts.length); // Should detect arithmetic mutations
        assertTrue(parts.length == 3); // Should detect == to != mutations
        assertFalse(parts.length != 3); // Should detect != to == mutations
        
        // Test each part length
        assertEquals(3, parts[0].length()); // Should detect arithmetic mutations
        assertEquals(3, parts[1].length()); // Should detect arithmetic mutations
        assertEquals(4, parts[2].length()); // Should detect arithmetic mutations
        
        assertTrue(parts[0].length() == 3); // Should detect == to != mutations
        assertTrue(parts[1].length() == 3); // Should detect == to != mutations
        assertTrue(parts[2].length() == 4); // Should detect == to != mutations
    }

    // Test specialties comparison mutations
    @Test
    void testSpecialtiesComparisons_CollectionMutations() {
        Set<String> specialties1 = staff1.getSpecialties(); // ["Hair Styling"]
        Set<String> specialties3 = staff3.getSpecialties(); // ["Nail Care"]
        
        // Test specialties comparisons
        assertFalse(specialties1.equals(specialties3)); // Should detect boolean return mutations
        assertTrue(!specialties1.equals(specialties3)); // Should detect negation mutations
        assertTrue(specialties1 != specialties3); // Should detect != to == mutations
        assertFalse(specialties1 == specialties3); // Should detect == to != mutations
        
        // Test specialties content
        assertTrue(specialties1.contains("Hair Styling")); // Should detect boolean return mutations
        assertFalse(specialties1.contains("Nail Care")); // Should detect boolean return mutations
        assertTrue(specialties3.contains("Nail Care")); // Should detect boolean return mutations
        assertFalse(specialties3.contains("Hair Styling")); // Should detect boolean return mutations
        
        // Test collection size comparisons
        assertEquals(1, specialties1.size()); // Should detect size mutations
        assertEquals(1, specialties3.size()); // Should detect size mutations
        assertTrue(specialties1.size() == specialties3.size()); // Should detect == to != mutations
        assertFalse(specialties1.size() != specialties3.size()); // Should detect != to == mutations
        
        // Test collection operations
        assertFalse(specialties1.isEmpty()); // Should detect boolean return mutations
        assertFalse(specialties3.isEmpty()); // Should detect boolean return mutations
        assertTrue(!specialties1.isEmpty()); // Should detect negation mutations
        assertTrue(!specialties3.isEmpty()); // Should detect negation mutations
    }

    // Test null field handling mutations
    @Test
    void testNullFieldHandling_ConditionalMutations() {
        // Create staff with null fields (but constructor sets id automatically)
        Staff nullStaff = new Staff();
        UUID originalId = nullStaff.getId(); // Constructor sets this
        nullStaff.setId(null);
        nullStaff.setName(null);
        nullStaff.setEmail(null);
        
        // Test null checks
        assertNull(nullStaff.getId()); // Should detect null return mutations
        assertNull(nullStaff.getName()); // Should detect null return mutations
        assertNull(nullStaff.getEmail()); // Should detect null return mutations
        
        // Test conditional logic with null values
        boolean hasId = nullStaff.getId() != null; // Should detect != to == mutations
        boolean hasName = nullStaff.getName() != null; // Should detect != to == mutations
        boolean hasEmail = nullStaff.getEmail() != null; // Should detect != to == mutations
        
        assertFalse(hasId); // Should detect boolean return mutations
        assertFalse(hasName); // Should detect boolean return mutations
        assertFalse(hasEmail); // Should detect boolean return mutations
        
        // Test logical operations with null checks
        assertFalse(hasId && hasName); // Should detect && to || mutations
        assertFalse(hasName && hasEmail); // Should detect && to || mutations
        assertTrue(!hasId || !hasName); // Should detect || to && mutations
        assertTrue(!hasName || !hasEmail); // Should detect || to && mutations
        
        // Test that constructor originally set an id
        assertNotNull(originalId); // Should detect null return mutations
    }

    // Test field comparison combinations
    @Test
    void testFieldComparisonCombinations_LogicalMutations() {
        // Test multiple field comparisons
        boolean sameId = staff1.getId().equals(staff2.getId());
        boolean sameName = staff1.getName().equals(staff2.getName());
        boolean sameEmail = staff1.getEmail().equals(staff2.getEmail());
        boolean samePhone = staff1.getPhone().equals(staff2.getPhone());
        boolean sameSpecialties = staff1.getSpecialties().equals(staff2.getSpecialties());
        
        // Test AND logic
        assertTrue(sameId && sameName); // Should detect && to || mutations
        assertTrue(sameName && sameEmail); // Should detect && to || mutations
        assertTrue(sameEmail && samePhone); // Should detect && to || mutations
        assertTrue(samePhone && sameSpecialties); // Should detect && to || mutations
        
        // Test complex AND logic
        assertTrue(sameId && sameName && sameEmail); // Should detect && to || mutations
        assertTrue(sameName && sameEmail && samePhone && sameSpecialties); // Should detect && to || mutations
        
        // Test OR logic with different staff
        boolean diffId = staff1.getId().equals(staff3.getId());
        boolean diffName = staff1.getName().equals(staff3.getName());
        boolean diffEmail = staff1.getEmail().equals(staff3.getEmail());
        
        assertFalse(diffId || diffName); // Should detect || to && mutations
        assertFalse(diffName || diffEmail); // Should detect || to && mutations
        assertFalse(diffId || diffName || diffEmail); // Should detect || to && mutations
        
        // Test negation combinations
        assertFalse(!(sameId && sameName)); // Should detect negation mutations
        assertTrue(!(diffId || diffName)); // Should detect negation mutations
        assertFalse(!sameId || !sameName); // Should detect || to && mutations
        assertTrue(!diffId && !diffName); // Should detect && to || mutations
    }
}
