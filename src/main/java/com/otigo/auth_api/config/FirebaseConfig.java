package com.otigo.auth_api.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    @Value("${FIREBASE_PROJECT_ID}")
    private String projectId;

    @Value("${FIREBASE_CLIENT_EMAIL}")
    private String clientEmail;

    @Value("${FIREBASE_PRIVATE_KEY}")
    private String privateKey;

    @Value("${FIREBASE_PRIVATE_KEY_ID}")
    private String privateKeyId;

    @Value("${FIREBASE_CLIENT_ID}")
    private String clientId;

    @PostConstruct
    public void initialize() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            String formattedKey = privateKey.replace("\\n", "\n");

            String json = "{\n" +
                    "  \"type\": \"service_account\",\n" +
                    "  \"project_id\": \"" + projectId + "\",\n" +
                    "  \"private_key_id\": \"" + privateKeyId + "\",\n" +
                    "  \"private_key\": \"" + formattedKey + "\",\n" +
                    "  \"client_email\": \"" + clientEmail + "\",\n" +
                    "  \"client_id\": \"" + clientId + "\",\n" +
                    "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                    "  \"token_uri\": \"https://oauth2.googleapis.com/token\"\n" +
                    "}";

            GoogleCredentials credentials = GoogleCredentials
                    .fromStream(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .setProjectId(projectId)
                    .build();

            FirebaseApp.initializeApp(options);
        }
    }
}