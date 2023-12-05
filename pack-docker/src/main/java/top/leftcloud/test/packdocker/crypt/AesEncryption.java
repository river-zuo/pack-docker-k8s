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

    public static void main(String[] args) throws UnknownHostException {
        String content = "https://192.168.30.77/api/anbao/v1/organization_prepare/security_objectives/company?mirrorParam=KTDffMoB9HqDlGxNvx9Nr6fuC4dMoE87UD9ymJeAaTmMoOeNEVEerOdvpBfZ+CZC7T9oh8kroieCXLyXFIva4XgnqJ5TXmQ0dHDDMLQnDbY=";

        content = "\thttps://10.50.2.88/api/anbao/v1/organization_prepare/security_objectives/company?mirrorParam=zOVCCFzTtHGxZrOzWfNdu3mDVOzAYkUlcbhF1zNNt97WCsaVIcx0V4Expile1V1+lSPQc45Xj/2QtDqqMQ2j1ERdww3vy64r42LXoqxHv/c=";

        content = "Kky1Qxd5dHBSU79esMRuNXAqWp4XHlUnRJCbB53JuWkCjhhXHKmcnz3m47WtXC7erhF8DfUFgOTkdsAcX3E+yzXZJeTPsFyWoGys4vpRaImF5OYTAX8xeDj+86fw3OlKk0y1yr4Aj6oAGLwczRvGwjvoJ0tJOxn+hpWXGjIOr9XS3ZTIyEZwRi1WOLAxbxH+zAyoTrX/Y4k/la75YKlunWR9IimwZWWXsBrlghJhETQ=";
        content = "zOVCCFzTtHGxZrOzWfNdu85Hfq+Gk/+/UxNwj6aJ0sk=";
        content = "https://10.50.2.88/api/anbao/v1/guard/center/guard/stat?mirrorParam=e2HVnmouMdHT6iGgodNFc2akzt99QEzcqbZKQ54i14GmEq4/hv+R44JZq+Vp/N52";
        content = "https://10.50.2.88/api/anbao/v1/activity?mirrorParam=Zu47Q9+pfYx/y7QW5YmofA==";
        content = "mirrorParam=k96dx9UD4DfK65jstyLmttoy2307aAh+WbYY0DZCV1CLXZZ+skZdYtneNJB4aySl";
//        content = "https://192.168.30.77/api/alarm/retrieval/detail?mirrorParam=DKKLhiBE4I1IxVvsWPreg1PFuP6nquFZ2VmBFw1Edr7tonqJvh6Ga8lOt2/2MGGwb63uf9WjjxOCsADRc9f//MX6Zamo/kBUOrpAloHCNPqQVtmcPF+CJSQCCfUZlusKDt7aa/Bj8G7wzloeoLeR+g==";

        String decrypt = processCrypt(content);
        System.out.println(decrypt);

        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        System.out.println(hostAddress);

        String f = "package-info.java";
        boolean validFilename = isValidFilename(f);
        System.out.println(validFilename);
    }

    public static String processCrypt(String content) {
        content = content.trim();
        if (content.contains("?")) {
            content = content.substring(content.indexOf("?") + 1);
        }
        content = content.replace("mirrorParam=", "");
        String secret = padding("1");
        String decrypt = decrypt(content, secret);
        log.info("content:[{}],decrypt:[{}]", content, decrypt);
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
