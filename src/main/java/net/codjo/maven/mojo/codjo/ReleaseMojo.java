package net.codjo.maven.mojo.codjo;
import java.io.File;
import java.util.List;
import net.codjo.util.file.FileUtil;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadataManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
/**
 * Bascule les lib dans le pom parent sur leur futur version stable et génère le fichier release.properties.
 *
 * @goal release
 * @aggregator
 */
public class ReleaseMojo extends SwitchToReleaseMojo {
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

    /**
     * @parameter expression="${releaseType}" default-value="stable"
     * @noinspection UnusedDeclaration
     */
    private String releaseType;


    public void setReactorProjects(List reactorProjects) {
        this.reactorProjects = reactorProjects;
    }


    /**
     * The projects in the reactor.
     *
     * @parameter expression="${reactorProjects}"
     * @readonly
     */
    private List reactorProjects;

    protected static final String STABLE_VERSION = "stable";
    protected static final String PATCH_VERSION = "patch";
    protected static final String RELEASE_CANDIDATE_VERSION = "rc";


    public void setReleaseType(String releaseType) {
        this.releaseType = releaseType;
    }


    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            String nextReleaseVersion = determineNewVersion();
            super.execute();

            String content = buildReleasePropertiesTemplate()
                  .replace("${NextVersion}", nextReleaseVersion);

            String scmUrl = getConnectionUrl().replace("scm:svn:", "scm\\:svn\\:");
            content = content.replace("${scm-url}", scmUrl);

            FileUtil.saveContent(new File(project.getBasedir(), "release.properties"),
                                 content);
        }
        catch (MojoExecutionException e) {
            throw e;
        }
        catch (MojoFailureException e) {
            throw e;
        }
        catch (Exception e) {
            throw new MojoFailureException(e, "Impossible de genere le fichier release.properties",
                                           "Impossible de genere le fichier release.properties pre-rempli avec les versions...");
        }
    }


    private String buildReleasePropertiesTemplate() {
        String header = "#release configuration\n";
        StringBuffer template = new StringBuffer(header);

        if (reactorProjects != null) {
            for (int i = 0, reactorProjectsSize = reactorProjects.size(); i < reactorProjectsSize; i++) {
                MavenProject reactorProject = (MavenProject)reactorProjects.get(i);
                appendVersions(template, reactorProject.getArtifactId());
            }
        }

        template.append("scm.tag=").append(project.getArtifactId()).append("-${NextVersion}\n")
              .append("scm.url=${scm-url}\n").append("completedPhase=map-development-versions\n");

        return template.toString();
    }


    private void appendVersions(StringBuffer template, String module) {
        template.append("project.dev.net.codjo.pom\\:").append(module).append("=SNAPSHOT\n");
        template.append("project.rel.net.codjo.pom\\:").append(module).append("=${NextVersion}\n");
    }


    private String determineNewVersion() throws Exception {
        VersionUtil versionUtil = new VersionUtil(artifactFactory,
                                                  repositoryMetadataManager,
                                                  project.getRemoteArtifactRepositories(),
                                                  localRepository);

        getLog().info("Determination de la nouvelle version " + releaseType);
        String newVersion;
        if (STABLE_VERSION.equals(releaseType)) {
            newVersion = versionUtil.determineNextReleaseVersion(project.getArtifact());
        }
        else if (PATCH_VERSION.equals(releaseType)) {
            newVersion = versionUtil.determineNextPatchVersion(project.getArtifact());
        }
        else if (RELEASE_CANDIDATE_VERSION.equals(releaseType)) {
            throw new MojoFailureException("Type de release not implemented");
        }
        else {
            throw new MojoFailureException("Type de release inconnu");
        }

        getLog().info("\t -> version = " + newVersion);

        return newVersion;
    }
}
