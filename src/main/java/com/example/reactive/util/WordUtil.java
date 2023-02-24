package com.example.reactive.util;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

public class WordUtil {

    private static final Random RANDOM_GENERATOR = new SecureRandom();
    private static final List<String> DICTIONARY = List.of("Ночь", "Аптека", "Улица", "Фонарь", "Огонь", "Скелет");

    public static String generateWord() {
        int i = RANDOM_GENERATOR.nextInt(DICTIONARY.size());
        return DICTIONARY.get(i);
    }
}
