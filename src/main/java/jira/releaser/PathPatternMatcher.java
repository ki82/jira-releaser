package jira.releaser;

import java.util.regex.Pattern;

public class PathPatternMatcher {

    private static final String TRUNK_PATTERN = "([^/]+/)?trunk/.*";

    public static boolean isInTrunk(final String path) {
        return Pattern.matches(TRUNK_PATTERN, path);
    }

}
