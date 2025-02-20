package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import java.security.KeyStore;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class PatternManager {
    private static final String PREFS_NAME = "PatternLock";
    private static final String KEY_PATTERN = "encrypted_pattern";
    private SharedPreferences prefs;
    private Context context;

    public PatternManager(Context context) {
        this.context = context.getApplicationContext();
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // 保存加密后的图案
    public void savePattern(String pattern) {
        try {
            String encrypted = encrypt(pattern);
            prefs.edit().putString(KEY_PATTERN, encrypted).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 验证图案
    public boolean verifyPattern(String inputPattern) {
        try {
            String saved = prefs.getString(KEY_PATTERN, "");
            return decrypt(saved).equals(inputPattern);
        } catch (Exception e) {
            return false;
        }
    }

    // 检查是否已设置图案
    public boolean isPatternSet() {
        return prefs.contains(KEY_PATTERN);
    }

    // 加密（使用Android Keystore）
    private String encrypt(String plaintext) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        if (!keyStore.containsAlias("pattern_key")) {
            generateKey();
        }

        SecretKey secretKey = (SecretKey) keyStore.getKey("pattern_key", null);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] iv = cipher.getIV();
        byte[] encrypted = cipher.doFinal(plaintext.getBytes());

        return Base64.encodeToString(iv, Base64.DEFAULT) + ":" +
                Base64.encodeToString(encrypted, Base64.DEFAULT);
    }

    // 解密
    private String decrypt(String ciphertext) throws Exception {
        String[] parts = ciphertext.split(":");
        byte[] iv = Base64.decode(parts[0], Base64.DEFAULT);
        byte[] encrypted = Base64.decode(parts[1], Base64.DEFAULT);

        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        SecretKey secretKey = (SecretKey) keyStore.getKey("pattern_key", null);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
        byte[] decrypted = cipher.doFinal(encrypted);
        return new String(decrypted);
    }

    // 生成加密密钥
    private void generateKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore"
        );
        keyGenerator.init(new KeyGenParameterSpec.Builder(
                "pattern_key",
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setUserAuthenticationRequired(false)
                .build());
        keyGenerator.generateKey();
    }
}
