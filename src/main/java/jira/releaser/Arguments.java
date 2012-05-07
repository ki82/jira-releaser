package jira.releaser;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public class Arguments {


    @Parameter(names = "--username", required=true)
    private String username;

    @Parameter(names = "--password", required=true)
    private String password;

    @Parameter(names = "--tagged-revision", required=true)
    private int taggedRevision;

    @Parameter(names = "--fix-version", required=true)
    private String fixVersion;

    @Parameter(names = "--jira-url", required=true)
    private String jiraUrl;

    @Parameter(names = "--fisheye-url", required=true)
    private String fisheyeUrl;

    @Parameter(names = "--end-date")
    private String endDate;



    private Arguments() {

    }

    static Arguments parse(final String[] args) {
        final Arguments argumentParser = new Arguments();
        new JCommander(argumentParser, args);
        return argumentParser;
    }

    String getUsername() {
        return username;
    }

    String getPassword() {
        return password;
    }

    int getTaggedRevision() {
        return taggedRevision;
    }

    String getFixVersion() {
        return fixVersion;
    }

    String getEndDate() {
        return endDate;
    }

    String getJiraUrl() {
        return jiraUrl;
    }

    String getFisheyeUrl() {
        return fisheyeUrl;
    }

}
