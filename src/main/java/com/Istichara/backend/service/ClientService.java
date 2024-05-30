package com.Istichara.backend.service;


import com.Istichara.backend.model.Client;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class ClientService {


    public List<Client> getAllClients() {
       Firestore firestore = FirestoreClient.getFirestore();

        ApiFuture<QuerySnapshot> query = firestore.collection("Clients").get();
        try {
            return query.get().toObjects(Client.class);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }
}