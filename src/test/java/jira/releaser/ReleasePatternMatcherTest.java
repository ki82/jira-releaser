package jira.releaser;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import jira.releaser.ReleasePatternMatcher;

import org.junit.Test;

public class ReleasePatternMatcherTest {

    @Test
    public void verifyReleaseNumbersMatched() throws Exception {
        assertTrue(ReleasePatternMatcher.isReleaseNumber("1.1.1"));
        assertTrue(ReleasePatternMatcher.isReleaseNumber("11.11.11"));
        assertTrue(ReleasePatternMatcher.isReleaseNumber("11.11.11-p10"));
    }

    @Test
    public void verifyInvalidStringsNotMatched() throws Exception {
        assertFalse(ReleasePatternMatcher.isReleaseNumber("i'm no version number"));
        assertFalse(ReleasePatternMatcher.isReleaseNumber("1.1"));
        assertFalse(ReleasePatternMatcher.isReleaseNumber("1.1.1.1"));
        assertFalse(ReleasePatternMatcher.isReleaseNumber("1.1.1-"));
    }

}
