package com.vulpix.api.config.security.googleauth;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.springframework.stereotype.Service;

@Service
public class GoogleAuthService {

    private final GoogleAuthenticator googleAuthenticator;
    private final GoogleAuthenticatorKey key;

    public GoogleAuthService() {
        googleAuthenticator = new GoogleAuthenticator();
        this.key = googleAuthenticator.createCredentials();
    }

    public String getSecret() {
        return key.getKey();
    }

    public int generateOTP() {
        long timestamp = System.currentTimeMillis();
        return googleAuthenticator.getTotpPassword(key.getKey(), timestamp);
    }

    public boolean validateOTP(String otp) {
        return googleAuthenticator.authorize(key.getKey(), Integer.parseInt(otp));
    }
}
