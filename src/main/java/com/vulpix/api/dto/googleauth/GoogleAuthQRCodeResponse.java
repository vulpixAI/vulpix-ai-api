package com.vulpix.api.dto.googleauth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GoogleAuthQRCodeResponse {
    private String qrcodeBase64;
}