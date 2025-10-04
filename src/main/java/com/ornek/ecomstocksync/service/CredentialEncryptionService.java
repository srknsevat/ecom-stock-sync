package com.ornek.ecomstocksync.service;

public interface CredentialEncryptionService {
    
    /**
     * Şifreler bir credential değerini
     * @param plainText Şifrelenecek metin
     * @return Şifrelenmiş metin (Base64 encoded)
     */
    String encrypt(String plainText);
    
    /**
     * Şifresini çözer bir credential değerini
     * @param encryptedText Şifrelenmiş metin (Base64 encoded)
     * @return Orijinal metin
     */
    String decrypt(String encryptedText);
    
    /**
     * Şifreleme anahtarını ayarlar
     * @param key Base64 encoded anahtar
     */
    void setEncryptionKey(String key);
    
    /**
     * Mevcut şifreleme anahtarını döndürür
     * @return Base64 encoded anahtar
     */
    String getEncryptionKey();
    
    /**
     * Yeni bir şifreleme anahtarı oluşturur
     * @return Base64 encoded yeni anahtar
     */
    String generateNewKey();
}
