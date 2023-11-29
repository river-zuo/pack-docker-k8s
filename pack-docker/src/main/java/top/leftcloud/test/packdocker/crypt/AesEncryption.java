package top.leftcloud.test.packdocker.crypt;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
public class AesEncryption {

    private static final String AES_KEY = "mysecretkey12345"; // 密钥，长度必须为 16、24 或 32 个字符

//    public static final String ALGRORI = "AES/ECB/PKCS5Padding";

    public static final String ALGRORI = "AES/CBC/PKCS5Padding";

    public static final String IV_PARAM = "9757543832500935";

//    public static void main(String[] args) {
//        String plaintext = "Hello, world!"; // 明文
//        String encrypted = encrypt(plaintext); // 加密
//        String decrypted = decrypt(encrypted); // 解密
//        System.out.println("明文：" + plaintext);
//        System.out.println("加密后：" + encrypted);
//        System.out.println("解密后：" + decrypted);
//    }



    public static String processCrypt(String content) {
        content = content.trim();
        if (content.contains("?")) {
            content = content.substring(content.indexOf("?") + 1);
        }
        content = content.replace("mirrorParam=", "");
        String secret = padding("1");
        String decrypt = decrypt(content, secret);
        log.info("content:[{}],decrypt:[{}]", content, secret);
        return decrypt;
    }

    static boolean isValidFilename(String filename) {
        String regex = "^[^\\\\/:*?\"<>|]*$"; // 合法文件名的正则表达式
        return filename.matches(regex);
    }

    static String padding(String val) {
        if (18 - val.length() > 0) {
            int len = 16 - val.length();
            for (int i = 0; i < len; i++) {
                val += " ";
            }
        }
        return val;
    }


    // 加密
    public static String encrypt(String plaintext, String secret) {
        try {
            SecretKeySpec key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance(ALGRORI);
            IvParameterSpec iv = new IvParameterSpec(IV_PARAM.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(ciphertext);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 解密
    public static String decrypt(String encrypted, String secret) {
        try {
            SecretKeySpec key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance(ALGRORI);
            IvParameterSpec iv = new IvParameterSpec(IV_PARAM.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            byte[] ciphertext = Base64.getDecoder().decode(encrypted);
            byte[] plaintext = cipher.doFinal(ciphertext);
            return new String(plaintext, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
