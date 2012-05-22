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

public class FixedIssuesFinder {

    private static final int LIMIT = Integer.MAX_VALUE;
    private final JiraConnection jiraConnection;
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");;


    public FixedIssuesFinder(final JiraConnection jiraConnection) {
        this.jiraConnection = jiraConnection;
    }

    List<RemoteIssue> getIssuesFixedAfter(final String startDate, final String endDate) throws ParseException {
        final String query = "resolution = Fixed AND " +
                "status IN (Closed, Resolved) AND " +
                "status CHANGED TO (Closed, Resolved) AFTER '" + startDate + "' BEFORE'" + endDate + "'";

        System.out.println("Jira Query: " + query);

        final List<RemoteIssue> issues = Lists.newArrayList();
        try {
            issues.addAll(jiraConnection.getIssuesForSearch(query, LIMIT));
        } catch (final RemoteException e) {
            System.out.println("To many results from Jira query, splitting the query into subqueries");
            final Date start = dateFormat.parse(startDate);
            final Date end = dateFormat.parse(endDate);
            final long middleTime = (end.getTime() + start.getTime()) / 2;
            final Date middle = new Date(middleTime);
            issues.addAll(getIssuesFixedAfter(dateFormat.format(start), dateFormat.format(middle)));
            issues.addAll(getIssuesFixedAfter(dateFormat.format(middle), dateFormat.format(end)));
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
