package net.codjo.maven.mojo.codjo;
import java.io.File;
import java.io.IOException;
import net.codjo.maven.common.mock.AgfMojoTestCase;
import net.codjo.maven.mojo.codjo.scm.CommitCommand;
import net.codjo.maven.mojo.codjo.scm.CommitConfig;
import net.codjo.test.common.LogString;
import net.codjo.util.file.FileUtil;
import org.apache.maven.project.MavenProject;
/**
 *
 */
public abstract class SwitchToParentMojoTestCase extends AgfMojoTestCase {
    private LogString log = new LogString();
    private MavenProject parentProject = new MavenProject();
    private AbstractSwitchToParentMojo mojo;


    protected abstract String getExpectedParentVersion();


    protected abstract String getGoal();


    public void test_execute() throws Exception {
        assertParentVersion(getExpectedParentVersion());
    }


    public void test_execute_commitRefusedByUser() throws Exception {
        mockUserAnswer("no");

        mojo.execute();

        assertTrue(getContent(getPomFile()).contains("<version>"
                                                     + getExpectedParentVersion()
                                                     + "</version>"));
        log.assertContent("");
    }


    public void test_execute_batchMode() throws Exception {
        mojo.settings.setInteractiveMode(false);

        mojo.execute();

        assertTrue(getContent(getPomFile()).contains("<version>"
                                                     + getExpectedParentVersion()
                                                     + "</version>"));
        log.assertContent("commit.execute("
                          + "basedir = " + getPomFile().getParent()
                          + "; files = [" + getPomFile() + "])");
    }


    protected void setUp() throws Exception {
        super.setUp();

        setupEnvironment(getClass().getSimpleName() + "-pom.xml", DUPLICATE_POM);

        mojo = (AbstractSwitchToParentMojo)lookupMojo(getGoal());

        getProject().setParent(parentProject);
        getProject().setRemoteArtifactRepositories(VersionUtilTest.defaultCodjoRepository());

        mojo.setCommitCommand(new CommitCommandMock());
    }


    protected void assertParentVersion(String expectedParentVersion) throws Exception {
        mockUserAnswer("yes");

        mojo.execute();

        assertTrue(getContent(getPomFile()).contains("<version>" + expectedParentVersion + "</version>"));
        log.assertContent(
              "commit.execute(basedir = " + getPomFile().getParent() + "; files = [" + getPomFile() + "])");
    }


    private String getContent(File file) throws IOException {
        return FileUtil.loadContent(file);
    }


    private class CommitCommandMock extends CommitCommand {

        public void execute(CommitConfig config) {
            log.call("commit.execute", config.getScmFileSet());
        }
    }
}
