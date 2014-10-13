package net.codjo.maven.mojo.codjo;
import java.io.File;
import java.io.IOException;
import net.codjo.maven.mojo.codjo.scm.CommitCommand;
import net.codjo.maven.mojo.codjo.scm.CommitConfig;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.scm.ScmFileSet;
/**
 *
 */
public abstract class SwitchAbstractMojo extends ScmMojo implements CommitConfig {
    private CommitCommand commitCommand = new CommitCommand();
    protected SwitchPomCustomizer myCustomizer;


    public void setProject(MavenProject project) {
        this.project = project;
    }


    public void setCommitCommand(CommitCommand commitCommand) {
        this.commitCommand = commitCommand;
    }


    public ScmFileSet getScmFileSet() {
        return new ScmFileSet(project.getBasedir());
    }


    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            if (myCustomizer == null) {
                throw new MojoExecutionException("Aucun customizer n'a été spécifié");
            }

            for (int i = 0; i < project.getModules().size(); i++) {
                PomCustomizer.applyCustomizer(myCustomizer, getChildModuleProject(i));
            }
            PomCustomizer.applyCustomizer(myCustomizer, project);

            myCustomizer.printLog();

            if (proposeCommit()) {
                commitPom();
            }
        }
        catch (Exception error) {
            throw new MojoExecutionException(
                  "Impossible de changer la version des librairies : " + error.getLocalizedMessage(), error);
        }
    }


    abstract protected boolean proposeCommit();


    protected void commitPom() throws IOException, MojoExecutionException {
        String response = "yes";
        if (settings.isInteractiveMode()) {
            getLog().info("");
            getLog().info("- Voulez-vous commiter les modifications ? (y/n)");
            response = inputHandler.readLine();
        }
        else {
            getLog().info("");
            getLog().info("- Commit de la bascule");
        }

        if ("y".equals(response.trim().toLowerCase()) || "yes".equals(response.trim().toLowerCase())) {
            commitCommand.execute(this);
            getLog().info("");
            getLog().info("\t\tCommit '" + getLogMessage() + "' effectue");
            getLog().info("");
        }
    }


    private MavenProject getChildModuleProject(int moduleIndex) throws Exception {
        MavenProject childModule = new MavenProject();
        childModule.setFile(new File(
              project.getFile().getParent() + "\\" +
              project.getModules().get(moduleIndex) + "\\" +
              project.getFile().getName()));
        return childModule;
    }


    protected interface SwitchPomCustomizer extends PomCustomizer.Customizer {
        public static String[] libTagsHierarchy = new String[]{"dependencyManagement",
                                                               "dependencies",
                                                               "dependency"};
        public static String[] libTagsHierarchy2 = new String[]{"build",
                                                                "plugins",
                                                                "plugin",
                                                                "dependencies",
                                                                "dependency"};
        public static String[] libInPluginTagsHierarchy = new String[]{"build", "pluginManagement",
                                                                       "plugins",
                                                                       "plugin",
                                                                       "dependencies",
                                                                       "dependency"};
        public static String[] pluginTagsHierarchy = new String[]{"build",
                                                                  "pluginManagement",
                                                                  "plugins",
                                                                  "plugin"};


        public void printLog() throws MojoExecutionException, IOException;
    }
    protected static class VersionInfo {
        protected static final String AGI_SUFFIX = "-agi";

        private boolean isAGI = false;
        private final String version;


        public VersionInfo(String version) {
            if (version.endsWith(AGI_SUFFIX)) {
                version = version.substring(0, version.length() - AGI_SUFFIX.length());
                isAGI = true;
            }
            this.version = version;
        }


        public boolean isAGI() {
            return isAGI;
        }


        public String getVersion() {
            return version;
        }


        public String getSuffix() {
            return isAGI() ? AGI_SUFFIX : "";
        }
    }
}
