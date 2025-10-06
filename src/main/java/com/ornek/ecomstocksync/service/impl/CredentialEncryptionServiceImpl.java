package com.ornek.ecomstocksync.service.impl;

import com.ornek.ecomstocksync.service.CredentialEncryptionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class CredentialEncryptionServiceImpl implements CredentialEncryptionService {
    
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private static final int KEY_LENGTH = 256;
    
    private SecretKey secretKey;
    
    @Value("${app.encryption.key:}")
    private String encryptionKeyFromConfig;
    
    public CredentialEncryptionServiceImpl() {
        initializeKey();
    }
    
    private void initializeKey() {
        try {
            if (encryptionKeyFromConfig != null && !encryptionKeyFromConfig.isEmpty()) {
                // Konfigürasyondan anahtar yükle
                byte[] keyBytes = Base64.getDecoder().decode(encryptionKeyFromConfig);
                this.secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
            } else {
                // Yeni anahtar oluştur
                generateNewKey();
            }
        } catch (Exception e) {
            // Hata durumunda yeni anahtar oluştur
            generateNewKey();
        }
    }
    
    @Override
    public String encrypt(String plainText) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Şifreleme hatası: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String decrypt(String encryptedText) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Şifre çözme hatası: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void setEncryptionKey(String key) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(key);
            this.secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
        } catch (Exception e) {
            throw new RuntimeException("Geçersiz şifreleme anahtarı: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String getEncryptionKey() {
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }
    
    @Override
    public String generateNewKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            keyGenerator.init(KEY_LENGTH, new SecureRandom());
            this.secretKey = keyGenerator.generateKey();
            return getEncryptionKey();
        } catch (Exception e) {
            throw new RuntimeException("Anahtar oluşturma hatası: " + e.getMessage(), e);
        }
    }
}
