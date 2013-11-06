package net.codjo.maven.mojo.codjo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.codjo.maven.common.mock.AgfMojoTestCase;
import net.codjo.maven.common.mock.MavenProjectMock;
import net.codjo.maven.mojo.codjo.scm.CommitCommandMock;
import net.codjo.test.common.FileComparator;
import net.codjo.test.common.LogString;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.MojoFailureException;
/**
 *
 */
public class ReleaseMojoTest extends AgfMojoTestCase {
    private LogString log = new LogString();
    private ReleaseMojo mojo;
    private ArtifactRepository codjoRemote;


    protected void setUp() throws Exception {
        super.setUp();
        setupEnvironment(localFile("-pom.xml"), USE_POM_DIRECTLY);

        mojo = (ReleaseMojo)lookupMojo("release");

        codjoRemote = VersionUtilTest.createRemoteRepository(VersionUtil.CODJO_REPOSITORY_ID);
        getProject().setRemoteArtifactRepositories(Collections.singletonList(codjoRemote));

        mojo.setCommitCommand(new CommitCommandMock(log));
        PomUtils.mockLastPomVersion("1.18");
        mojo.project.getModel().setArtifactId(getClass().getSimpleName());

        List reactorProjects = new ArrayList();
        MavenProjectMock projectMock = new MavenProjectMock();
        projectMock.setArtifactId(mojo.project.getArtifactId() + "-module1");
        reactorProjects.add(mojo.project);
        reactorProjects.add(projectMock);

        mojo.setReactorProjects(reactorProjects);
    }


    protected void tearDown() throws Exception {
        deleteOutputFile("release.properties");
    }


    public void test_execute() throws Exception {
        mockUserAnswer("no");
        mojo.setReleaseType(ReleaseMojo.STABLE_VERSION);

        mojo.execute();

        assertFileEquals(localFile("-etalon.properties"), "release.properties");
    }


    public void test_execute_remoteRepositoryIsUnavailable() throws Exception {
        codjoRemote.setBlacklisted(true);
        mojo.setReleaseType(ReleaseMojo.STABLE_VERSION);

        try {
            mojo.execute();
            fail();
        }
        catch (MojoFailureException ex) {
            assertEquals("Repository 'agf-repository' est inaccessible.", ex.getMessage());
        }

        assertNull(getClass().getResource("release.properties"));
    }


    public void test_execute_patch() throws Exception {
        mockUserAnswer("no");
        PomUtils.mockLastPomVersion("1.18");
        mojo.setReleaseType(ReleaseMojo.PATCH_VERSION);

        mojo.execute();

        assertFileEquals(localFile("-patch-etalon.properties"), "release.properties");
    }


    private void assertFileEquals(String expected, String actual) throws IOException {
        FileComparator fileComparator = new FileComparator("#");
        assertTrue(fileComparator.equalsNotOrdered(getOutputFile(expected),
                                                   getOutputFile(actual)));
    }


    private String localFile(String postFix) {
        return getClass().getSimpleName() + postFix;
    }
}
