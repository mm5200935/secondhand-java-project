package app.controller;

import app.service.interfaces.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PutMapping("/advertisements/{id}/approve")
    public ResponseEntity<String> approveAdvertisement(@PathVariable int id) {
        try {
            adminService.approveAdvertisement(id);
            return ResponseEntity.ok("Advertisement approved successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/advertisements/{id}/reject")
    public ResponseEntity<String> rejectAdvertisement(@PathVariable int id) {
        try {
            adminService.rejectAdvertisement(id);
            return ResponseEntity.ok("Advertisement rejected successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/advertisements/{id}")
    public ResponseEntity<String> deleteAdvertisement(@PathVariable int id) {
        try {
            adminService.deleteAdvertisement(id);
            return ResponseEntity.ok("Advertisement deleted successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/users/count")
    public ResponseEntity<Integer> getTotalUsers() {
        return ResponseEntity.ok(adminService.getTotalUsers());
    }

    @GetMapping("/advertisements/count")
    public ResponseEntity<Integer> getTotalAdvertisements() {
        return ResponseEntity.ok(adminService.getTotalAdvertisements());
    }

    @GetMapping("/advertisements/pending")
    public ResponseEntity<?> getPendingAdvertisements() {
        return ResponseEntity.ok(adminService.getPendingAdvertisements());
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }
}