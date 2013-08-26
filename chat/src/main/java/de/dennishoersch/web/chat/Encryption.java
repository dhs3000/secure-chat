/*
 * Copyright 2012-2013 Dennis Hörsch.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.dennishoersch.web.chat;

import java.net.URL;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;

import de.dennishoersch.lang.Throwables;
import de.dennishoersch.web.js.Function;
import de.dennishoersch.web.js.ScriptCompiler;
import de.dennishoersch.web.util.ResourceResolver;

/**
 * @author hoersch
 * 
 */
public class Encryption {

    private static final int _AES_KEY_LENGTH = 256;
    private static final int _RSA_KEY_LENGTH = 1024;
    private static final Integer _RSA_PASSPHRASE_BYTES = Integer.valueOf(_RSA_KEY_LENGTH / 8 / 2); // Maximal bits / 8 Bytes können verschlüsselt
    // werden.(UTF-8 = 2 byte)

    private final Function _generatePassphrase;
    private final Function _getPublicKey;
    private final Function _decryptAES;
    private final Function _encryptAES;
    private final Function _encryptRSA;

    public Encryption(ResourceResolver resourceResolver) {
        ScriptCompiler compiler = new ScriptCompiler(libs(resourceResolver));

        _generatePassphrase = compiler.compile("function(length) { return PassphraseGenerator.generate(length); }");
        _getPublicKey = compiler.compile("function(publicKey) { var crypt = new JSEncrypt(); crypt.setPublicKey(publicKey); return crypt.getPublicKey(); }");
        _decryptAES = compiler.compile("function(key, ciphertext) { return Aes.Ctr.decrypt(ciphertext, key, " + _AES_KEY_LENGTH + "); }");
        _encryptAES = compiler.compile("function(key, text) { return Aes.Ctr.encrypt(text, key, " + _AES_KEY_LENGTH + "); }");
        _encryptRSA = compiler.compile("function(publicKey, text) { var crypt = new JSEncrypt(); crypt.setPublicKey(publicKey); return crypt.encrypt(text); }");
    }

    private List<URL> libs(ResourceResolver resourceResolver) {
        List<URL> result = new ArrayList<>();

        result.add(resourceResolver.resolve("/js/lib/env.rhino.js"));
        result.add(resourceResolver.resolve("/js/lib/aes.js"));
        result.add(resourceResolver.resolve("/js/lib/jsencrypt.js"));
        result.add(resourceResolver.resolve("/js/D.js"));
        result.add(resourceResolver.resolve("/js/PassphraseGenerator.js"));
        return result;
    }

    public String generatePassphrase() {
        String result = (String) _generatePassphrase.call(_RSA_PASSPHRASE_BYTES);
        return result;
    }

    public KeyPair generateRSAKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(1024);
            return keyGen.genKeyPair();

        } catch (NoSuchAlgorithmException e) {
            throw Throwables.throwUnchecked(e);
        }
    }

    /**
     * @param keyPair
     * @return the public key
     */
    public String transformToPublicKey(KeyPair keyPair) {
        String key = new String(Base64.encodeBase64(keyPair.getPublic().getEncoded()));

        return (String) _getPublicKey.call(key);
    }

    /**
     * @param publicKey
     * @param text
     * @return encrypted text
     */
    public String encryptRSA(String publicKey, String text) {
        return (String) _encryptRSA.call(publicKey, text);
    }

    /**
     * @param keyPair
     * @param encrypted
     * @return decrypted text
     */
    public String decryptRSA(KeyPair keyPair, String encrypted) {
        byte[] encrypted_ = Base64.decodeBase64(encrypted.getBytes());

        return decryptRSA(keyPair.getPrivate(), encrypted_);
    }

    private static String decryptRSA(Key key, byte[] input) {
        try {
            Cipher rsa;
            rsa = Cipher.getInstance("RSA");
            rsa.init(Cipher.DECRYPT_MODE, key);
            byte[] utf8 = rsa.doFinal(input);
            return new String(utf8, "UTF8");
        } catch (Exception e) {
            throw Throwables.throwUnchecked(e);
        }
    }

    /**
     * @param key
     * @param input
     * @return decrypted with AES algorithm
     */
    public String decryptAES(String key, String input) {
        Object result = _decryptAES.call(key, input);
        return (String) result;
    }

    /**
     * @param key
     * @param input
     * @return encrypted with AES
     */
    public String encryptAES(String key, String input) {
        Object result = _encryptAES.call(key, input);
        return (String) result;
    }
}
