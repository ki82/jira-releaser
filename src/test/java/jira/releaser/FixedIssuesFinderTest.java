package jira.releaser;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.atlassian.jira.rpc.soap.client.RemoteIssue;
import com.atlassian.jira.rpc.soap.client.RemoteVersion;
import com.google.common.collect.Lists;

@SuppressWarnings({ "unchecked" })
public class FixedIssuesFinderTest extends AbstractMockitoTestCase {

    private static final String RELEASE_NUMBER = "4.7.0-p11";

    private static final String START_DATE = "2005-09-11 13:00";

    private static final String END_DATE = "2008-08-08 13:00";

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
        remoteIssues = Lists.newArrayList();
        remoteIssues.add(issue);
        when(jiraConnection.getIssuesForSearch(anyString(), anyInt())).thenReturn(
                remoteIssues, new ArrayList<RemoteIssue>());
        when(fixVersion.getName()).thenReturn(RELEASE_NUMBER);
        fixVersions = new RemoteVersion[] { fixVersion };
        when(issue.getFixVersions()).thenReturn(new RemoteVersion[0]);
        fixedIssuesFinder = new FixedIssuesFinder(jiraConnection);
    }

    @Test
    public void shouldSearchForIssuesFixedAfterDate() throws Exception {
        fixedIssuesFinder.getIssuesFixedAfter(START_DATE, END_DATE);

        verify(jiraConnection).getIssuesForSearch(contains(START_DATE), anyInt());
    }

    @Test
    public void shouldSearchForIssuesBeforeEndDate() throws Exception {
        fixedIssuesFinder.getIssuesFixedAfter(START_DATE, END_DATE);

        verify(jiraConnection).getIssuesForSearch(contains(START_DATE), anyInt());
    }

    @Test
    public void shouldFilterOutIssuesWithReleasedFixVersionMatchingAnyVersionNumber()
            throws Exception {
        when(issue.getFixVersions()).thenReturn(fixVersions);
        when(fixVersion.isReleased()).thenReturn(true);
        assertNoIssueFound();
    }

    @Test
    public void shouldNotFilterOutIssuesWithUnreleasedFixVersionMatchingAnyVersionNumber()
            throws Exception {
        when(issue.getFixVersions()).thenReturn(fixVersions);
        when(fixVersion.isReleased()).thenReturn(false);
        assertFound(issue);
    }

    @Test
    public void shouldNotFilterOutIssuesWithoutFixVersions() throws Exception {
        assertFound(issue);
    }

    @Test
    public void shouldNotFilterOutIssuesWithOtherFixVersions() throws Exception {
        when(issue.getFixVersions()).thenReturn(fixVersions);
        when(fixVersion.getName()).thenReturn("Santa Clause");
        assertFound(issue);
    }

    @Test
    public void shouldSplitTheQueryWhenToManySearchResult() throws Exception {
        final RemoteIssue lastIssue = givenQueryResultCountWillExceedLimit();
        when(lastIssue.getCreated()).thenReturn(getDate(2001, 9, 11, 13, 00));

        fixedIssuesFinder.getIssuesFixedAfter(START_DATE, END_DATE);

        verify(jiraConnection).getIssuesForSearch(contains("createdDate >= '2001-09-11 13:00'"), anyInt());
    }

    private void assertNoIssueFound() throws ParseException, RemoteException {
        assertThat(fixedIssuesFinder.getIssuesFixedAfter(START_DATE, END_DATE),
                hasSize(0));
    }

    private void assertFound(final RemoteIssue foundIssue) throws Exception {
        assertThat(fixedIssuesFinder.getIssuesFixedAfter(START_DATE, END_DATE),
                contains(foundIssue));
    }

    private Calendar getDate(final int year, final int month, final int day, final int hour, final int minute) {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, hour, minute);
        return calendar;
    }

    private RemoteIssue givenQueryResultCountWillExceedLimit() {
        remoteIssues.clear();
        for (int i = 0; i < 999; i++) {
            final RemoteIssue issue = mock(RemoteIssue.class);
            when(issue.getFixVersions()).thenReturn(new RemoteVersion[0]);
            remoteIssues.add(issue);

        }
        final RemoteIssue lastIssue = mock(RemoteIssue.class);
        when(lastIssue.getFixVersions()).thenReturn(new RemoteVersion[0]);
        remoteIssues.add(lastIssue);
        return lastIssue;
    }


}
