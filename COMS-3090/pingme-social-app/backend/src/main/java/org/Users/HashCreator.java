package org.Users;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Component;

@Component
public class HashCreator {

    // maybe make this a static method?

    /**
     * creates a hash from given text
     * @param input
     * @return hashed String
     * @throws NoSuchAlgorithmException
     */
    public static String createSHAHash(String input) throws NoSuchAlgorithmException {

        try {
            String hashtext = null;
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] messageDigest =
                    md.digest(input.getBytes(StandardCharsets.UTF_8));

            hashtext = convertToHex(messageDigest);
            return hashtext;
        } catch(Exception e)
        {
            // just to stop errors
        }


        return "SHOULD NOT RETURN THIS";
    }

    /**
     * converts given bytes into hexidecimal
     */
    private static String convertToHex(final byte[] messageDigest) {
        BigInteger bigint = new BigInteger(1, messageDigest);
        String hexText = bigint.toString(16);
        while (hexText.length() < 32) {
            hexText = "0".concat(hexText);
        }
        return hexText;
    }
}
