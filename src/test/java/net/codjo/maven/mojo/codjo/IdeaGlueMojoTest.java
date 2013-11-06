package net.codjo.maven.mojo.codjo;
import net.codjo.maven.common.mock.AgfMojoTestCase;
import net.codjo.test.common.PathUtil;
import net.codjo.test.common.XmlUtil;
import net.codjo.util.file.FileUtil;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.project.MavenProject;

public class IdeaGlueMojoTest extends AgfMojoTestCase {

    protected void tearDown() throws Exception {
        super.tearDown();
        File pomGlueFile = new File(getProject().getBasedir(), "pom-glue.xml");
        if (pomGlueFile.exists()) {
            pomGlueFile.delete();
        }
    }


    public void test_fromProject() throws Exception {
        IdeaGlueMojo mojo = initialize("pom.xml");
        customizeApplicationProject();
        mojo.setWorkingDir(new File(IdeaGlueMojo.PROJECTS_DIR, "my-dir"));

        mojo.execute();

        assertPomGlue("pom_fromProject-glue-etalon.xml");
    }


    public void test_noParametersSet() throws Exception {
        Mojo mojo = initialize("pom_noParametersSet.xml");

        mojo.execute();

        assertFalse(new File(getProject().getBasedir(), "pom-glue.xml").exists());
    }


    public void test_fromLib() throws Exception {
        IdeaGlueMojo mojo = initialize("pom.xml");
        mojo.setWorkingDir(new File(IdeaGlueMojo.LIB_DIR, "my-dir"));
        MavenProject parentProject = new MavenProject();
        parentProject.setArtifactId("codjo-pom-library");
        getProject().setParent(parentProject);

        mojo.execute();

        assertPomGlue("pom_fromLib-glue-etalon.xml");
    }


    public void test_fromPlugin() throws Exception {
        IdeaGlueMojo mojo = initialize("pom.xml");
        mojo.setWorkingDir(new File(IdeaGlueMojo.PLUGIN_DIR, "my-dir"));
        getProject().setPackaging("maven-plugin");

        mojo.execute();

        assertPomGlue("pom_fromPlugin-glue-etalon.xml");
    }


    public void test_fromSoftware() throws Exception {
        IdeaGlueMojo mojo = initialize("pom.xml");
        mojo.setWorkingDir(new File(IdeaGlueMojo.SOFTWARE_DIR, "my-dir"));
        customizeApplicationProject();

        mojo.execute();

        assertPomGlue("pom_fromSoftware-glue-etalon.xml");
    }


    public void test_fromUnknownType() throws Exception {
        IdeaGlueMojo mojo = initialize("pom.xml");
        mojo.setWorkingDir(File.createTempFile("my-pom", ".xml").getParentFile());

        try {
            mojo.execute();
            fail();
        }
        catch (Exception e) {
            assertEquals("Generation en erreur : Type de projet non supporté.", e.getLocalizedMessage());
        }
    }


    public void test_onlyLib() throws Exception {
        IdeaGlueMojo mojo = initialize("pom_onlyLib.xml");
        mojo.setWorkingDir(new File(IdeaGlueMojo.PROJECTS_DIR, "my-dir"));

        mojo.execute();

        assertPomGlue("pom_onlyLib-glue-etalon.xml");
    }


    private IdeaGlueMojo initialize(String pomFileName) throws Exception {
        setupEnvironment("/mojos/idea-glue/" + pomFileName);
        Mojo mojo = lookupMojo("idea-glue");
        initializeProject();
        return (IdeaGlueMojo)mojo;
    }


    private void assertPomGlue(String etalonFileName) throws IOException {
        File expected = PathUtil.find(getClass(), "/mojos/idea-glue/" + etalonFileName);
        File actual = new File(getProject().getBasedir(), "pom-glue.xml");
        XmlUtil.assertEquals(FileUtil.loadContent(expected), FileUtil.loadContent(actual));
    }


    private void initializeProject() {
        getProject().setGroupId("my-group");
        getProject().setArtifactId("my-id");
        getProject().setName("One project");
        getProject().setVersion("1.0");
        getProject().setUrl("http://here.com");
    }


    private void customizeApplicationProject() {
        List modules = new ArrayList();
        modules.add("my-id-datagen");
        modules.add("my-id-server");
        modules.add("my-id-client");
        getProject().getModel().setModules(modules);
        getProject().getModel().setPackaging("pom");
    }
}
