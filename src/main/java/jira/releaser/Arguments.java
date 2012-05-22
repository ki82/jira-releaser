package jira.releaser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public class Arguments {

    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

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

    @Parameter(names = "--end-date", description = "In format yyyy:MM:dd HH:mm")
    private final String endDate;

    @Parameter(names = "--start-date", description = "In format yyyy:MM:dd HH:mm")
    private final String startDate;


    private Arguments() {
        endDate = dateFormat.format(Calendar.getInstance().getTime());
        startDate = dateFormat.format(new Date(0));
    }

    static Arguments parse(final String... args) {
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

    String getStartDate() {
        return startDate;
    }

}
