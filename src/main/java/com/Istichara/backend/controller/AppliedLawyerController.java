package com.Istichara.backend.controller;

import com.Istichara.backend.service.AppliedLawyerService;
import com.Istichara.backend.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/appliedlawyers")
@CrossOrigin("*")
public class AppliedLawyerController {

    private static final Logger logger = LoggerFactory.getLogger(AppliedLawyerController.class);

    @Autowired
    private AppliedLawyerService appliedLawyerService;

    @Autowired
    private EmailService emailService;

    @GetMapping
    public List<Map<String, Object>> getAllAppliedLawyers() {
        return appliedLawyerService.getAllAppliedLawyersWithIds();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLawyer(@PathVariable String id) {
        boolean result = appliedLawyerService.deleteAppliedLawyer(id);
        if (result) {
            return ResponseEntity.ok("removed successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to remove lawyer");
        }
    }

    @PostMapping("/reject/{id}")
    public ResponseEntity<String> rejectLawyer(@PathVariable String id, @RequestBody Map<String, Object> lawyerData) {
        String email = (String) lawyerData.get("Email");
        logger.info("Rejecting lawyer with ID: {} and email: {}", id, email);
        appliedLawyerService.rejectLawyer(id, email);
        return ResponseEntity.ok("Rejection email sent successfully");
    }

    @PostMapping("/approve/{id}")
    public ResponseEntity<String> approveLawyer(@PathVariable String id, @RequestBody Map<String, Object> lawyerData) {
        try {
            // Extract data from the lawyerData map
            String email = (String) lawyerData.get("email"); // Ensure the key name matches the JSON payload
            String lastName = (String) lawyerData.get("lastName");

            // Check if they are not null or empty
            if (email == null || email.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email is required");
            }
            if (lastName == null || lastName.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Last name is required");
            }

            // Create a new user in Firebase Auth
            String uid = appliedLawyerService.createUser(email, lastName);

            // Prepare data for ActiveLawyers collection
            Map<String, Object> activeLawyerData = new HashMap<>();
            activeLawyerData.put("description", "");
            activeLawyerData.put("image", lawyerData.get("image"));
            activeLawyerData.put("isAvailable", true);
            activeLawyerData.put("location", lawyerData.get("city"));
            activeLawyerData.put("name", lawyerData.get("firstName"));
            activeLawyerData.put("reviews", 0);

            // Add the lawyer to the ActiveLawyers collection with  UID
            boolean result = appliedLawyerService.addActiveLawyer(uid, activeLawyerData);

            if (result) {
                // Delete the lawyer from the AppliedLawyers collection
                boolean deleteResult = appliedLawyerService.deleteAppliedLawyer(id);
                if (deleteResult) {
                    return ResponseEntity.ok(" approved and removed successfully");
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed ");
                }
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed");
            }
        } catch (Exception e) {
            //logger.error("Error occurred during approval", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error ");
        }
    }



    //endpoint test
    @GetMapping("/test-email")
    public ResponseEntity<String> testEmail() {
        String to = "fouajou035@gmail.com";
        String subject = "Test Email";
        String text = "This is a test email v2.";
        emailService.sendSimpleMessage(to, subject, text);
        return ResponseEntity.ok("Test email sent successfully");
    }

}
