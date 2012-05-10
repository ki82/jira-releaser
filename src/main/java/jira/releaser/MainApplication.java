package jira.releaser;

import java.net.URL;
import java.util.List;

import nu.xom.Builder;

import com.atlassian.jira.rpc.soap.client.RemoteIssue;
import com.atlassian.jira_soapclient.SOAPSession;

public class MainApplication {
    private final static String SOAPSERVICE_URL = "/rpc/soap/jirasoapservice-v2";
    private static URL jiraUrl;
    private static SOAPSession soapSession;

    public static void main(final String[] args) throws Exception {
        runWithArgs(args);
    }

    public static void runWithArgs(final String... args) throws Exception {
        final Arguments arguments = Arguments.parse(args);
        jiraUrl = new URL(arguments.getJiraUrl() + SOAPSERVICE_URL);
        soapSession = new SOAPSession(jiraUrl);

        final JiraConnection jiraConnection = JiraConnection.login(arguments, soapSession);
        final FixedIssuesFinder issuesFinder = new FixedIssuesFinder(jiraConnection);
        List<RemoteIssue> issues;
        if (arguments.getEndDate() != null) {
            issues = issuesFinder.getIssuesFixedAfter("2011-12-10", arguments.getEndDate());
        } else {
            issues = issuesFinder.getIssuesFixedAfter("2011-12-10");
        }
        final FishEyeConnection fishEyeConnection = FishEyeConnection.login(arguments, new Builder());
        final RevisionFinder revisionFinder = new RevisionFinder(fishEyeConnection);
        final IssueFilterer issueFilterer = new IssueFilterer(revisionFinder);
        final List<RemoteIssue> filteredIssues = issueFilterer.excludeCommitsAfter(issues, arguments.getTaggedRevision());
        final IssueReleaser issueReleaser = new IssueReleaser(jiraConnection);
        issueReleaser.addFixVersionTo(filteredIssues, arguments.getFixVersion());
    }

}
