package jira.releaser;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.contains;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import jira.releaser.FixedIssuesFinder;
import jira.releaser.JiraConnection;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.atlassian.jira.rpc.soap.client.RemoteIssue;
import com.atlassian.jira.rpc.soap.client.RemoteVersion;
import com.google.common.collect.Lists;

public class FixedIssuesFinderTest extends AbstractMockitoTestCase {

    private static final String RELEASE_NUMBER = "4.7.0-p11";

    private static final String START_DATE = "2001-09-11";

    private static final String END_DATE = "2008-08-08";

    private FixedIssuesFinder fixedIssuesFinder;

    @Mock
    JiraConnection jiraConnection;
    @Mock
    RemoteIssue issue;
    @Mock
    RemoteVersion fixVersion;

    private List<RemoteIssue> remoteIssues;

    private RemoteVersion[] fixVersions;

    @Before
    public void setup() throws Exception {
        remoteIssues = Lists.newArrayList(issue);
        when(jiraConnection.getIssuesForSearch(anyString(), anyInt())).thenReturn(remoteIssues);
        when(fixVersion.getName()).thenReturn(RELEASE_NUMBER);
        fixVersions = new RemoteVersion[] { fixVersion };
        when(issue.getFixVersions()).thenReturn(new RemoteVersion[0]);
        fixedIssuesFinder = new FixedIssuesFinder(jiraConnection);
    }

    @Test
    public void shouldSearchForIssuesFixedAfterDate() throws Exception {
        fixedIssuesFinder.getIssuesFixedAfter(START_DATE);

        verify(jiraConnection).getIssuesForSearch(contains(START_DATE), anyInt());
    }

    @Test
    public void shouldSearchForIssuesBeforeEndDate() throws Exception {
        fixedIssuesFinder.getIssuesFixedAfter(START_DATE, END_DATE);

        verify(jiraConnection).getIssuesForSearch(contains(START_DATE), anyInt());
    }

    @Test
    public void shouldFilterOutIssuesWithFixVersionMatchingVersionNumber() throws Exception {
        when(issue.getFixVersions()).thenReturn(fixVersions);
        assertTrue(fixedIssuesFinder.getIssuesFixedAfter(START_DATE).isEmpty());
    }

    @Test
    public void shouldNotFilterOutIssuesWithoutFixVersions() throws Exception {
        assertThat(fixedIssuesFinder.getIssuesFixedAfter(START_DATE), contains(issue));
    }

    @Test
    public void shouldNotFilterOutIssuesWithOtherFixVersions() throws Exception {
        when(issue.getFixVersions()).thenReturn(fixVersions);
        when(fixVersion.getName()).thenReturn("Santa Clause");
        assertThat(fixedIssuesFinder.getIssuesFixedAfter(START_DATE), contains(issue));
    }


}
