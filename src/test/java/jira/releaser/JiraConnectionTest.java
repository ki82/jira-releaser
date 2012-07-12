package jira.releaser;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.atlassian.jira.rpc.soap.client.JiraSoapService;
import com.atlassian.jira.rpc.soap.client.RemoteIssue;
import com.atlassian.jira.rpc.soap.client.RemoteVersion;
import com.atlassian.jira_soapclient.SOAPSession;


public class JiraConnectionTest extends AbstractMockitoTestCase {

    private static final String PROJECT_NAME = "project-name";
    private static final RemoteIssue[] NO_ISSUES = new RemoteIssue[0];
    private static final String USERNAME = "Username";
    private static final String PASSWORD = "Password";
    private static final String TOKEN = "Token";
    private static final String QUERY = "Query";
    private static final int LIMIT = 7;

    JiraConnection jiraConnection;

    @Mock
    Arguments arguments;
    @Mock
    SOAPSession soapSession;
    @Mock
    JiraSoapService jiraService;
    @Mock
    RemoteVersion version;

    @Before
    public void setup() throws Exception {
        when(arguments.getUsername()).thenReturn(USERNAME);
        when(arguments.getPassword()).thenReturn(PASSWORD);
        when(soapSession.getJiraSoapService()).thenReturn(jiraService);
        when(soapSession.getAuthenticationToken()).thenReturn(TOKEN);
        jiraConnection = JiraConnection.login(arguments, soapSession);
    }

    @Test
    public void triesToLogIn() throws Exception {
        verify(soapSession).connect(USERNAME, PASSWORD);
    }

    @Test
    public void shouldUseTokenWhenSearching() throws Exception {
        when(jiraService.getIssuesFromJqlSearch(anyString(), anyString(), anyInt())).thenReturn(NO_ISSUES);
        jiraConnection.getIssuesForSearch(QUERY, LIMIT);
        verify(jiraService).getIssuesFromJqlSearch(TOKEN, QUERY, LIMIT);
    }

    @Test
    public void releasesFixVersion() throws Exception {
        jiraConnection.releaseFixVersion(version, PROJECT_NAME);
        verify(jiraService).releaseVersion(TOKEN, PROJECT_NAME, version);
    }

}
