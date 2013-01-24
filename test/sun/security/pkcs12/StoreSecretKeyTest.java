/*
 * Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/*
 * @test
 * @bug 8005408
 * @summary KeyStore API enhancements
 */

import java.io.*;
import java.security.*;
import java.util.*;
import javax.crypto.*;
import javax.crypto.spec.*;

// Store a secret key in a keystore and retrieve it again.

public class StoreSecretKeyTest {
    private final static String DIR = System.getProperty("test.src", ".");
    private static final char[] PASSWORD = "passphrase".toCharArray();
    private static final String KEYSTORE = "keystore.p12";
    private static final String ALIAS = "my secret key";

    public static void main(String[] args) throws Exception {

        new File(KEYSTORE).delete();

        try {

            KeyStore keystore = KeyStore.getInstance("PKCS12");
            keystore.load(null, null);

            // Set entry
            keystore.setEntry(ALIAS,
                new KeyStore.SecretKeyEntry(generateSecretKey("AES", 128)),
                    new KeyStore.PasswordProtection(PASSWORD));

            System.out.println("Storing keystore to: " + KEYSTORE);
            keystore.store(new FileOutputStream(KEYSTORE), PASSWORD);

            System.out.println("Loading keystore from: " + KEYSTORE);
            keystore.load(new FileInputStream(KEYSTORE), PASSWORD);
            System.out.println("Loaded keystore with " + keystore.size() +
                " entries");
            KeyStore.Entry entry = keystore.getEntry(ALIAS,
                new KeyStore.PasswordProtection(PASSWORD));
            System.out.println("Retrieved entry: " + entry);

            if (entry instanceof KeyStore.SecretKeyEntry) {
                System.out.println("Retrieved secret key entry: " +
                    entry);
            } else {
                throw new Exception("Not a secret key entry");
            }
        } finally {
            new File(KEYSTORE).delete();
        }
    }

    private static SecretKey generateSecretKey(String algorithm, int size)
        throws NoSuchAlgorithmException {
        KeyGenerator generator = KeyGenerator.getInstance(algorithm);
        generator.init(size);
        return generator.generateKey();
    }
}