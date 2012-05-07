package jira.releaser;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import jira.releaser.PathPatternMatcher;

import org.junit.Test;


public class PathPatternMatcherTest {

    @Test
    public void shouldMatchAsTrunk() throws Exception {
        assertTrue(PathPatternMatcher.isInTrunk("trunk/file.txt"));
        assertTrue(PathPatternMatcher.isInTrunk("module/trunk/file.txt"));
    }

    @Test
    public void shouldNotMatchAsTrunk() throws Exception {
        assertFalse(PathPatternMatcher.isInTrunk("module/branches/branch/file.txt"));
        assertFalse(PathPatternMatcher.isInTrunk("module/submodule/trunk/file.txt"));
    }

}
