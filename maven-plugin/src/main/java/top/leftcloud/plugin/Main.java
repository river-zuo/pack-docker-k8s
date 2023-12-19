package top.leftcloud.plugin;

import org.codehaus.plexus.util.Base64;

public class Main {

    public static void main(String[] args) {

//        org.codehaus.plexus.util.Base64
        Base64 base64 = new Base64();
        String s = base64.toString();
        System.out.println(s);

    }
}
