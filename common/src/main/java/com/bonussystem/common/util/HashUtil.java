package com.bonussystem.common.util;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

public final class HashUtil {

    private static final Argon2 ARGON2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);

    private static final int ITERATIONS = 3;
    private static final int MEMORY_KB = 65536;
    private static final int THREADS = 1;

    private HashUtil() {}

    public static String hash(String password) {
        return ARGON2.hash(ITERATIONS, MEMORY_KB, THREADS, password.toCharArray());
    }

    public static boolean verify(String storedHash, String password) {
        return ARGON2.verify(storedHash, password.toCharArray());
    }
}