package jira.releaser;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.atlassian.jira.rpc.soap.client.RemoteIssue;
import com.atlassian.jira.rpc.soap.client.RemoteVersion;
import com.google.common.collect.Lists;


public class IssueReleaserTest extends AbstractMockitoTestCase {

    private static final String PROJECT_NAME = "STORY";
    private static final String VERSION_NAME = "1.2.3-p4";

    @Mock
    JiraConnection jiraConnection;
    @Mock
    RemoteIssue issue;
    @Mock
    RemoteVersion version;
    @Mock
    RemoteVersion wrongVersion;

    List<RemoteIssue> issues = Lists.newArrayList();
    List<RemoteVersion> existingVersions = Lists.newArrayList();

    IssueReleaser issueReleaser;

    @Before
    public void setUp() throws Exception {
        when(issue.getProject()).thenReturn(PROJECT_NAME);
        when(jiraConnection.getVersionsIn(PROJECT_NAME)).thenReturn(existingVersions);

        existingVersions.add(wrongVersion);
        issues.add(issue);

        issueReleaser = new IssueReleaser(jiraConnection);
    }

    @Test
    public void shouldAddExistingFixVersionToIssue() throws Exception {
        givenExistingFixVersionWithName(VERSION_NAME);

        issueReleaser.addFixVersionTo(issues, VERSION_NAME);

        verify(jiraConnection).addFixVersionToIssue(issue, version);
        verify(jiraConnection, never()).createReleasedVersion(anyString(), anyString());
    }

    @Test
    public void shouldCreateAndAddFixVersionToIssue() throws Exception {
        when(jiraConnection.createReleasedVersion(VERSION_NAME, PROJECT_NAME)).thenReturn(version);

        issueReleaser.addFixVersionTo(issues, VERSION_NAME);

        verify(jiraConnection).addFixVersionToIssue(issue, version);
    }

    @Test
    public void shouldSetExistingFixVersionToReleasedWhenUnreleased() throws Exception {
        givenExistingFixVersionWithName(VERSION_NAME);
        when(version.isReleased()).thenReturn(false);

        issueReleaser.addFixVersionTo(issues, VERSION_NAME);

        verify(jiraConnection).releaseFixVersion(version, PROJECT_NAME);
    }

    @Test
    public void shouldNotSetExistingFixVersionToReleasedWhenReleased() throws Exception {
        givenExistingFixVersionWithName(VERSION_NAME);
        when(version.isReleased()).thenReturn(true);

        issueReleaser.addFixVersionTo(issues, VERSION_NAME);

        verify(jiraConnection, never()).releaseFixVersion(any(RemoteVersion.class), anyString());
    }

    private void givenExistingFixVersionWithName(final String versionName) {
        existingVersions.add(version);
        when(version.getName()).thenReturn(versionName);
    }

}
