package com.bablu.gmailqa.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@Service
public class GmailService {

    public Gmail getGmailClient(String accessToken) throws GeneralSecurityException, IOException {
        GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);

        return new Gmail.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                credential)
                .setApplicationName("GmailQA") // your app name
                .build();
    }

    public List<Message> listMessages(Gmail gmail, String userId) throws IOException {
        return gmail.users().messages().list(userId).setMaxResults(10L).execute().getMessages();
    }

    public Message getMessage(Gmail gmail, String userId, String messageId) throws IOException {
        return gmail.users().messages().get(userId, messageId).execute();
    }
}

