package jira.releaser;

import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.atlassian.jira.rpc.soap.client.RemoteIssue;
import com.atlassian.jira.rpc.soap.client.RemoteVersion;
import com.google.common.collect.Lists;

class FixedIssuesFinder {

    private static final int LIMIT = 1000;
    private final JiraConnection jiraConnection;
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");;


    public FixedIssuesFinder(final JiraConnection jiraConnection) {
        this.jiraConnection = jiraConnection;
    }

    List<RemoteIssue> getIssuesFixedAfter(final String startDate, final String endDate) throws ParseException, RemoteException {
        return getIssues(startDate, endDate, new Date(0));
    }

    private List<RemoteIssue> getIssues(final String startDate, final String endDate, final Date creationDate)
            throws RemoteException {
        final String query = "resolution = Fixed AND " +
                "status IN (Closed, Resolved) AND " +
                "status CHANGED TO (Closed, Resolved) AFTER '" + startDate + "' BEFORE '" + endDate + "' AND " +
                "createdDate >= '" + dateFormat.format(creationDate) + "' ORDER BY createdDate ASC";

        System.out.println("Jira Query: " + query);

        final List<RemoteIssue> issues = Lists.newArrayList();
        issues.addAll(jiraConnection.getIssuesForSearch(query, LIMIT));
        if (issues.size() == LIMIT) {
            System.out.println("To many results from Jira query, splitting the query into subqueries");
            issues.addAll(getIssues(startDate, endDate, issues.get(LIMIT - 1).getCreated().getTime()));
        }
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
