package com.vulpix.api.dto.googleauth;

import jakarta.validation.constraints.Email;
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
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String otp;
    private String secretKey;
    private String dispositivoCode;
}