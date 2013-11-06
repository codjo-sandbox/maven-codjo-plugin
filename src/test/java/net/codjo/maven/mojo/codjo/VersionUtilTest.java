package net.codjo.maven.mojo.codjo;
import net.codjo.maven.common.mock.AgfMojoTestCase;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.factory.DefaultArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.DefaultArtifactRepository;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.MojoFailureException;
/**
 *
 */
public class VersionUtilTest extends TestCase {
    private static final List NO_REMOTE_REPOSITORIES = Collections.emptyList();


    public void test_determineNextReleaseFrameworkVersion() throws Exception {
        VersionUtil versionUtil = createVersionUtil(defaultCodjoRepository());

        DefaultArtifact superPom = createSuperPom(Artifact.RELEASE_VERSION);

        List oldVersions = new ArrayList();
        oldVersions.add("1.31");
        oldVersions.add("1.32");
        PomUtils.mockOldPomVersions(oldVersions);
        assertEquals("1.33", versionUtil.determineNextReleaseVersion(superPom));

        oldVersions.clear();
        oldVersions.add("1.5-rc2");
        PomUtils.mockOldPomVersions(oldVersions);
        assertEquals("1.5", versionUtil.determineNextReleaseVersion(superPom));

        oldVersions.clear();
        oldVersions.add("1.18.1");
        PomUtils.mockOldPomVersions(oldVersions);
        assertEquals("1.19", versionUtil.determineNextReleaseVersion(superPom));

        oldVersions.clear();
        oldVersions.add("1.8");
        oldVersions.add("1.9");
        oldVersions.add("1.10");
        PomUtils.mockOldPomVersions(oldVersions);
        assertEquals("1.11", versionUtil.determineNextReleaseVersion(superPom));
    }


    public void test_determineNext_noCodjoRepository() throws Exception {
        VersionUtil versionUtil = createVersionUtil(NO_REMOTE_REPOSITORIES);

        try {
            versionUtil.determineNextReleaseVersion(createSuperPom(Artifact.RELEASE_VERSION));
            fail();
        }
        catch (MojoFailureException ex) {
            assertEquals("Aucun repository 'agf-repository' trouve.", ex.getMessage());
        }
    }


    public void test_determineNext_codjoUnavailable() throws Exception {
        ArtifactRepository codjoRemote = createRemoteRepository(VersionUtil.CODJO_REPOSITORY_ID);
        codjoRemote.setBlacklisted(true);

        VersionUtil versionUtil = createVersionUtil(toList(codjoRemote));

        try {
            versionUtil.determineNextReleaseVersion(createSuperPom(Artifact.RELEASE_VERSION));
            fail();
        }
        catch (MojoFailureException ex) {
            assertEquals("Repository 'agf-repository' est inaccessible.", ex.getMessage());
        }
    }


    public void test_determineNextPatchFrameworkVersion() throws Exception {
        VersionUtil versionUtil = createVersionUtil(defaultCodjoRepository());

        DefaultArtifact superPom = createSuperPom(Artifact.RELEASE_VERSION);
//        exemple de liste de versions récupérée
//        [info]   -> 1.50
//        [info]   -> 1.51
//        [info]   -> 1.51.1
//        [info]   -> 1.52
//        [info]   -> 1.51.1-rc2
//        [info]   -> 1.53-rc1
//        [info]   -> 1.53
//        [info]   -> 1.54.1
//        [info]   -> 1.54
//        [info]   -> 1.55
//        [info]   -> 1.55.1
//        [info]   -> 1.56
//        [info]   -> 1.57
//        [info]   -> 1.57.1
//        [info]   -> 1.57.2
//        [info]   -> 1.58
//        [info]   -> 1.59
//        [info]   -> 1.60-rc1
//        [info]   -> 1.59-1
//        [info]   -> 1.60
//        [info]   -> SNAPSHOT

        List oldVersions = new ArrayList();
        oldVersions.add("1.31");
        oldVersions.add("1.32");
        oldVersions.add("SNAPSHOT");
        PomUtils.mockOldPomVersions(oldVersions);
        assertEquals("1.32.1", versionUtil.determineNextPatchVersion(superPom));

        oldVersions.add("1.32.1");
        PomUtils.mockOldPomVersions(oldVersions);
        assertEquals("1.32.2", versionUtil.determineNextPatchVersion(superPom));

        oldVersions.add("1.33-rc1");
        oldVersions.add("1.33-rc2");
        PomUtils.mockOldPomVersions(oldVersions);
        assertEquals("1.32.2", versionUtil.determineNextPatchVersion(superPom));

        oldVersions.clear();
        oldVersions.add("1.8");
        oldVersions.add("1.8.1");
        PomUtils.mockOldPomVersions(oldVersions);
        assertEquals("1.8.2", versionUtil.determineNextPatchVersion(superPom));
    }


    public void test_determineNextRcFrameworkVersion() throws Exception {
        VersionUtil versionUtil = createVersionUtil(defaultCodjoRepository());

        DefaultArtifact superPom = createSuperPom(Artifact.RELEASE_VERSION);

        List oldVersions = new ArrayList();
        oldVersions.add("1.31");
        oldVersions.add("1.32");
        oldVersions.add("SNAPSHOT");
        PomUtils.mockOldPomVersions(oldVersions);
        assertEquals("1.33-rc1", versionUtil.determineNextReleaseCandidateVersion(superPom));

        oldVersions.add("1.32.1");
        PomUtils.mockOldPomVersions(oldVersions);
        assertEquals("1.33-rc1", versionUtil.determineNextReleaseCandidateVersion(superPom));

        oldVersions.add("1.33-rc1");
        oldVersions.add("1.33-rc2");
        PomUtils.mockOldPomVersions(oldVersions);
        assertEquals("1.33-rc3", versionUtil.determineNextReleaseCandidateVersion(superPom));
    }


    public void test_comparator_list() throws Exception {
        List versionsToSort = new ArrayList();
        versionsToSort.add("1.10.1");
        versionsToSort.add("1.2-rc1");
        versionsToSort.add("1.2.1");
        versionsToSort.add("1.8");
        versionsToSort.add("1.9");
        versionsToSort.add("1.10");
        versionsToSort.add("1.11-rc2");
        versionsToSort.add("1.11-rc1");
        versionsToSort.add("1.11");
        versionsToSort.add("1.20");
        versionsToSort.add("SNAPSHOT");

        List expectedSortedVersions = new ArrayList();
        expectedSortedVersions.add("SNAPSHOT");
        expectedSortedVersions.add("1.2-rc1");
        expectedSortedVersions.add("1.2.1");
        expectedSortedVersions.add("1.8");
        expectedSortedVersions.add("1.9");
        expectedSortedVersions.add("1.10");
        expectedSortedVersions.add("1.10.1");
        expectedSortedVersions.add("1.11-rc1");
        expectedSortedVersions.add("1.11-rc2");
        expectedSortedVersions.add("1.11");
        expectedSortedVersions.add("1.20");

        Collections.sort(versionsToSort, new VersionComparator());

        Assert.assertEquals(expectedSortedVersions, versionsToSort);
    }


    public void test_comparator() throws Exception {
        VersionComparator comparator = new VersionComparator();

        assertTrue(comparator.compare("SNAPSHOT", "1.20") < 0);
        assertTrue(comparator.compare("1.20", "SNAPSHOT") > 0);
        assertEquals(0, comparator.compare("SNAPSHOT", "SNAPSHOT"));

        assertTrue(comparator.compare("1.2-rc1", "1.1") > 0);
        assertTrue(comparator.compare("1.2-rc1", "1.2") < 0);
        assertTrue(comparator.compare("1.2-rc1", "1.2.1") < 0);
        assertTrue(comparator.compare("1.2-rc1", "1.3") < 0);
        assertTrue(comparator.compare("1.2-rc1", "1.2-rc2") < 0);

        assertTrue(comparator.compare("1.2.1", "1.1") > 0);
        assertTrue(comparator.compare("1.2.1", "1.2") > 0);
        assertTrue(comparator.compare("1.2.1", "1.2.2") < 0);
        assertTrue(comparator.compare("1.2.1", "1.3") < 0);
        assertTrue(comparator.compare("1.2.1", "1.2-rc1") > 0);
    }


    private VersionUtil createVersionUtil(List remoteRepositories) {
        return new VersionUtil(new DefaultArtifactFactory(),
                               new RepositoryMetadataManagerMock(),
                               remoteRepositories,
                               null);
    }


    private VersionRange toVersion(String releaseVersion) {
        return VersionRange.createFromVersion(releaseVersion);
    }


    private DefaultArtifact createSuperPom(String version) {
        return new DefaultArtifact("net.codjo.pom", "codjo-pom", toVersion(version), null, "pom", "n/a", null);
    }


    private List toList(ArtifactRepository remote) {
        return Collections.singletonList(remote);
    }


    static ArtifactRepository createRemoteRepository(String repositoryId) {
        return new DefaultArtifactRepository(repositoryId,
                                             AgfMojoTestCase.toUrl("src/test/resources/remoteRepository"),
                                             new DefaultRepositoryLayout());
    }


    static List defaultCodjoRepository() {
        return Collections.singletonList(createRemoteRepository(VersionUtil.CODJO_REPOSITORY_ID));
    }
}
