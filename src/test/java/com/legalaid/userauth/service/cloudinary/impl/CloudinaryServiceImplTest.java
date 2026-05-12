package com.legalaid.userauth.service.cloudinary.impl;

import com.legalaid.userauth.config.CloudinaryProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("CloudinaryServiceImpl")
class CloudinaryServiceImplTest {

    private HttpServer server;
    private String receivedBody;
    private final AtomicReference<Integer> status = new AtomicReference<>(200);

    private CloudinaryServiceImpl service;

    @BeforeEach
    void setUp() throws Exception {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/test-cloud/auto/upload", new Handler());
        server.start();

        int port = server.getAddress().getPort();
        CloudinaryProperties props = new CloudinaryProperties();
        props.setCloudName("test-cloud");
        props.setApiKey("api-key-123");
        props.setApiSecret("super-secret");
        props.setUploadFolder("Home/legalAid/lawyer-profile-documents");
        props.setUploadUrlTemplate(String.format("http://localhost:%d/%%s/auto/upload", port));

        service = new CloudinaryServiceImpl(props, java.net.http.HttpClient.newHttpClient(), new ObjectMapper());
        // override the template for the current port using reflection-free string formatting in the service call
        props.setUploadUrlTemplate(String.format("http://localhost:%d/%%s/auto/upload", port));
    }

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    @DisplayName("should upload a document and return secure URL")
    void shouldUploadDocument() {
        MultipartFile file = new MockMultipartFile(
                "document",
                "lawyer-id.pdf",
                "application/pdf",
                "dummy content".getBytes(StandardCharsets.UTF_8)
        );

        String url = service.uploadDocument(file);

        assertThat(url).isEqualTo("https://res.cloudinary.com/test-cloud/raw/upload/v1/lawyer-id.pdf");
        assertThat(receivedBody).contains("name=\"api_key\"");
        assertThat(receivedBody).contains("name=\"folder\"");
        assertThat(receivedBody).contains("name=\"signature\"");
        assertThat(receivedBody).contains("name=\"file\"; filename=\"lawyer-id.pdf\"");

        String timestamp = extract(receivedBody, "timestamp");
        String signature = extract(receivedBody, "signature");
        String expectedSignature = sha1Hex("folder=Home/legalAid/lawyer-profile-documents&timestamp=" + timestamp + "super-secret");
        assertThat(signature).isEqualTo(expectedSignature);
    }

    @Test
    @DisplayName("should reject empty files")
    void shouldRejectEmptyFile() {
        MultipartFile file = new MockMultipartFile("document", new byte[0]);

        assertThatThrownBy(() -> service.uploadDocument(file))
                .isInstanceOf(com.legalaid.userauth.exception.AuthExceptions.InvalidUploadException.class);
    }

    private String extract(String body, String field) {
        Matcher matcher = Pattern.compile("name=\"" + field + "\"\\r\\n\\r\\n([^\\r]+)\\r\\n").matcher(body);
        assertThat(matcher.find()).as("Expected field %s in multipart body", field).isTrue();
        return matcher.group(1);
    }

    private String sha1Hex(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private class Handler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try (InputStream in = exchange.getRequestBody()) {
                receivedBody = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            }
            String response = "{\"secure_url\":\"https://res.cloudinary.com/test-cloud/raw/upload/v1/lawyer-id.pdf\"}";
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(status.get(), response.getBytes(StandardCharsets.UTF_8).length);
            exchange.getResponseBody().write(response.getBytes(StandardCharsets.UTF_8));
            exchange.close();
        }
    }
}
