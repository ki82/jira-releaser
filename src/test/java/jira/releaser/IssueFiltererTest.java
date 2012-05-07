package jira.releaser;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;

import jira.releaser.IssueFilterer;
import jira.releaser.RevisionFinder;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.atlassian.jira.rpc.soap.client.RemoteIssue;
import com.google.common.collect.Lists;


public class IssueFiltererTest extends AbstractMockitoTestCase {

    private static final int REVISION_NUMBER = 100;

    private static final String ISSUE_KEY = "STORY-123";

    @Mock
    RevisionFinder revisionFinder;
    @Mock
    RemoteIssue issue;

    private IssueFilterer issueFilterer;

    private List<RemoteIssue> filteredIssues;

    @Before
    public void setUp() throws Exception {
        when(revisionFinder.getLastRevisionInTrunkFor(ISSUE_KEY)).thenReturn(REVISION_NUMBER);
        when(issue.getKey()).thenReturn(ISSUE_KEY);
        issueFilterer = new IssueFilterer(revisionFinder);
    }

    @Test
    public void shouldExcludeStoriesWithCommitsAfterGivenRevision() throws Exception {
        filteredIssues = issueFilterer.excludeCommitsAfter(Lists.newArrayList(issue), REVISION_NUMBER - 1);

        assertTrue(filteredIssues.isEmpty());
    }

    @Test
    public void shouldInclueStorieWithTheSameCommitAsGivenRevision() throws Exception {
        filteredIssues = issueFilterer.excludeCommitsAfter(Lists.newArrayList(issue), REVISION_NUMBER);

        assertThat(filteredIssues, contains(issue));
    }

}
