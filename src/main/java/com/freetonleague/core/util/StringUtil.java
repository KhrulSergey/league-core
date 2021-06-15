package com.freetonleague.core.util;

import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;

public class StringUtil {

    private static final int NAME_LENGTH = 6;
    private static final int TEAM_NAME_LENGTH = 9;
    private static final char MINIMUM_CODE_POINT = '0';
    private static final char MAXIMUM_CODE_POINT = 'z';

    public static String generateRandomSpecialCharacters(int length) {
        RandomStringGenerator pwdGenerator = new RandomStringGenerator.Builder()
                .withinRange(MINIMUM_CODE_POINT, MAXIMUM_CODE_POINT)
                .filteredBy(CharacterPredicates.LETTERS, CharacterPredicates.DIGITS)
                .build();
        return pwdGenerator.generate(length);
    }

    public static String generateRandomName() {
        return generateRandomSpecialCharacters(NAME_LENGTH);
    }

    public static String generateRandomTeamName() {
        return generateRandomSpecialCharacters(TEAM_NAME_LENGTH);
    }
}
