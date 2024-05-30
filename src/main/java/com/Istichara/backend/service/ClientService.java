package com.Istichara.backend.service;

import com.Istichara.backend.model.Client;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class ClientService {

    public List<Client> getAllClients() {
        Firestore firestore = FirestoreClient.getFirestore();

        ApiFuture<QuerySnapshot> query = firestore.collection("Clients").get();

        try {
            List<QueryDocumentSnapshot> documents = query.get().getDocuments();

            return documents.stream().map(doc -> {
                Client client = doc.toObject(Client.class);
                if (client != null) {
                    client.setCreatedAt(LocalDateTime.ofInstant(Instant.ofEpochMilli(doc.getCreateTime().toDate().getTime()), ZoneId.systemDefault()));
                }
                return client;
            }).collect(Collectors.toList());
        } catch (Exception e ) {
            e.printStackTrace();
            return null;
        }
    }
}
