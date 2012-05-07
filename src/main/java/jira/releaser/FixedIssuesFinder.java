package jira.releaser;

import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.atlassian.jira.rpc.soap.client.RemoteIssue;
import com.atlassian.jira.rpc.soap.client.RemoteVersion;
import com.google.common.collect.Lists;

public class FixedIssuesFinder {

    private static final int LIMIT = 1000;
    private final JiraConnection jiraConnection;

    public FixedIssuesFinder(final JiraConnection jiraConnection) {
        this.jiraConnection = jiraConnection;
    }

    List<RemoteIssue> getIssuesFixedAfter(final String startDate) throws RemoteException {
        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return getIssuesFixedAfter(startDate, dateFormat.format(Calendar.getInstance().getTime()));
    }

    List<RemoteIssue> getIssuesFixedAfter(final String startDate, final String endDate) throws RemoteException {
        final String query = "resolution = Fixed AND " +
                "status IN (Closed, Resolved) AND " +
                "status CHANGED TO (Closed, Resolved) AFTER '" + startDate + "' BEFORE'" + endDate + "'";
        final List<RemoteIssue> issues = jiraConnection.getIssuesForSearch(query, LIMIT);
        return removeReleasedIssues(issues);
    }

    private List<RemoteIssue> removeReleasedIssues(final List<RemoteIssue> issues) {
        final ArrayList<RemoteIssue> result = Lists.newArrayList();
        for (final RemoteIssue issue : issues) {
            if (issueCanBeReleased(issue)) {
                result.add(issue);
            }
        }
        return result;
    }

    private boolean issueCanBeReleased(final RemoteIssue issue) {
        return !issueIsReleased(issue);
    }

    private boolean issueIsReleased(final RemoteIssue issue) {
        final RemoteVersion[] fixVersions = issue.getFixVersions();
        for (int i = 0; i < fixVersions.length; i++) {
            if (ReleasePatternMatcher.isReleaseNumber(fixVersions[i].getName())) {
                return true;
            }
        }
        return false;
    }

}
