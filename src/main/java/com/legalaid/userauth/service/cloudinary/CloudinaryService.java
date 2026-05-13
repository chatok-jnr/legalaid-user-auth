package com.legalaid.userauth.service.cloudinary;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {
    String uploadDocument(MultipartFile document);
}

