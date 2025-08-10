package com.beautysalon.service;

import com.beautysalon.model.Staff;
import com.beautysalon.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StaffService {
    
    @Autowired
    private StaffRepository staffRepository;
    
    public List<Staff> getAllStaff() {
        return staffRepository.findAll();
    }
    
    public List<Staff> getActiveStaff() {
        return staffRepository.findActiveStaff();
    }
    
    public Optional<Staff> getStaffById(UUID id) {
        return staffRepository.findById(id);
    }
    
    public List<Staff> getStaffByRole(String role) {
        return staffRepository.findByRole(role);
    }
    
    public List<Staff> getActiveStaffByRole(String role) {
        return staffRepository.findActiveByRole(role);
    }
    
    public Staff createStaff(Staff staff) {
        staff.setCreatedAt(Instant.now());
        staff.setUpdatedAt(Instant.now());
        return staffRepository.save(staff);
    }
    
    public Staff updateStaff(UUID id, Staff staffDetails) {
        Optional<Staff> optionalStaff = staffRepository.findById(id);
        if (optionalStaff.isPresent()) {
            Staff staff = optionalStaff.get();
            staff.setName(staffDetails.getName());
            staff.setEmail(staffDetails.getEmail());
            staff.setPhone(staffDetails.getPhone());
            staff.setRole(staffDetails.getRole());
            staff.setSpecialties(staffDetails.getSpecialties());
            staff.setIsActive(staffDetails.getIsActive());
            staff.setUpdatedAt(Instant.now());
            return staffRepository.save(staff);
        }
        return null;
    }
    
    public boolean deleteStaff(UUID id) {
        if (staffRepository.existsById(id)) {
            staffRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
