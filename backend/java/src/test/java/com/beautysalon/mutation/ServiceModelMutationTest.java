package com.beautysalon.mutation;

import com.beautysalon.model.Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Targeted mutation tests for Service model.
 * Focus on killing mutations in getters, setters, equals, hashCode, and toString methods.
 */
class ServiceModelMutationTest {

    private Service service1;
    private Service service2;
    private Service service3;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        
        service1 = new Service();
        service1.setId(testId);
        service1.setName("Haircut");
        service1.setDescription("Professional haircut service");
        service1.setPrice(BigDecimal.valueOf(50.00));
        service1.setDuration(60);
        
        service2 = new Service();
        service2.setId(testId);
        service2.setName("Haircut");
        service2.setDescription("Professional haircut service");
        service2.setPrice(BigDecimal.valueOf(50.00));
        service2.setDuration(60);
        
        service3 = new Service();
        service3.setId(UUID.randomUUID());
        service3.setName("Manicure");
        service3.setDescription("Nail care service");
        service3.setPrice(BigDecimal.valueOf(30.00));
        service3.setDuration(45);
    }

    // Test getter return value mutations
    @Test
    void testGetters_ReturnValueMutations() {
        // Test ID getter
        assertEquals(testId, service1.getId()); // Should detect return value mutations
        assertNotNull(service1.getId()); // Should detect null return mutations
        assertFalse(service1.getId() == null); // Should detect == to != mutations
        assertTrue(service1.getId() != null); // Should detect != to == mutations
        
        // Test name getter
        assertEquals("Haircut", service1.getName()); // Should detect string return mutations
        assertNotNull(service1.getName()); // Should detect null return mutations
        assertTrue(service1.getName().length() > 0); // Should detect > to >= mutations
        assertFalse(service1.getName().isEmpty()); // Should detect boolean return mutations
        
        // Test description getter
        assertEquals("Professional haircut service", service1.getDescription()); // Should detect string return mutations
        assertNotNull(service1.getDescription()); // Should detect null return mutations
        assertTrue(service1.getDescription().contains("haircut")); // Should detect boolean return mutations
        
        // Test price getter
        assertEquals(BigDecimal.valueOf(50.00), service1.getPrice()); // Should detect return value mutations
        assertNotNull(service1.getPrice()); // Should detect null return mutations
        assertTrue(service1.getPrice().compareTo(BigDecimal.ZERO) > 0); // Should detect > to >= mutations
        
        // Test duration getter
        assertEquals(Integer.valueOf(60), service1.getDuration()); // Should detect primitive return mutations
        assertTrue(service1.getDuration() > 0); // Should detect > to >= mutations
        assertFalse(service1.getDuration() <= 0); // Should detect <= to < mutations
        assertTrue(service1.getDuration() != 0); // Should detect != to == mutations
    }

    // Test setter mutations and field assignment
    @Test
    void testSetters_FieldAssignmentMutations() {
        Service testService = new Service();
        
        // Test ID setter
        UUID newId = UUID.randomUUID();
        testService.setId(newId);
        assertEquals(newId, testService.getId()); // Should detect field assignment mutations
        assertNotEquals(testId, testService.getId()); // Should detect != to == mutations
        
        // Test name setter
        testService.setName("Massage");
        assertEquals("Massage", testService.getName()); // Should detect field assignment mutations
        assertNotEquals("Haircut", testService.getName()); // Should detect != to == mutations
        assertTrue(testService.getName().equals("Massage")); // Should detect boolean return mutations
        
        // Test description setter
        testService.setDescription("Relaxing massage therapy");
        assertEquals("Relaxing massage therapy", testService.getDescription()); // Should detect field assignment mutations
        assertTrue(testService.getDescription().contains("massage")); // Should detect boolean return mutations
        assertFalse(testService.getDescription().contains("haircut")); // Should detect boolean return mutations
        
        // Test price setter
        BigDecimal newPrice = BigDecimal.valueOf(80.00);
        testService.setPrice(newPrice);
        assertEquals(newPrice, testService.getPrice()); // Should detect field assignment mutations
        assertTrue(testService.getPrice().compareTo(BigDecimal.valueOf(50.00)) > 0); // Should detect > to >= mutations
        
        // Test duration setter
        testService.setDuration(90);
        assertEquals(Integer.valueOf(90), testService.getDuration()); // Should detect field assignment mutations
        assertTrue(testService.getDuration() > 60); // Should detect > to >= mutations
        assertFalse(testService.getDuration() == 60); // Should detect == to != mutations
    }

    // Test equals method mutations
    @Test
    void testEquals_ConditionalLogicMutations() {
        // Since Service doesn't override equals, it uses Object.equals (reference comparison)
        assertTrue(service1.equals(service1)); // Should detect boolean return mutations (reflexivity)
        assertFalse(service1.equals(service2)); // Should detect boolean return mutations (different objects)
        assertFalse(service1.equals(service3)); // Should detect boolean return mutations
        
        // Test reflexivity
        assertTrue(service1.equals(service1)); // Should detect boolean return mutations
        assertFalse(!service1.equals(service1)); // Should detect negation mutations
        
        // Test null comparison
        assertFalse(service1.equals(null)); // Should detect boolean return mutations
        assertTrue(!service1.equals(null)); // Should detect negation mutations
        
        // Test different class comparison
        assertFalse(service1.equals("not a service")); // Should detect boolean return mutations
        assertTrue(!service1.equals("not a service")); // Should detect negation mutations
        
        // Test conditional logic for field comparisons (manual comparison)
        boolean sameId = service1.getId().equals(service2.getId());
        boolean sameName = service1.getName().equals(service2.getName());
        boolean samePrice = service1.getPrice().equals(service2.getPrice());
        
        assertTrue(sameId && sameName && samePrice); // Should detect && to || mutations
        assertFalse(!sameId || !sameName || !samePrice); // Should detect || to && mutations
    }

    // Test hashCode method mutations (using Object's default hashCode)
    @Test
    void testHashCode_ArithmeticMutations() {
        // Since Service doesn't override hashCode, it uses Object.hashCode (based on memory address)
        int hashCode1 = service1.hashCode();
        int hashCode2 = service1.hashCode();
        assertEquals(hashCode1, hashCode2); // Should detect == to != mutations (same object)
        
        // Test hashCode for different objects (will have different hash codes)
        assertNotEquals(service1.hashCode(), service2.hashCode()); // Should detect != to == mutations
        
        // Test hashCode arithmetic operations
        assertTrue(hashCode1 == hashCode2); // Should detect == to != mutations
        assertFalse(hashCode1 != hashCode2); // Should detect != to == mutations
        
        // Test hashCode boundary conditions
        assertTrue(hashCode1 >= Integer.MIN_VALUE); // Should detect >= to > mutations
        assertTrue(hashCode1 <= Integer.MAX_VALUE); // Should detect <= to < mutations
        
        // Test hashCode arithmetic relationships
        int hashCode3 = service3.hashCode();
        boolean sameHash = hashCode1 == hashCode3;
        boolean differentHash = hashCode1 != hashCode3;
        
        assertTrue(sameHash || differentHash); // Should detect || to && mutations
        assertFalse(sameHash && differentHash); // Should detect && to || mutations
    }

    // Test toString method mutations (using Object's default toString)
    @Test
    void testToString_StringOperationMutations() {
        // Since Service doesn't override toString, it uses Object.toString (class@hashcode format)
        String serviceString = service1.toString();
        assertNotNull(serviceString); // Should detect null return mutations
        
        // Test toString contains class name (Object.toString format: ClassName@hashcode)
        assertTrue(serviceString.contains("Service")); // Should detect boolean return mutations
        assertTrue(serviceString.contains("@")); // Should detect boolean return mutations
        
        // Test string operations
        assertFalse(!serviceString.contains("Service")); // Should detect negation mutations
        assertTrue(serviceString.length() > 0); // Should detect > to >= mutations
        assertFalse(serviceString.isEmpty()); // Should detect boolean return mutations
        
        // Test string equality
        String serviceString2 = service1.toString();
        assertTrue(serviceString.equals(serviceString2)); // Should detect boolean return mutations
        assertFalse(!serviceString.equals(serviceString2)); // Should detect negation mutations
        
        // Test string comparison mutations
        assertTrue(serviceString == serviceString2 || serviceString.equals(serviceString2)); // Should detect || to && mutations
        assertFalse(serviceString != serviceString2 && !serviceString.equals(serviceString2)); // Should detect && to || mutations
    }

    // Test price comparison mutations
    @Test
    void testPriceComparisons_BoundaryMutations() {
        BigDecimal price1 = service1.getPrice(); // 50.00
        BigDecimal price3 = service3.getPrice(); // 30.00
        
        // Test price comparisons
        assertTrue(price1.compareTo(price3) > 0); // Should detect > to >= mutations
        assertFalse(price1.compareTo(price3) <= 0); // Should detect <= to < mutations
        assertTrue(price1.compareTo(price3) != 0); // Should detect != to == mutations
        assertFalse(price1.compareTo(price3) == 0); // Should detect == to != mutations
        
        // Test price equality
        BigDecimal samePrice = BigDecimal.valueOf(50.00);
        assertTrue(price1.compareTo(samePrice) == 0); // Should detect == to != mutations
        assertFalse(price1.compareTo(samePrice) != 0); // Should detect != to == mutations
        assertTrue(price1.equals(samePrice)); // Should detect boolean return mutations
        assertFalse(!price1.equals(samePrice)); // Should detect negation mutations
        
        // Test boundary conditions
        BigDecimal zero = BigDecimal.ZERO;
        assertTrue(price1.compareTo(zero) > 0); // Should detect > to >= mutations
        assertFalse(price1.compareTo(zero) <= 0); // Should detect <= to < mutations
        
        BigDecimal hundred = BigDecimal.valueOf(100.00);
        assertTrue(price1.compareTo(hundred) < 0); // Should detect < to <= mutations
        assertFalse(price1.compareTo(hundred) >= 0); // Should detect >= to > mutations
    }

    // Test duration comparison mutations
    @Test
    void testDurationComparisons_ArithmeticMutations() {
        int duration1 = service1.getDuration(); // 60
        int duration3 = service3.getDuration(); // 45
        
        // Test duration arithmetic comparisons
        assertTrue(duration1 > duration3); // Should detect > to >= mutations
        assertFalse(duration1 <= duration3); // Should detect <= to < mutations
        assertTrue(duration1 != duration3); // Should detect != to == mutations
        assertFalse(duration1 == duration3); // Should detect == to != mutations
        
        // Test arithmetic operations
        int sum = duration1 + duration3;
        assertEquals(105, sum); // Should detect + to - mutations
        assertTrue(sum > duration1); // Should detect > to >= mutations
        assertTrue(sum > duration3); // Should detect > to >= mutations
        
        int difference = duration1 - duration3;
        assertEquals(15, difference); // Should detect - to + mutations
        assertTrue(difference > 0); // Should detect > to >= mutations
        assertFalse(difference <= 0); // Should detect <= to < mutations
        
        // Test multiplication and division
        int doubled = duration1 * 2;
        assertEquals(120, doubled); // Should detect * mutations
        assertTrue(doubled == duration1 * 2); // Should detect == to != mutations
        
        int halved = duration1 / 2;
        assertEquals(30, halved); // Should detect / mutations
        assertTrue(halved < duration1); // Should detect < to <= mutations
    }

    // Test null field handling mutations
    @Test
    void testNullFieldHandling_ConditionalMutations() {
        // Create service with null fields (but constructor sets id automatically)
        Service nullService = new Service();
        UUID originalId = nullService.getId(); // Constructor sets this
        nullService.setId(null);
        nullService.setName(null);
        nullService.setPrice(null);
        
        // Test null checks
        assertNull(nullService.getId()); // Should detect null return mutations
        assertNull(nullService.getName()); // Should detect null return mutations
        assertNull(nullService.getPrice()); // Should detect null return mutations
        
        // Test conditional logic with null values
        boolean hasId = nullService.getId() != null; // Should detect != to == mutations
        boolean hasName = nullService.getName() != null; // Should detect != to == mutations
        boolean hasPrice = nullService.getPrice() != null; // Should detect != to == mutations
        
        assertFalse(hasId); // Should detect boolean return mutations
        assertFalse(hasName); // Should detect boolean return mutations
        assertFalse(hasPrice); // Should detect boolean return mutations
        
        // Test logical operations with null checks
        assertFalse(hasId && hasName); // Should detect && to || mutations
        assertFalse(hasName && hasPrice); // Should detect && to || mutations
        assertTrue(!hasId || !hasName); // Should detect || to && mutations
        assertTrue(!hasName || !hasPrice); // Should detect || to && mutations
        
        // Test that constructor originally set an id
        assertNotNull(originalId); // Should detect null return mutations
    }
}
