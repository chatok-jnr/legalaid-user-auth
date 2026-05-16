package com.legalaid.userauth.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldReturnPayloadTooLargeForOversizedUpload() {
        var problem = handler.handleMaxUploadSizeExceeded(new MaxUploadSizeExceededException(10 * 1024 * 1024));

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.PAYLOAD_TOO_LARGE.value());
        assertThat(problem.getDetail()).isEqualTo("File size must not exceed 10MB");
    }
}
