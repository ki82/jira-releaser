package jira.releaser;

import static jira.releaser.RevisionFinder.NO_REVISION_FOUND;

import java.util.List;

import com.atlassian.jira.rpc.soap.client.RemoteIssue;
import com.google.common.collect.Lists;

public class IssueFilterer {

    private final RevisionFinder revisionFinder;

    public IssueFilterer(final RevisionFinder revisionFinder) {
        this.revisionFinder = revisionFinder;
    }

    public List<RemoteIssue> excludeCommitsAfter(final List<RemoteIssue> issues,
            final int excludeAfterRevision) {
        final List<RemoteIssue> filteredIssues = Lists.newArrayList();
        for (final RemoteIssue issue : issues) {
            final String issueKey = issue.getKey();
            final int lastRevision = revisionFinder.getLastRevisionInTrunkFor(issueKey);
            if (isIncludedRevision(lastRevision, excludeAfterRevision)) {
                filteredIssues.add(issue);
                System.out.println(issueKey + ": " + lastRevision);
            }
        }
        return filteredIssues;
    }

    private boolean isIncludedRevision(final int lastRevision, final int taggedRevision) {
        return lastRevision != NO_REVISION_FOUND && lastRevision <= taggedRevision;
    }

}
