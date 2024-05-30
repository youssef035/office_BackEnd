package com.Istichara.backend.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AppliedLawyerService {


    @Autowired
    private EmailService emailService;

    public List<Map<String, Object>> getAllAppliedLawyersWithIds() {
        Firestore firestore = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> query = firestore.collection("AppliedLawyers").get();
        List<Map<String, Object>> lawyers = new ArrayList<>();

        try {
            List<QueryDocumentSnapshot> documents = query.get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                Map<String, Object> lawyer = document.getData();
                lawyer.put("id", document.getId());
                lawyers.add(lawyer);
            }
        } catch (Exception e) {
            return  null;
        }
        return lawyers;
    }

    public boolean deleteAppliedLawyer(String id) {
        Firestore firestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> writeResult = firestore.collection("AppliedLawyers").document(id).delete();
        try {
            writeResult.get();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void rejectLawyer(String id, String email) {
        boolean deleted = deleteAppliedLawyer(id);
        if (deleted) {
            String subject = "Application Rejection";
            String text = "We regret to inform you that your application has been rejected.";
            emailService.sendSimpleMessage(email, subject, text);
        }
    }

    public String createUser(String Email, String password) throws Exception {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(Email)
                .setPassword(password);

        UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
        return userRecord.getUid();
    }

    public boolean addActiveLawyer(String uid, Map<String, Object> lawyerData) {

        Firestore firestore = FirestoreClient.getFirestore();

        ApiFuture<WriteResult> writeResult = firestore.collection("ActiveLawyers").document(uid).set(lawyerData);
        try {
            writeResult.get();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
