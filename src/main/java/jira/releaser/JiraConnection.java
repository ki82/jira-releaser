package jira.releaser;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.axis.NoEndPointException;

import com.atlassian.jira.rpc.soap.client.JiraSoapService;
import com.atlassian.jira.rpc.soap.client.RemoteAuthenticationException;
import com.atlassian.jira.rpc.soap.client.RemoteFieldValue;
import com.atlassian.jira.rpc.soap.client.RemoteIssue;
import com.atlassian.jira.rpc.soap.client.RemoteVersion;
import com.atlassian.jira_soapclient.SOAPSession;
import com.google.common.collect.Lists;

public class JiraConnection {

    private final SOAPSession soapSession;
    private String token;
    private JiraSoapService jiraService;
    private final Arguments arguments;

    public static JiraConnection login(final Arguments arguments, final SOAPSession soapSession) throws Exception {
        final JiraConnection jiraConnection = new JiraConnection(arguments, soapSession);
        jiraConnection.loginToJira();
        return jiraConnection;
    }

    private JiraConnection(final Arguments argumentParser, final SOAPSession soapSession) {
        this.arguments = argumentParser;
        this.soapSession = soapSession;
    }

    private void loginToJira() throws RemoteException {
        System.out.println("Connecting with: " + arguments.getUsername());
        try {
            soapSession.connect(arguments.getUsername(), arguments.getPassword());
        } catch (final NoEndPointException e) {
            System.err.println("Unable to connect to Jira. Exiting.");
            System.exit(-1);
        } catch (final RemoteAuthenticationException e) {
            System.err.println("Could not log in to Jira. Exiting.");
            System.exit(-1);
        } catch (final Exception e) {
            System.err.println("Jira Connection Error");
            System.err.println(e.getStackTrace());
            System.exit(-1);
        }
        jiraService = soapSession.getJiraSoapService();
        token = soapSession.getAuthenticationToken();
    }

    public List<RemoteIssue> getIssuesForSearch(final String query, final int limit) throws RemoteException {
        return Lists.newArrayList(jiraService.getIssuesFromJqlSearch(token, query, limit));
    }

    public List<RemoteVersion> getVersionsIn(final String projectName) throws RemoteException {
        return Lists.newArrayList(jiraService.getVersions(token, projectName));
    }

    public void releaseFixVersion(final RemoteVersion version, final String projectName)
            throws RemoteException {
        jiraService.releaseVersion(token, projectName, version);
    }

    public RemoteVersion createReleasedVersion(final String versionName, final String projectName)
            throws RemoteException {
        final RemoteVersion remoteVersion = new RemoteVersion();
        remoteVersion.setName(versionName);
        remoteVersion.setReleased(true);
        return jiraService.addVersion(token, projectName, remoteVersion);
    }

    public void addFixVersionToIssue(final RemoteIssue issue, final RemoteVersion newVersion)
            throws RemoteException {
        final RemoteFieldValue versionField = new RemoteFieldValue();
        versionField.setId("fixVersions");
        versionField.setValues(getExtendedFixVersionIds(issue, newVersion));
        jiraService.updateIssue(token, issue.getKey(), new RemoteFieldValue[]{ versionField });
    }

    private String[] getExtendedFixVersionIds(final RemoteIssue issue, final RemoteVersion newVersion) {
        final RemoteVersion[] fixVersions = issue.getFixVersions();
        final List<String> versionIds = Lists.newArrayList();
        for (int i = 0; i < fixVersions.length; i++) {
            versionIds.add(fixVersions[i].getId());
        }
        versionIds.add(newVersion.getId());
        return versionIds.toArray(new String[0]);
    }
}
