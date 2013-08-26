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

package de.dennishoersch.web.websocket.chat;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Strings;

import de.dennishoersch.lang.Throwables;
import de.dennishoersch.web.chat.Encryption;
import de.dennishoersch.web.util.ResourceResolver;

/**
 * @author hoersch
 * 
 */
public class EncryptionTest {

    private Encryption encryption;

    @Before
    public void setUp() {
        final Path path = Paths.get("WebContent");
        encryption = new Encryption(new ResourceResolver() {
            @Override
            public URL resolve(String other) {
                try {
                    return path.toAbsolutePath().resolve(other).toUri().toURL();
                } catch (MalformedURLException e) {
                    throw Throwables.throwUnchecked(e);
                }
            }
        });
    }

    /**
     * Test method for {@link de.dennishoersch.web.chat.Encryption#generatePassphrase()}.
     */
    @Test
    public void testGeneratePassphrase() {
        String passphrase1 = encryption.generatePassphrase();

        assertThat(passphrase1, isNotNullOrEmptyString());
        assertThat(Integer.valueOf(passphrase1.length()), equalTo(Integer.valueOf(64)));

        String passphrase2 = encryption.generatePassphrase();

        assertThat(passphrase2, isNotNullOrEmptyString());
        assertThat(Integer.valueOf(passphrase2.length()), equalTo(Integer.valueOf(64)));

        assertThat(passphrase1, is(not(equalTo(passphrase2))));
    }

    /**
     * Test method for {@link de.dennishoersch.web.chat.Encryption#transformToPublicKey(java.security.KeyPair)}.
     */
    @Test
    public void testTransformToPublicKey() {
        KeyPair keyPair = encryption.generateRSAKeyPair();
        String publicKey = encryption.transformToPublicKey(keyPair);

        assertThat(publicKey, isNotNullOrEmptyString());

        String encrypted = encryption.encryptRSA(publicKey, "HALLO");
        assertThat(encrypted, isNotNullOrEmptyString());

        String decrypted = encryption.decryptRSA(keyPair, encrypted);
        assertThat(decrypted, isNotNullOrEmptyString());

        assertThat(decrypted, is(equalTo("HALLO")));

    }

    /**
     * Test method for {@link de.dennishoersch.web.chat.Encryption#decryptAES(java.lang.String, java.lang.String)}.
     */
    @Test
    public void testDecryptAES() {
        fail("Not yet implemented");
    }

    /**
     * Test method for {@link de.dennishoersch.web.chat.Encryption#encryptAES(java.lang.String, java.lang.String)}.
     */
    @Test
    public void testEncryptAES() {
        fail("Not yet implemented");
    }

    private static Matcher<String> isNotNullOrEmptyString() {
        return new TypeSafeMatcher<String>() {

            @Override
            public void describeTo(Description description) {
                description.appendText("a non-null and not empty string.");
            }

            @Override
            protected boolean matchesSafely(String string) {
                return !Strings.isNullOrEmpty(string);
            }
        };
    }

}
