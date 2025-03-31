package com.vulpix.api.dto.googleauth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoogleAuthQRCodeResponse {
    private String secretKey;
    private String qrcodeBase64;
}