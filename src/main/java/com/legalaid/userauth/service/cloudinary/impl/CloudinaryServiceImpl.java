package com.legalaid.userauth.service.cloudinary.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.legalaid.userauth.config.CloudinaryProperties;
import com.legalaid.userauth.exception.AuthExceptions;
import com.legalaid.userauth.exception.AuthExceptions.CloudinaryUploadException;
import com.legalaid.userauth.exception.AuthExceptions.InvalidUploadException;
import com.legalaid.userauth.service.cloudinary.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {

    private final CloudinaryProperties cloudinaryProperties;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Override
    public String uploadDocument(MultipartFile document) {
        if (document == null || document.isEmpty()) {
            throw new InvalidUploadException();
        }

        try {
            long timestamp = Instant.now().getEpochSecond();
            Map<String, String> signatureParams = new TreeMap<>();
            signatureParams.put("folder", cloudinaryProperties.getUploadFolder());
            signatureParams.put("timestamp", String.valueOf(timestamp));
            String signature = sign(signatureParams, cloudinaryProperties.getApiSecret());

            Map<String, String> fields = new LinkedHashMap<>();
            fields.put("api_key", cloudinaryProperties.getApiKey());
            fields.put("timestamp", String.valueOf(timestamp));
            fields.put("folder", cloudinaryProperties.getUploadFolder());
            fields.put("signature", signature);

            String boundary = "----LegalAidBoundary" + UUID.randomUUID();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(String.format(cloudinaryProperties.getUploadUrlTemplate(), cloudinaryProperties.getCloudName())))
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .POST(ofMimeMultipartData(boundary, fields, document))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new CloudinaryUploadException("Cloudinary upload failed with HTTP status " + response.statusCode());
            }

            JsonNode json = objectMapper.readTree(response.body());
            JsonNode secureUrl = json.get("secure_url");
            if (secureUrl == null || secureUrl.asText().isBlank()) {
                throw new CloudinaryUploadException("Cloudinary response did not include a secure URL");
            }
            return secureUrl.asText();
        } catch (IOException e) {
            throw new CloudinaryUploadException("Unable to upload document: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CloudinaryUploadException("Cloudinary upload was interrupted");
        }
    }

    private String sign(Map<String, String> params, String apiSecret) {
        String payload = params.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));
        return sha1Hex(payload + apiSecret);
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
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-1 algorithm is required", e);
        }
    }

    private HttpRequest.BodyPublisher ofMimeMultipartData(
            String boundary,
            Map<String, String> fields,
            MultipartFile document
    ) throws IOException {
        var byteArrays = new java.util.ArrayList<byte[]>();
        byte[] separator = ("--" + boundary + "\r\nContent-Disposition: form-data; name=").getBytes(StandardCharsets.UTF_8);

        for (Map.Entry<String, String> entry : fields.entrySet()) {
            byteArrays.add(separator);
            byteArrays.add(("\"" + entry.getKey() + "\"\r\n\r\n" + entry.getValue() + "\r\n").getBytes(StandardCharsets.UTF_8));
        }

        byteArrays.add(("--" + boundary + "\r\nContent-Disposition: form-data; name=\"file\"; filename=\"" +
                safeFileName(document.getOriginalFilename()) + "\"\r\nContent-Type: " +
                (document.getContentType() == null ? MediaType.APPLICATION_OCTET_STREAM_VALUE : document.getContentType()) +
                "\r\n\r\n").getBytes(StandardCharsets.UTF_8));
        byteArrays.add(document.getBytes());
        byteArrays.add("\r\n".getBytes(StandardCharsets.UTF_8));
        byteArrays.add(("--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));

        return HttpRequest.BodyPublishers.ofByteArrays(byteArrays);
    }

    private String safeFileName(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            return "document";
        }
        return originalFilename.replaceAll("[\\r\\n\"]", "_");
    }
}
