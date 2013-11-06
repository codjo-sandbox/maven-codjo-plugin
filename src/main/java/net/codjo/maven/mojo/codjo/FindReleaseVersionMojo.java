package net.codjo.maven.mojo.codjo;
import java.util.HashSet;
import java.util.Set;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataRetrievalException;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadataManager;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadataResolutionException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
/**
 * Cible permettant d'afficher la derniere version stable du framework.
 *
 * @goal find-release-version
 * @requiresProject false
 */
public class FindReleaseVersionMojo extends AbstractMojo {
    /**
     * @parameter expression="${project}"
     * @readonly
     * @noinspection UNUSED_SYMBOL
     */
    private MavenProject project;

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


    public void execute() throws MojoExecutionException {
        try {
            executeImpl();
        }
        catch (Exception e) {
            throw new MojoExecutionException(
                  "Determination de la derniere version stable du framework en echec : " + e.getLocalizedMessage(), e);
        }
    }


    private void executeImpl() throws Exception {
        VersionUtil versionUtil = new VersionUtil(artifactFactory,
                                                  repositoryMetadataManager,
                                                  project.getRemoteArtifactRepositories(),
                                                  localRepository);

        Set controlVersion = new HashSet();

        StringBuilder builder = new StringBuilder();
        builder.append(getVersion(versionUtil, "codjo-pom", controlVersion));
        builder.append(getVersion(versionUtil, "codjo-pom-plugin", controlVersion));
        builder.append(getVersion(versionUtil, "codjo-pom-library", controlVersion));
        builder.append(getVersion(versionUtil, "codjo-pom-application", controlVersion));
        builder.append(getVersion(versionUtil, "codjo-pom-external", controlVersion));
        builder.append(getVersion(versionUtil, "codjo-pom-agif", controlVersion));

        if (controlVersion.size() > 1 || controlVersion.isEmpty()) {
            getLog().error("Les versions ne sont pas toutes cohérentes");
            getLog().info(builder.toString());
        }
        else {
           getLog().info("\n\nVoici la version du framework: "+controlVersion.toArray()[0]+"\n");
        }
    }


    private String getVersion(VersionUtil versionUtil, String artifactId, Set controlVersion)
          throws MojoExecutionException, RepositoryMetadataResolutionException, ArtifactMetadataRetrievalException,
                 MojoFailureException {
        String lastVersion = versionUtil.findLastVersion(
              artifactFactory.createArtifact("net.codjo.pom", artifactId, "SNAPSHOT", null, "pom"));
        controlVersion.add(lastVersion);
        return "\t" + artifactId + ": " + lastVersion + "\n";
    }
}
