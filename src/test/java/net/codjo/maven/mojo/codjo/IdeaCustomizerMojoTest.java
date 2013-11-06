/*
 * Team : CODJO AM / OSI / SI / BO
 *
 * Copyright (c) 2001 CODJO Asset Management.
 */
package net.codjo.maven.mojo.codjo;
import net.codjo.maven.common.mock.AgfMojoTestCase;
import net.codjo.util.file.FileUtil;
import java.io.File;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecutionException;
/**
 *
 */
public class IdeaCustomizerMojoTest extends AgfMojoTestCase {

    public void test_execute_ideaFailure() throws Exception {
        setupEnvironment("/mojos/withoutConfig-pom.xml");

        Mojo mojo = lookupMojo("idea-customizer");
        initializeProject();

        getIpr().delete();
        getIws().delete();

        try {
            mojo.execute();
            fail();
        }
        catch (MojoExecutionException ex) {
            assertEquals(IdeaCustomizerMojo.IDEA_FILE_MISSING_MESSAGE, ex.getMessage());
        }
    }


    public void test_execute_withoutConfiguration() throws Exception {
        setupEnvironment("/mojos/withoutConfig-pom.xml");

        IdeaCustomizerMojo mojo = (IdeaCustomizerMojo)lookupMojo("idea-customizer");

        initializeProject();

        FileUtil.saveContent(getIpr(), "<project><component name='JikesSettings'></component></project>");
        FileUtil.saveContent(getIws(), "<project></project>");

        mojo.execute();

        assertTrue(FileUtil.loadContent(getIpr()).indexOf("codjo-gui-toolkit") != -1);
        String iws = FileUtil.loadContent(getIws());
        assertTrue(iws.indexOf("<project") != -1);
    }

    public void test_addAttribute() throws Exception {
        setupEnvironment("/mojos/withoutConfig-pom.xml");

        IdeaCustomizerMojo mojo = (IdeaCustomizerMojo)lookupMojo("idea-customizer");

        initializeProject();

        FileUtil.saveContent(getIpr(), "<project></project>");
        FileUtil.saveContent(getIws(), "<project><component name=\"ProjectView\"><navigator><showModules/></navigator></component></project>");

        mojo.execute();

        assertTrue(FileUtil.loadContent(getIws()).indexOf("showModules PackagesPane=\"false\"") != -1);
    }

    public void test_vcsManagerConfigurationWithGit() throws Exception {
        setupEnvironment("/mojos/withoutConfig-pom.xml");

        IdeaCustomizerMojo mojo = (IdeaCustomizerMojo)lookupMojo("idea-customizer");

        initializeProject();

        FileUtil.saveContent(getIpr(), "<project></project>");
        FileUtil.saveContent(getIws(), "<project><component name=\"VcsManagerConfiguration\"><option name=\"ACTIVE_VCS_NAME\" value=\"git\"/></component></project>");

        mojo.execute();

        assertTrue(FileUtil.loadContent(getIws()).indexOf("value=\"Git\"") != -1);
    }

    public void test_vcsManagerConfigurationWithSvn() throws Exception {
        setupEnvironment("/mojos/withoutConfig-pom.xml");

        IdeaCustomizerMojo mojo = (IdeaCustomizerMojo)lookupMojo("idea-customizer");

        initializeProject();

        FileUtil.saveContent(getIpr(), "<project></project>");
        FileUtil.saveContent(getIws(), "<project><component name=\"VcsManagerConfiguration\"><option name=\"ACTIVE_VCS_NAME\" value=\"svn\"/></component></project>");

        mojo.execute();

        assertTrue(FileUtil.loadContent(getIws()).indexOf("value=\"svn\"") != -1);
    }

    private void initializeProject() {
        getProject().setGroupId("my-group");
        getProject().setArtifactId("my-id");
        getProject().setName("One project");
        getProject().setVersion("1.0");
        getProject().setUrl("http://here.com");
    }


    private File getIws() {
        return new File(getProject().getBasedir(), getProject().getArtifactId() + ".iws");
    }


    private File getIpr() {
        return new File(getProject().getBasedir(), getProject().getArtifactId() + ".ipr");
    }
}
