package com.vulpix.api.dto.googleauth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoogleAuthOtpRequest {
    @NotBlank
    private String otp;
    private String secretKey;
}