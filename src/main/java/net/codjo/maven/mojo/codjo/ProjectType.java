package net.codjo.maven.mojo.codjo;
import net.codjo.util.file.FileUtil;
import java.io.IOException;
import org.apache.maven.project.MavenProject;
/**
 *
 */
public class ProjectType {
    private final String label;
    public static final ProjectType UNKNOWN = new ProjectType("Projet de type inconnu");
    public static final ProjectType SUPER_POM = new SuperPomType();
    public static final ProjectType ROOT_PROJECT = new RootProjectType();
    public static final ProjectType PLUGIN = new PluginType();
    public static final ProjectType LIBRARY_PROJECT = new LibraryType();
    public static final ProjectType RELEASE_TEST_PROJECT = new ReleaseTestProjectType();
    public static final ProjectType SQL_PROJECT = new SqlProjectType();
    public static final ProjectType DATAGEN_PROJECT = new DatagenProjectType();

    public static final ProjectType[] TYPES = {UNKNOWN, SUPER_POM, ROOT_PROJECT, PLUGIN, RELEASE_TEST_PROJECT,
                                               LIBRARY_PROJECT, SQL_PROJECT, DATAGEN_PROJECT};


    private ProjectType(String label) {
        this.label = label;
    }


    public String toString() {
        return label;
    }


    public boolean match(MavenProject project) {
        return false;
    }


    public String getHelp() {
        return "n/a";
    }


    protected String loadContent(String resourceURI) {
        try {
            return FileUtil.loadContent(getClass().getResource(resourceURI));
        }
        catch (IOException e) {
            return "impossible de charger l'aide";
        }
    }


    private static class PluginType extends ProjectType {

        private PluginType() {
            super("Projet plugin");
        }


        public boolean match(MavenProject project) {
            return "maven-plugin".equals(project.getPackaging());
        }
    }

    private static class SuperPomType extends ProjectType {

        private SuperPomType() {
            super("Projet super-pom");
        }


        public boolean match(MavenProject project) {
            return !project.hasParent() && "codjo-pom".equals(project.getArtifactId());
        }


        public String getHelp() {
            return "\n\n" + loadContent("/help/SuperPomProject.txt");
        }
    }

    private static class LibraryType extends ProjectType {

        private LibraryType() {
            super("Projet librairie");
        }


        public boolean match(MavenProject project) {
            return project.hasParent() && "codjo-pom-library".equals(project.getParent().getArtifactId());
        }


        public String getHelp() {
            return "\n\n" + loadContent("/help/LibraryProject.txt");
        }
    }

    private static class RootProjectType extends ProjectType {

        private RootProjectType() {
            super("Projet Racine");
        }


        public boolean match(MavenProject project) {
            return "pom".equals(project.getPackaging()) && !project.getModules().isEmpty();
        }


        public String getHelp() {
            return "\n\n" + loadContent("/help/RootProject.txt");
        }
    }

    private static class ReleaseTestProjectType extends ProjectType {

        private ReleaseTestProjectType() {
            super("Projet Test-release");
        }


        public boolean match(MavenProject project) {
            return project.getArtifactId().endsWith("-release-test");
        }


        public String getHelp() {
            return "\n\n" + loadContent("/help/TestReleaseProject.txt");
        }
    }

    private static class SqlProjectType extends ProjectType {

        private SqlProjectType() {
            super("Projet SQL");
        }


        public boolean match(MavenProject project) {
            return project.getArtifactId().endsWith("-sql");
        }


        public String getHelp() {
            return "\n\n" + loadContent("/help/SqlProject.txt");
        }
    }

    private static class DatagenProjectType extends ProjectType {

        private DatagenProjectType() {
            super("Projet datagen");
        }


        public boolean match(MavenProject project) {
            return project.getArtifactId().endsWith("-datagen");
        }


        public String getHelp() {
            return "\n\n" + loadContent("/help/DatagenProject.txt");
        }
    }
}
