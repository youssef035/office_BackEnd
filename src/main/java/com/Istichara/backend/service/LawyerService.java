package com.Istichara.backend.service;


import com.Istichara.backend.model.Lawyer;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class LawyerService {

    public List<Lawyer> getAllActiveLawyers() throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        ApiFuture<QuerySnapshot> query = db.collection("ActiveLawyers").get();//fetching
        QuerySnapshot querySnapshot = query.get();                  //getting result fetched

        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();

        List<Lawyer> lawyers = new ArrayList<>();

        for (QueryDocumentSnapshot document : documents) {
            Lawyer lawyer = document.toObject(Lawyer.class);    //convert into lwayer object
            lawyers.add(lawyer);
        }
        return lawyers;
    }

    public void createLawyer(String email, String password, String firstName, String lastName, String location, String image) throws Exception {
        // Create Firebase Auth user
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(email)
                .setPassword(password)
                .setDisplayName(firstName + " " + lastName);

        UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);

        String uid = userRecord.getUid(); // getting UID of the created one

        // Create Firestore document
        Map<String, Object> lawyerData = new HashMap<>();
        lawyerData.put("description", "");
        lawyerData.put("image", image);
        lawyerData.put("isAvailable", true);
        lawyerData.put("location", location);
        lawyerData.put("name", firstName);
        lawyerData.put("reviews", 0);

        // Add to Firestore with UID as the document ID
        FirestoreClient.getFirestore().collection("ActiveLawyers").document(uid).set(lawyerData);

    }
}
