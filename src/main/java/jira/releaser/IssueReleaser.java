package jira.releaser;

import java.rmi.RemoteException;
import java.util.List;

import com.atlassian.jira.rpc.soap.client.RemoteIssue;
import com.atlassian.jira.rpc.soap.client.RemoteVersion;

public class IssueReleaser {

    private final JiraConnection jiraConnection;

    public IssueReleaser(final JiraConnection jiraConnection) {
        this.jiraConnection = jiraConnection;
    }

    public void addFixVersionTo(final List<RemoteIssue> issues, final String versionName)
            throws RemoteException {
        for (final RemoteIssue issue : issues) {
            System.out.println("Adding fix version to " + issue.getKey());
            final RemoteVersion version = getOrCreateReleasedVersion(versionName, issue.getProject());
            jiraConnection.addFixVersionToIssue(issue, version);
        }
    }

    private RemoteVersion getOrCreateReleasedVersion(final String versionName,
            final String projectName) throws RemoteException {
        final List<RemoteVersion> existingVersions = jiraConnection.getVersionsIn(projectName);
        for (final RemoteVersion version : existingVersions) {
            if (versionName.equals(version.getName())) {
                release(version, projectName);
                return version;
            }
        }
        return jiraConnection.createReleasedVersion(versionName, projectName);
    }

    private void release(final RemoteVersion version, final String projectName)
            throws RemoteException {
        if (!version.isReleased()) {
            jiraConnection.releaseFixVersion(version, projectName);
        }
    }

}
