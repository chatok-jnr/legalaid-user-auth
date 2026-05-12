package com.legalaid.userauth.controller;

import com.legalaid.userauth.config.SecurityConfig;
import com.legalaid.userauth.dto.response.lawyer.LawyerResponse;
import com.legalaid.userauth.exception.GlobalExceptionHandler;
import com.legalaid.userauth.security.JwtAuthenticationFilter;
import com.legalaid.userauth.service.lawyer.LawyerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LawyerController.class, properties = "server.servlet.context-path=")
@Import({SecurityConfig.class, GlobalExceptionHandler.class, LawyerControllerTest.TestSecurityConfig.class})
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class LawyerControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private LawyerService lawyerService;
    @SuppressWarnings("unused")
    @MockBean private JwtAuthenticationFilter jwtAuthenticationFilter;
    @SuppressWarnings("unused")
    }

    @Test
    @DisplayName("should upload a document for a lawyer role")
    void shouldUploadDocumentForLawyer() throws Exception {
        var response = LawyerResponse.DocumentUploadResponse.builder()
                .documentUrl("https://res.cloudinary.com/demo/id.pdf")
                .build();
        given(lawyerService.uploadProfileDocument(any(), eq("lawyer@example.com"))).willReturn(response);

        MockMultipartFile file = new MockMultipartFile(
                "document",
                "id.pdf",
                "application/pdf",
                "content".getBytes()
        );

        mockMvc.perform(multipart("/auth/lawyer/profile/document").file(file)
                        .with(user("lawyer@example.com").roles("LAWYER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.documentUrl").value("https://res.cloudinary.com/demo/id.pdf"));
    }

    @Test
    @DisplayName("should forbid non-lawyer roles")
    void shouldForbidClientRole() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "document",
                "id.pdf",
                "application/pdf",
                "content".getBytes()
        );

        mockMvc.perform(multipart("/auth/lawyer/profile/document").file(file)
                        .with(user("client@example.com").roles("CLIENT")))
                .andExpect(status().isForbidden());
    }
}
