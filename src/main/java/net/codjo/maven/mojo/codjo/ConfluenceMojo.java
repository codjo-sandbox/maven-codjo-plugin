package net.codjo.maven.mojo.codjo;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadataManager;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;
/**
 *
 */
public abstract class ConfluenceMojo extends AbstractMojo {
    /**
     * @parameter expression="${confluenceUrl}" default-value="http://wp-confluence/confluence"
     * @noinspection UnusedDeclaration
     */
    protected String confluenceUrl = "http://wp-confluence/confluence";
    /**
     * @parameter expression="${confluenceUser}" default-value="amdsidev"
     * @noinspection UnusedDeclaration
     */
    protected String confluenceUser;
    /**
     * @parameter expression="${confluencePassword}" default-value="amdsidev"
     * @noinspection UnusedDeclaration
     */
    protected String confluencePassword;
    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     * @noinspection UnusedDeclaration
     */
    protected MavenProject project;
    /**
     * Local maven repository.
     *
     * @parameter expression="${localRepository}"
     * @required
     * @readonly
     * @noinspection UnusedDeclaration
     */
    protected ArtifactRepository localRepository;
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
    protected RepositoryMetadataManager repositoryMetadataManager;


    protected String findLastFrameworkVersion() throws Exception {
        VersionUtil versionUtil = new VersionUtil(artifactFactory, repositoryMetadataManager,
                                                  project.getRemoteArtifactRepositories(), localRepository);

        return versionUtil.findLastVersion(project.getArtifact());
    }


    protected String removeFixVersion(String frameworkVersion) {
        int firstPoint = frameworkVersion.indexOf('.');
        int lastPointIndex = frameworkVersion.indexOf('.', firstPoint + 1);
        if (lastPointIndex == -1) {
            return frameworkVersion;
        }
        return frameworkVersion.substring(0, lastPointIndex);
    }
}
