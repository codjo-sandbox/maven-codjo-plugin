package net.codjo.maven.mojo.codjo;
import net.codjo.maven.common.mock.AgfMojoComponent;
import java.util.ArrayList;
import java.util.List;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.metadata.DefaultRepositoryMetadataManager;
import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadata;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadataResolutionException;
import org.apache.maven.artifact.repository.metadata.Versioning;
import org.codehaus.plexus.logging.console.ConsoleLogger;
/**
 *
 */
public class RepositoryMetadataManagerMock extends DefaultRepositoryMetadataManager {
    private String lastVersion = "n/a";
    private List oldVersions = new ArrayList();


    public RepositoryMetadataManagerMock() {
        AgfMojoComponent.declareComponent(this);
        enableLogging(new ConsoleLogger(ConsoleLogger.LEVEL_DEBUG, getClass().getSimpleName()));
    }


    public void resolve(RepositoryMetadata metadata,
                        List remoteRepositories,
                        ArtifactRepository localRepository) throws RepositoryMetadataResolutionException {
        Metadata anotherMetadata = new Metadata();
        Versioning versioning = new Versioning();
        if (!oldVersions.isEmpty()) {
            versioning.setVersions(oldVersions);
            lastVersion = (String)oldVersions.get(oldVersions.size() - 1);
        }
        versioning.setRelease(lastVersion);
        anotherMetadata.setVersioning(versioning);
        metadata.setMetadata(anotherMetadata);
    }


    public void mockResolveRelease(String version) {
        lastVersion = version;
        oldVersions.add(version);
    }


    public void mockAllResolvedRelease(List versions) {
        oldVersions = versions;
    }
}
