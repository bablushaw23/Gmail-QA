package com.bablu.gmailqa.controller;

import com.bablu.gmailqa.service.GmailService;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePartHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/gmail")
public class GmailController {
    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    private GmailService gmailService;

    @GetMapping("/emails")
    public List<Map<String, String>> getEmails(OAuth2AuthenticationToken auth) throws Exception {
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                auth.getAuthorizedClientRegistrationId(), auth.getName());

        String accessToken = client.getAccessToken().getTokenValue();

        Gmail gmail = gmailService.getGmailClient(accessToken);
        List<Message> messages = gmailService.listMessages(gmail, "me");

        List<Map<String, String>> emailSummaries = new ArrayList<>();

        for (Message m : messages) {
            Message fullMessage = gmailService.getMessage(gmail, "me", m.getId());

            String subject = "", from = "", snippet = fullMessage.getSnippet();

            for (MessagePartHeader header : fullMessage.getPayload().getHeaders()) {
                if (header.getName().equalsIgnoreCase("Subject")) {
                    subject = header.getValue();
                }
                if (header.getName().equalsIgnoreCase("From")) {
                    from = header.getValue();
                }
            }

            Map<String, String> email = new HashMap<>();
            email.put("id", m.getId());
            email.put("subject", subject);
            email.put("from", from);
            email.put("snippet", snippet);

            emailSummaries.add(email);
        }

        return emailSummaries;
    }

}
