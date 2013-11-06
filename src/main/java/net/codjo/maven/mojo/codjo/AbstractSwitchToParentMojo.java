package net.codjo.maven.mojo.codjo;
import net.codjo.maven.mojo.codjo.scm.CommitCommand;
import net.codjo.maven.mojo.codjo.scm.CommitConfig;
import java.io.IOException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.scm.ScmFileSet;
import org.jdom.JDOMException;
/**
 *
 */
public abstract class AbstractSwitchToParentMojo extends ScmMojo implements CommitConfig {
    private CommitCommand commitCommand = new CommitCommand();


    public void setCommitCommand(CommitCommand commitCommand) {
        this.commitCommand = commitCommand;
    }


    public ScmFileSet getScmFileSet() {
        return new ScmFileSet(project.getBasedir(), project.getFile());
    }


    protected void changeParentVersionAndCommit(String newVersion)
          throws IOException, MojoExecutionException, MojoFailureException, JDOMException {
        changeParentVersion(newVersion);

        commitPom();
    }


    private void commitPom() throws IOException, MojoExecutionException {
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
            getLog().info("\t\tCommit '" + getLogMessage() + "' effectuee");
            getLog().info("");
        }
    }


    private void changeParentVersion(String newVersion)
          throws IOException, JDOMException, MojoExecutionException, MojoFailureException {
        getLog().info("");
        getLog().info("- Bascule du parent vers la version " + newVersion);
        PomCustomizer.applyCustomizer(PomCustomizer.changeParentVersion(newVersion), project);
        getLog().info("");
        getLog().info("\t\tOk");
    }
}
