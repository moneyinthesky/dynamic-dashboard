package io.moneyinthesky.dashboard.patterns;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;
import static java.lang.String.valueOf;
import static java.util.Arrays.asList;

public class ExplodableStringToken {

    private static final String VARIATION_SEPARATOR = ",";
    private static final String RANGE_SEPARATOR = "-";
    private List<String> tokenVariations;

    protected ExplodableStringToken(String rawToken) {
        if(rawToken == null || rawToken.isEmpty()) {
            tokenVariations = asList("");

        } else if(containsVariableCharacter(rawToken)) {
            generateVariations(rawToken.substring(1, rawToken.length()-1));

        } else {
            tokenVariations = asList(rawToken);
        }
    }

    protected List<String> explode() {
        return tokenVariations;
    }

    private List<String> generateVariations(String token) {
        tokenVariations = new ArrayList<>();
        List<String> subTokens = asList(token.split(VARIATION_SEPARATOR));
        subTokens.stream().forEach(subToken -> addToVariations(subToken));

        return tokenVariations;
    }

    private void addToVariations(String subToken) {
        if(subToken.contains(RANGE_SEPARATOR)) {
            String[] rangeParts = subToken.split(RANGE_SEPARATOR);
            validateRangeParts(rangeParts);

            if(rangeParts[0].length() > 1 || rangeParts[1].length() > 1) {
                List<Integer> temporaryTokens = new ArrayList<>();
                int lengthOfFirstToken = rangeParts[0].length();

                for(int current = parseInt(rangeParts[0]); current <= parseInt(rangeParts[1]); current++) {
                    temporaryTokens.add(current);
                }

                temporaryTokens.stream().map((val) -> {
                    String stringValue = valueOf(val);
                    int stringValueLength = stringValue.length();
                    for(int x = stringValueLength; x < lengthOfFirstToken; x++) {
                        stringValue = '0' + stringValue;
                    }
                    return stringValue;
                }).forEach(tokenVariations::add);

            } else {
                char first = rangeParts[0].charAt(0);
                char last = rangeParts[1].charAt(0);

                for (char current = first; current <= last; current++) {
                    tokenVariations.add(valueOf(current));
                }
            }
        } else {
            tokenVariations.add(subToken);
        }
    }

    private void validateRangeParts(String[] rangeParts) {
        if(rangeParts.length != 2) {
            throw new IllegalArgumentException("Invalid range token");

        } else if(rangeParts[0].length() == 0 || rangeParts[1].length() == 0) {
            throw new IllegalArgumentException("Empty ranges are not supported");

        } else if(rangeParts[0].length() > 1) {
            if(!rangeParts[0].matches("[0-9]+")) {
                throw new IllegalArgumentException("Multi-character ranges may only contain numbers");
            }
        } else if(rangeParts[1].length() > 1) {
            if(!rangeParts[1].matches("[0-9]+")) {
                throw new IllegalArgumentException("Multi-character ranges may only contain numbers");
            }
        }
    }

    private static boolean containsVariableCharacter(String format) {
        return format.startsWith("[") && format.endsWith("]");
    }
}
