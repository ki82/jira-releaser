package jira.releaser;

import static jira.releaser.RevisionFinder.NO_REVISION_FOUND;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import jira.releaser.FishEyeCheckin;
import jira.releaser.FishEyeConnection;
import jira.releaser.RevisionFinder;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.google.common.collect.Lists;


public class RevisionFinderTest extends AbstractMockitoTestCase {

    private static final int HIGHEST_REVISION = 3000;
    private static final int HIGH_REVISION = 200;
    private static final int LOW_REVISION = 10;

    private static final String PATH_ON_TRUNK = "module/trunk/file.txt";
    private static final String PATH_ON_BRANCH = "module/branches/branch/file.txt";

    private static final String ISSUE_KEY = "STORY-123";

    @Mock
    private FishEyeConnection connection;
    @Mock
    private FishEyeCheckin highestRevisionOnBranch;
    @Mock
    private FishEyeCheckin highRevisionOnTrunk;
    @Mock
    private FishEyeCheckin lowRevisionOnTrunk;

    private final List<FishEyeCheckin> fileRevisions = Lists.newArrayList();

    private RevisionFinder revisionFinder;

    @Before
    public void setup() throws Exception {
        when(connection.getCheckinsWhereCommentContains(ISSUE_KEY)).thenReturn(fileRevisions);
        when(highestRevisionOnBranch.getRevision()).thenReturn(HIGHEST_REVISION);
        when(highestRevisionOnBranch.getPath()).thenReturn(PATH_ON_BRANCH);
        when(highRevisionOnTrunk.getRevision()).thenReturn(HIGH_REVISION);
        when(highRevisionOnTrunk.getPath()).thenReturn(PATH_ON_TRUNK);
        when(lowRevisionOnTrunk.getRevision()).thenReturn(LOW_REVISION);
        when(lowRevisionOnTrunk.getPath()).thenReturn(PATH_ON_TRUNK);
        revisionFinder = new RevisionFinder(connection);
    }

    @Test
    public void givesMinusOneWhenNoRevisionsFound() throws Exception {
        assertThat(revisionFinder.getLastRevisionInTrunkFor(ISSUE_KEY), equalTo(NO_REVISION_FOUND));
    }

    @Test
    public void findsLastRevisionNumberWhenOrderedAscending() throws Exception {
        fileRevisions.add(highRevisionOnTrunk);
        fileRevisions.add(lowRevisionOnTrunk);

        assertThat(revisionFinder.getLastRevisionInTrunkFor(ISSUE_KEY), equalTo(HIGH_REVISION));
    }

    @Test
    public void findsLastRevisionNumberWhenOrderedDescending() throws Exception {
        fileRevisions.add(lowRevisionOnTrunk);
        fileRevisions.add(highRevisionOnTrunk);

        assertThat(revisionFinder.getLastRevisionInTrunkFor(ISSUE_KEY), equalTo(HIGH_REVISION));
    }

    @Test
    public void findsLastRevisionOnTrunk() throws Exception {
        fileRevisions.add(highestRevisionOnBranch);
        fileRevisions.add(lowRevisionOnTrunk);

        assertThat(revisionFinder.getLastRevisionInTrunkFor(ISSUE_KEY), equalTo(LOW_REVISION));
    }

}
