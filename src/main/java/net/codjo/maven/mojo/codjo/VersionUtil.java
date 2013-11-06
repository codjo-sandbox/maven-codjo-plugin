package net.codjo.maven.mojo.codjo;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataRetrievalException;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.metadata.ArtifactRepositoryMetadata;
import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadata;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadataManager;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadataResolutionException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.StringUtils;
public class VersionUtil {
    public static final String CODJO_REPOSITORY_ID = "agf-repository";
    protected ArtifactFactory artifactFactory;
    private RepositoryMetadataManager repositoryMetadataManager;
    private List remoteArtifactRepositories;
    private ArtifactRepository localRepository;


    public VersionUtil(ArtifactFactory artifactFactory,
                       RepositoryMetadataManager repositoryMetadataManager,
                       List remoteArtifactRepositories,
                       ArtifactRepository localRepository) {
        this.artifactFactory = artifactFactory;
        this.repositoryMetadataManager = repositoryMetadataManager;
        this.remoteArtifactRepositories = remoteArtifactRepositories;
        this.localRepository = localRepository;
    }


    public String findLastVersion(Artifact artifact) throws MojoExecutionException,
                                                            RepositoryMetadataResolutionException,
                                                            ArtifactMetadataRetrievalException,
                                                            MojoFailureException {
        updatePolicy();

        RepositoryMetadata repositoryMetadata = getMetadata(artifact);
        if (repositoryMetadata == null) {
            throw new MojoExecutionException("Impossible de déterminer la derniere version "
                                             + "d'un artifact provided");
        }
        Metadata metadata = repositoryMetadata.getMetadata();

        checkThatRepositoryIsAvailable(VersionUtil.CODJO_REPOSITORY_ID, remoteArtifactRepositories);

        return metadata.getVersioning().getRelease();
    }


    public String findLastParentVersion(MavenProject project) throws MojoExecutionException,
                                                                     RepositoryMetadataResolutionException,
                                                                     ArtifactMetadataRetrievalException,
                                                                     MojoFailureException {

        return findLastVersion(getParentArtifact(project));
    }


    public String findLastStableParentVersion(MavenProject project) throws Exception {
        Version highestStableVersion = findHighestVersionOf(getParentArtifact(project),
                                                            withType(Version.RELEASE_VERSION,
                                                                     Version.PATCH_VERSION));
        return highestStableVersion.toString();
    }


    public String determineNextReleaseVersion(Artifact artifact) throws Exception {
        Version highestVersion = findHighestVersionOf(artifact, allVersion());
        return highestVersion.nextRelease().toString();
    }


    public String determineNextPatchVersion(Artifact artifact) throws Exception {
        Version highestVersion = findHighestVersionOf(artifact, withType(Version.RELEASE_VERSION,
                                                                         Version.PATCH_VERSION));
        return highestVersion.nextPatch().toString();
    }


    public String determineNextReleaseCandidateVersion(Artifact artifact) throws Exception {
        Version highestVersion = findHighestVersionOf(artifact, withType(Version.RELEASE_VERSION,
                                                                         Version.RC_VERSION));
        return highestVersion.nextReleaseCandidate().toString();
    }


    private Version findHighestVersionOf(Artifact artifact, VersionSelector selector) throws Exception {
        updatePolicy();

        List versions = findVersions(artifact);
        for (int i = versions.size() - 1; i >= 0; i--) {
            Version version = (Version)versions.get(i);
            if (version.getType() != Version.BAD_VERSION && selector.match(version)) {
                return version;
            }
        }
        throw new MojoFailureException("Aucune version trouve : " + versions);
    }


    /**
     * @noinspection CollectionDeclaredAsConcreteClass
     */
    private LinkedList findVersions(Artifact artifact) throws MojoExecutionException,
                                                              RepositoryMetadataResolutionException,
                                                              ArtifactMetadataRetrievalException {
        RepositoryMetadata repositoryMetadata = getMetadata(artifact);

        if (repositoryMetadata == null) {
            throw new MojoExecutionException(
                  "Impossible de déterminer la derniere version d'un artifact provided");
        }
        Metadata metadata = repositoryMetadata.getMetadata();

        LinkedList list = new LinkedList();
        for (Iterator iterator = metadata.getVersioning().getVersions().iterator(); iterator.hasNext();) {
            list.add(new Version((String)iterator.next()));
        }

        Collections.sort(list, new VersionComparator());
        return list;
    }


    private Artifact getParentArtifact(MavenProject project) {
        MavenProject parent = project.getParent();
        return createArtifact(parent.getGroupId(), parent.getArtifactId(), null, null, null);
    }


    private Artifact createArtifact(String groupId, String artifactId, String version,
                                    String scope, String type) {
        return artifactFactory.createArtifact(groupId, artifactId,
                                              StringUtils.defaultString(version, Artifact.LATEST_VERSION),
                                              scope,
                                              StringUtils.defaultString(type, "jar"));
    }


    private RepositoryMetadata getMetadata(Artifact artifact)
          throws ArtifactMetadataRetrievalException, RepositoryMetadataResolutionException {
        RepositoryMetadata metadata = new ArtifactRepositoryMetadata(artifact);
        repositoryMetadataManager.resolve(metadata, remoteArtifactRepositories, localRepository);
        return metadata;
    }


    private void updatePolicy() throws MojoFailureException {
        getRepository(VersionUtil.CODJO_REPOSITORY_ID, remoteArtifactRepositories)
              .getReleases()
              .setUpdatePolicy(ArtifactRepositoryPolicy.UPDATE_POLICY_ALWAYS);
        checkThatRepositoryIsAvailable(VersionUtil.CODJO_REPOSITORY_ID, remoteArtifactRepositories);
    }


    private static void checkThatRepositoryIsAvailable(String repositoryId, List list)
          throws MojoFailureException {
        ArtifactRepository repository = getRepository(CODJO_REPOSITORY_ID, list);
        if (repository.isBlacklisted()) {
            throw new MojoFailureException("Repository '" + repositoryId + "' est inaccessible.");
        }
    }


    private static ArtifactRepository getRepository(String repositoryId, List list)
          throws MojoFailureException {
        for (int i = 0; i < list.size(); i++) {
            ArtifactRepository repository = (ArtifactRepository)list.get(i);
            if (repositoryId.equals(repository.getId())) {
                return repository;
            }
        }
        throw new MojoFailureException("Aucun repository '" + repositoryId + "' trouve.");
    }


    private static VersionSelector allVersion() {
        return new VersionSelector() {
            public boolean match(Version version) {
                return true;
            }
        };
    }


    private static VersionSelector withType(final int type1, final int type2) {
        return new VersionSelector() {
            public boolean match(Version version) {
                return version.getType() == type1 || version.getType() == type2;
            }
        };
    }


    private interface VersionSelector {
        boolean match(Version version);
    }
}
