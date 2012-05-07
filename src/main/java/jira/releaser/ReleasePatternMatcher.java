package jira.releaser;

import java.util.regex.Pattern;

public final class ReleasePatternMatcher {

    private static final String RELEASE_PATTERN = "\\d+\\.\\d+\\.\\d+(-[a-z0-9]+)?";

    static boolean isReleaseNumber(final String releaseNumber) {
        return Pattern.matches(RELEASE_PATTERN, releaseNumber);
    }

}
