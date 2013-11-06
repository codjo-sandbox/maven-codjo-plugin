package net.codjo.maven.mojo.codjo;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
/**
 * Bascule le parent sur la derniere version stable disponible.
 *
 * @goal switch-to-parent-snapshot
 * @aggregator
 */
public class SwitchToParentSnapshotMojo extends AbstractSwitchToParentMojo {

    public String getLogMessage() {
        return "Switch parent pom to the codjo-pom snapshot version";
    }


    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            changeParentVersionAndCommit(Artifact.SNAPSHOT_VERSION);
        }
        catch (Exception error) {
            getLog().error(error.getLocalizedMessage(), error);
            throw new MojoExecutionException("Impossible de changer la version du POM parent", error);
        }
    }
}
