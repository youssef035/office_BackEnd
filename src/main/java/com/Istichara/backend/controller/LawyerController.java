package com.Istichara.backend.controller;


import com.Istichara.backend.model.Lawyer;
import com.Istichara.backend.service.LawyerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@CrossOrigin("*")
public class LawyerController {

    @Autowired
    private LawyerService lawyerService;


    @GetMapping("/lawyers")
    public List<Lawyer> getAllLawyers() throws ExecutionException, InterruptedException {
        return lawyerService.getAllActiveLawyers();
    }


    @PostMapping("/approve")
    public void approveLawyer(@RequestBody Map<String, String> lawyerData) {
        String email = lawyerData.get("email");
        String firstName = lawyerData.get("firstName");
        String lastName = lawyerData.get("lastName");
        String location = lawyerData.get("location");
        String image = lawyerData.get("image");
        try {
            lawyerService.createLawyer(email, lastName, firstName, lastName, location, image);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
