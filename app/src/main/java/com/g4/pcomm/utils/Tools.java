package com.g4.pcomm.utils;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Talkamynn on 28/09/2017.
 *
 * Contains several tools for app
 */

public class Tools {

    private static final String LOG = "PComm - Tools";

    /**
     * Convert string from aXMLRPC response to HashMap structure.
     *
     * @param respString The string to convert.
     *
     * @return A {@code HashMap} of string Map.
     */
    public HashMap<String, HashMap<String, String>> stringToMap(String respString) {

        // Variables initialisation
        String[] respToElems;
        HashMap<String, HashMap<String, String>> result = new HashMap<String, HashMap<String, String>>();

        // String cleaning and first separation level (by entity)
        respToElems = respString.replace("[", "").replace("]", "").split("\\}, \\{");
        int i = 0;
        for (String str : respToElems) {
            if (i == 0) {
                result.put("status", new HashMap<String, String>());
            } else {
                result.put("entity_" + i, new HashMap<String, String>());
            }
            respToElems[i] = str.replace("{", "").replace("}", "");
            i++;
        }
        Log.d(LOG, "HashMap entities: " + result.toString());

        // Second separation level (by entity attributes) and preparing response
        int k = 0;
        for (String str : respToElems) {
            String[] tmp = str.split(", ");
            for (String attr : tmp) {
                String[] tmpAttr = {"", ""};
                if (attr.length() > 13) {
                    if (attr.substring(0, 13).equals("email_address")) {
                        tmpAttr[0] = attr.substring(0, 13);
                        tmpAttr[1] = attr.substring(14);
                    } else {
                        tmpAttr = attr.split("=");
                    }
                } else {
                    tmpAttr = attr.split("=");
                }
                Log.d(LOG, "Index: " + attr);
                if (k == 0) {
                    result.get("status").put(tmpAttr[0], tmpAttr[1]);
                } else {
                    result.get("entity_" + k).put(tmpAttr[0], tmpAttr[1]);
                }
            }
            k++;
        }
        Log.d(LOG, "Full entities: " + result.toString());
        return result;
    }

    /**
     * Hash a string to avoid security issue, especially for passwords manipulation.
     *
     * @param stringToHash The string to hash.
     *
     * @return hashedString The result of hash.
     */
    public String MD5(String stringToHash) {
        try {

            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(stringToHash.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; i++) {
                sb.append(Integer.toString((array[i] & 0xFF) | 0x100).substring(1,3));
            }

            return sb.toString();

        } catch (java.security.NoSuchAlgorithmException ex) {
            return null;
        }
    }

    /**
     * Convert clear password to SecretKey.
     *
     * @param password The password to modify.
     *
     * @return SecretKey The modified password.
     */
    public SecretKey generateKey(String password)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        return new SecretKeySpec(password.getBytes(), "AES");
    }

    /**
     * Encrypt any string or message.
     *
     * @param message The string to encrypt.
     * @param secret The secret key to process encryption.
     *
     * @return code The encrypted message, as byte array.
     */
    public byte[] encryptMsg(String message, SecretKey secret)
        throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidParameterSpecException, IllegalBlockSizeException, BadPaddingException,
            UnsupportedEncodingException {

        /* Encrypt the message */
        Cipher cipher = null;
        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secret);
        return cipher.doFinal(message.getBytes("UTF-8"));
    }

    /**
     * Decrypt encrypted string.
     *
     * @param cipherText The string to decrypt.
     * @param secret The secret key to process encryption.
     *
     * @return decryptString The decrypted message, as string.
     */
    public String decryptMsg(byte[] cipherText, SecretKey secret)
        throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidParameterSpecException,
            InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException,
            IllegalBlockSizeException, UnsupportedEncodingException {

        /* Decrypt the message, given derived encContentValue and initialization vector */
        Cipher cipher = null;
        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secret);
        return new String(cipher.doFinal(cipherText), "UTF-8");
    }
}
