package com.rezende.notification_service.configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;

@Slf4j
@Configuration
public class FirebaseConfig {

    @Value("${firebase.config.service-account}")
    private String serviceAccountPath;

    @PostConstruct
    public void initFirebase() {
        try (FileInputStream serviceAccount = new FileInputStream(serviceAccountPath)) {

            final FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if(FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("Firebase inicializado com sucesso. Arquivo: {}", serviceAccountPath);
            }
        } catch (IOException e) {
            log.error("Erro ao inicializar o Firebase com o arquivo: {}", serviceAccountPath, e);
        }
    }
}
