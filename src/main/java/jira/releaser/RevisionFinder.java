package jira.releaser;

import java.util.List;

public class RevisionFinder {

    static final int NO_REVISION_FOUND = -1;
    private final FishEyeConnection connection;

    public RevisionFinder(final FishEyeConnection connection) {
        this.connection = connection;
    }

    public int getLastRevisionInTrunkFor(final String issueKey) {
        final List<FishEyeCheckin> checkins = connection.getCheckinsWhereCommentContains(issueKey);
        int lastRevision = NO_REVISION_FOUND;
        for (final FishEyeCheckin checkin : checkins) {
            final int currentRevision = checkin.getRevision();
            if (isInTrunk(checkin) && currentRevision > lastRevision) {
                lastRevision = currentRevision;
            }
        }
        return lastRevision;
    }

    private boolean isInTrunk(final FishEyeCheckin checkin) {
        return PathPatternMatcher.isInTrunk(checkin.getPath());
    }

}
