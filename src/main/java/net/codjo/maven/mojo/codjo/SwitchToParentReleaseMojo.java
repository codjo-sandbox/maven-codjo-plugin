package net.codjo.maven.mojo.codjo;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadataManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
/**
 * Bascule le parent sur la derniere version stable disponible.
 *
 * @goal switch-to-parent-release
 * @aggregator
 */
public class SwitchToParentReleaseMojo extends AbstractSwitchToParentMojo {

    /**
     * Local maven repository.
     *
     * @parameter expression="${localRepository}"
     * @required
     * @readonly
     * @noinspection UnusedDeclaration
     */
    private ArtifactRepository localRepository;
    /**
     * @parameter expression="${component.org.apache.maven.artifact.factory.ArtifactFactory}"
     * @required
     * @readonly
     */
    protected ArtifactFactory artifactFactory;
    /**
     * @parameter expression="${component.org.apache.maven.artifact.repository.metadata.RepositoryMetadataManager}"
     * @required
     * @readonly
     * @noinspection UnusedDeclaration
     */
    private RepositoryMetadataManager repositoryMetadataManager;


    public String getLogMessage() {
        return "Switch parent pom to the last codjo-pom stable version";
    }


    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            String lastParentVersion = findLastVersion();

            getLog().info("");
            getLog().info("");
            getLog().info("- Recuperation de la derniere version de '"
                          + project.getParent().getArtifactId() + "'");
            getLog().info("");
            getLog().info("\t\tVersion trouve : " + lastParentVersion);

            changeParentVersionAndCommit(lastParentVersion);
        }
        catch (Exception error) {
            getLog().error(error.getLocalizedMessage(), error);
            throw new MojoExecutionException("Impossible de changer la version du POM parent", error);
        }
    }


    private String findLastVersion() throws Exception {
        VersionUtil versionUtil = new VersionUtil(artifactFactory,
                                                  repositoryMetadataManager,
                                                  project.getRemoteArtifactRepositories(),
                                                  localRepository);

        return versionUtil.findLastStableParentVersion(project);
    }
}
