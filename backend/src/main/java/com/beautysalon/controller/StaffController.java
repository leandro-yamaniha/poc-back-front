package com.beautysalon.controller;

import com.beautysalon.model.Staff;
import com.beautysalon.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/staff")
@CrossOrigin(origins = "*")
public class StaffController {
    
    @Autowired
    private StaffService staffService;
    
    @GetMapping
    public ResponseEntity<List<Staff>> getAllStaff() {
        List<Staff> staff = staffService.getAllStaff();
        return ResponseEntity.ok(staff);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<Staff>> getActiveStaff() {
        List<Staff> staff = staffService.getActiveStaff();
        return ResponseEntity.ok(staff);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Staff> getStaffById(@PathVariable UUID id) {
        Optional<Staff> staff = staffService.getStaffById(id);
        return staff.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/role/{role}")
    public ResponseEntity<List<Staff>> getStaffByRole(@PathVariable String role) {
        List<Staff> staff = staffService.getStaffByRole(role);
        return ResponseEntity.ok(staff);
    }
    
    @GetMapping("/role/{role}/active")
    public ResponseEntity<List<Staff>> getActiveStaffByRole(@PathVariable String role) {
        List<Staff> staff = staffService.getActiveStaffByRole(role);
        return ResponseEntity.ok(staff);
    }
    
    @PostMapping
    public ResponseEntity<Staff> createStaff(@RequestBody Staff staff) {
        try {
            Staff createdStaff = staffService.createStaff(staff);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdStaff);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Staff> updateStaff(@PathVariable UUID id, @RequestBody Staff staff) {
        Staff updatedStaff = staffService.updateStaff(id, staff);
        if (updatedStaff != null) {
            return ResponseEntity.ok(updatedStaff);
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStaff(@PathVariable UUID id) {
        boolean deleted = staffService.deleteStaff(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
