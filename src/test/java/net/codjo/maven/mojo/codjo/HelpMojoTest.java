package net.codjo.maven.mojo.codjo;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.apache.maven.project.MavenProject;
/**
 *
 */
public class HelpMojoTest extends TestCase {
    public void test_helpType() throws Exception {
        MavenProject project = new MavenProject();
        assertEquals(ProjectType.UNKNOWN, HelpMojo.toProjectType(project));

        project.setPackaging("maven-plugin");
        assertEquals(ProjectType.PLUGIN, HelpMojo.toProjectType(project));
    }


    public void test_root() throws Exception {
        MavenProject project = new MavenProject();
        project.setPackaging("pom");

        List modules = new ArrayList();
        modules.add("myapp-datagen");
        project.getModel().setModules(modules);

        assertEquals(ProjectType.ROOT_PROJECT, HelpMojo.toProjectType(project));
    }


    public void test_releaseTest() throws Exception {
        MavenProject project = new MavenProject();
        project.setPackaging("pom");
        project.setArtifactId("myapp-release-test");

        assertEquals(ProjectType.RELEASE_TEST_PROJECT, HelpMojo.toProjectType(project));
    }


    public void test_superPomProject() throws Exception {
        MavenProject project = new MavenProject();
        project.setPackaging("pom");
        project.setArtifactId("codjo-pom");
        ProjectType actual = HelpMojo.toProjectType(project);
        assertFalse(actual.getHelp().contains("impossible de charger l'aide"));
        assertEquals(ProjectType.SUPER_POM, actual);
    }


    public void test_sqlProject() throws Exception {
        MavenProject project = new MavenProject();
        project.setPackaging("pom");
        project.setArtifactId("myapp-sql");
        ProjectType actual = HelpMojo.toProjectType(project);
        assertFalse(actual.getHelp().contains("impossible de charger l'aide"));
        assertEquals(ProjectType.SQL_PROJECT, actual);
    }


    public void test_datagenProject() throws Exception {
        MavenProject project = new MavenProject();
        project.setPackaging("pom");
        project.setArtifactId("myapp-datagen");
        ProjectType actual = HelpMojo.toProjectType(project);
        assertFalse(actual.getHelp().contains("impossible de charger l'aide"));
        assertEquals(ProjectType.DATAGEN_PROJECT, actual);
    }


    public void test_libraryProject() throws Exception {
        MavenProject project = new MavenProject();
        MavenProject parent = new MavenProject();
        parent.setArtifactId("codjo-pom-library");
        project.setParent(parent);
        ProjectType actual = HelpMojo.toProjectType(project);
        assertFalse(actual.getHelp().contains("impossible de charger l'aide"));
        assertEquals(ProjectType.LIBRARY_PROJECT, actual);
    }
}
