package io.moneyinthesky.dashboard.patterns;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

public class ExplodableString {

    private static final String EXPLODABLE_STRING_TOKEN_PATTERN = "(?:\\[[^\\[\\]]*?\\])";
    private static final String STRING_TOKEN_PATTERN = "(?:[^\\[\\]]+)";
    private static Pattern EXPLODABLE_STRING_PATTERN = compile("(" + EXPLODABLE_STRING_TOKEN_PATTERN + "|" + STRING_TOKEN_PATTERN + ")");

    private ExplodableString() {}

    public static List<String> explode(String format) {
        if(format == null)
            return null;

        List<String> variations = new ArrayList<>();

        for (ExplodableStringToken explodableStringToken : generateTokens(format)) {
            variations = multiplyByVariations(variations, explodableStringToken);
        }

        return variations;
    }

    private static List<ExplodableStringToken> generateTokens(String format) {
        List<ExplodableStringToken> tokens = new ArrayList<>();

        Matcher matcher = EXPLODABLE_STRING_PATTERN.matcher(format);
        while (matcher.find()) {
            tokens.add(new ExplodableStringToken(format.substring(matcher.start(), matcher.end())));
        }

        return tokens;
    }

    private static List<String> multiplyByVariations(List<String> variations, ExplodableStringToken explodableStringToken) {
        List<String> explodedList = new ArrayList<>();

        List<String> tokens = explodableStringToken.explode();
        if (!variations.isEmpty() && !tokens.isEmpty()) {
            tokens.stream().forEach(variable -> variations.stream().forEach(variation -> explodedList.add(variation + variable)));

        } else if (!variations.isEmpty()) {
            explodedList.addAll(variations);

        } else {
            tokens.stream().forEach(variable -> explodedList.add(variable));
        }

        return explodedList;
    }
}
