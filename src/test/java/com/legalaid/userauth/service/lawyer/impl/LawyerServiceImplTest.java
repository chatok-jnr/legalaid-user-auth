package com.legalaid.userauth.service.lawyer.impl;

import com.legalaid.userauth.dto.request.lawyer.LawyerRequest;
import com.legalaid.userauth.entity.User;
import com.legalaid.userauth.entity.lawyer.LawyerProfile;
import com.legalaid.userauth.exception.AuthExceptions;
import com.legalaid.userauth.repository.RoleRepository;
import com.legalaid.userauth.repository.UserRepository;
import com.legalaid.userauth.repository.UserRoleRepository;
import com.legalaid.userauth.repository.lawyer.LawyerRepository;
import com.legalaid.userauth.service.cloudinary.CloudinaryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("LawyerServiceImpl")
class LawyerServiceImplTest {

    @Mock LawyerRepository lawyerRepository;
    @Mock UserRepository userRepository;
    @Mock RoleRepository roleRepository;
    @Mock UserRoleRepository userRoleRepository;
    @Mock CloudinaryService cloudinaryService;

    @InjectMocks LawyerServiceImpl lawyerService;

    private User lawyerUser;
    private LawyerProfile lawyerProfile;

    @BeforeEach
    void setUp() {
        lawyerUser = User.builder()
                .id(UUID.randomUUID())
                .fullName("Jane Lawyer")
                .username("jane.lawyer")
                .email("lawyer@example.com")
                .roles(Set.of())
                .isActive(true)
                .isVisible(true)
                .build();

        lawyerProfile = LawyerProfile.builder()
                .user(lawyerUser)
                .barNumber("BAR-12345")
                .bio("Experienced attorney")
                .yearsExperience((short) 10)
                .build();
    }

    @Test
    @DisplayName("should upload a profile document and return its URL")
    void shouldUploadProfileDocument() {
        var file = new MockMultipartFile("document", "id.pdf", "application/pdf", "content".getBytes());

        when(userRepository.findByEmail("lawyer@example.com")).thenReturn(Optional.of(lawyerUser));
        when(lawyerRepository.findById(lawyerUser.getId())).thenReturn(Optional.of(lawyerProfile));
        when(cloudinaryService.uploadDocument(any())).thenReturn("https://res.cloudinary.com/demo/id.pdf");

        var response = lawyerService.uploadProfileDocument(file, "lawyer@example.com");

        assertThat(response.getDocumentUrl()).isEqualTo("https://res.cloudinary.com/demo/id.pdf");
        verify(cloudinaryService).uploadDocument(eq(file));
    }

    @Test
    @DisplayName("should reject missing document uploads")
    void shouldRejectEmptyUpload() {
        assertThatThrownBy(() -> lawyerService.uploadProfileDocument(null, "lawyer@example.com"))
                .isInstanceOf(AuthExceptions.InvalidUploadException.class);
        verify(cloudinaryService, never()).uploadDocument(any());
    }

    @Test
    @DisplayName("should reject non-lawyer users")
    void shouldRejectMissingLawyerProfile() {
        var file = new MockMultipartFile("document", "id.pdf", "application/pdf", "content".getBytes());
        when(userRepository.findByEmail("lawyer@example.com")).thenReturn(Optional.of(lawyerUser));
        when(lawyerRepository.findById(lawyerUser.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> lawyerService.uploadProfileDocument(file, "lawyer@example.com"))
                .isInstanceOf(AuthExceptions.LawyerNotFoundException.class);
        verify(cloudinaryService, never()).uploadDocument(any());
    }
}

