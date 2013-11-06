package net.codjo.maven.mojo.codjo;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
/**
 * @goal idea-glue
 * @aggregator
 */
public class IdeaGlueMojo extends AbstractMojo {
    public static final File PROJECTS_DIR = new File("c:\\dev\\projects");
    public static final File LIB_DIR = new File("c:\\dev\\projects\\codjo\\lib");
    public static final File PLUGIN_DIR = new File("c:\\dev\\projects\\codjo\\plugin");
    public static final File SOFTWARE_DIR = new File("c:\\dev\\projects\\codjo\\software");

    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     * @noinspection UnusedDeclaration
     */
    private MavenProject project;

    /**
     * @parameter expression="${appli}"
     * @readonly
     * @noinspection UnusedDeclaration
     */
    private String appli;

    /**
     * @parameter expression="${lib}"
     * @readonly
     * @noinspection UnusedDeclaration
     */
    private String lib;

    /**
     * @parameter expression="${plugin}"
     * @readonly
     * @noinspection UnusedDeclaration
     */
    private String plugin;

    /**
     * @parameter expression="${workingDir}" default-value="${project.basedir}"
     * @readonly
     * @noinspection UnusedDeclaration
     */
    private File workingDir;


    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            if (appli != null || lib != null || plugin != null) {
                executeImpl();
            }
        }
        catch (Exception e) {
            throw new MojoExecutionException("Generation en erreur : " + e.getMessage(), e);
        }
    }


    public File getWorkingDir() {
        return workingDir;
    }


    protected void setWorkingDir(File workingDir) {
        this.workingDir = workingDir;
    }


    private void executeImpl() throws IOException, MojoExecutionException {
        StringBuilder buffer = new StringBuilder()
              .append("<project>\n")
              .append("\n")
              .append("    <modelVersion>4.0.0</modelVersion>\n")
              .append("\n")
              .append("    <groupId>").append(project.getGroupId()).append("</groupId>\n")
              .append("    <artifactId>").append(project.getArtifactId()).append("-glue</artifactId>\n")
              .append("    <version>SNAPSHOT</version>\n")
              .append("\n");

        if (ProjectType.ROOT_PROJECT.match(project)) {
            buffer.append("    <parent>\n")
                  .append("        <groupId>").append(project.getGroupId()).append("</groupId>\n")
                  .append("        <artifactId>").append(project.getArtifactId()).append("</artifactId>\n")
                  .append("        <version>").append(project.getVersion()).append("</version>\n")
                  .append("    </parent>\n")
                  .append("\n");
        }

        buffer.append("    <name>").append(project.getName()).append(" Glue</name>\n")
              .append("    <url/>\n")
              .append("\n")
              .append("    <packaging>pom</packaging>\n")
              .append("    <modules>\n")
              .append("        <module/>\n");
        if (appli != null) {
            appendProjects(buffer);
        }
        if (lib != null) {
            appendLibs(buffer);
        }
        if (plugin != null) {
            appendPlugins(buffer);
        }
        buffer.append("    </modules>\n")
              .append("\n")
              .append("</project>");
        saveContent(new File(project.getBasedir(), "pom-glue.xml"), buffer.toString());
    }


    private boolean isLibrary() {
        return LIB_DIR.equals(workingDir.getParentFile());
    }


    private boolean isPlugin() {
        return PLUGIN_DIR.equals(workingDir.getParentFile());
    }


    private boolean isProject() {
        return PROJECTS_DIR.equals(workingDir.getParentFile());
    }


    private boolean isSoftware() {
        return SOFTWARE_DIR.equals(workingDir.getParentFile());
    }


    private void appendProjects(StringBuilder buffer) throws MojoExecutionException {
        for (Iterator it = split(appli); it.hasNext();) {
            appendProject(buffer, (String)it.next());
        }
    }


    private void appendProject(StringBuilder buffer, String projectName) throws MojoExecutionException {
        buffer.append("        <module>");
        if (isLibrary()) {
            buffer.append("../../../");
        }
        else if (isPlugin()) {
            buffer.append("../../../../");
        }
        else if (isProject()) {
            buffer.append("../");
        }
        else if (isSoftware()) {
            buffer.append("../../../");
        }
        else {
            throwUnsupportedProjectType();
        }
        buffer.append(projectName).append("</module>\n");
    }


    private void throwUnsupportedProjectType() throws MojoExecutionException {
        throw new MojoExecutionException("Type de projet non supporté.");
    }


    private void appendLibs(StringBuilder buffer) throws MojoExecutionException {
        for (Iterator it = split(lib); it.hasNext();) {
            appendLibrary(buffer, (String)it.next());
        }
    }


    private void appendLibrary(StringBuilder buffer, String libraryName) throws MojoExecutionException {
        buffer.append("        <module>");
        if (isLibrary()) {
            buffer.append("../");
        }
        else if (isPlugin()) {
            buffer.append("../../../lib/");
        }
        else if (isProject()) {
            buffer.append("../codjo/lib/");
        }
        else if (isSoftware()) {
            buffer.append("../../lib/");
        }
        else {
            throwUnsupportedProjectType();
        }
        buffer.append("codjo-").append(libraryName).append("</module>\n");
    }


    private void appendPlugins(StringBuilder buffer) throws MojoExecutionException {
        for (Iterator it = split(plugin); it.hasNext();) {
            appendPlugin(buffer, (String)it.next());
        }
    }


    private void appendPlugin(StringBuilder buffer, String pluginName) throws MojoExecutionException {
        buffer.append("        <module>");
        if (isLibrary()) {
            buffer.append("../../maven/plugins/");
        }
        else if (isPlugin()) {
            buffer.append("../");
        }
        else if (isProject()) {
            buffer.append("../codjo/maven/plugins/");
        }
        else if (isSoftware()) {
            buffer.append("../../maven/plugins/");
        }
        else {
            throwUnsupportedProjectType();
        }
        buffer.append("maven-").append(pluginName).append("-plugin").append("</module>\n");
    }


    private static Iterator split(String text) {
        final String[] split = text.split(",");
        return new Iterator() {
            private int index = 0;


            public boolean hasNext() {
                return index < split.length;
            }


            public Object next() {
                return split[index++];
            }


            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }


    private void saveContent(File file, String fileContent) throws IOException {
        Writer writer = new BufferedWriter(new FileWriter(file));
        try {
            writer.write(fileContent);
        }
        finally {
            writer.close();
        }
    }
}
