package net.codjo.maven.mojo.codjo;
import net.codjo.maven.common.mock.AgfMojoComponent;
import java.util.List;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
/**
 *
 */
public class PomUtils {
    private PomUtils() {
    }


    public static void mockLastPomVersion(String lastVersion) throws ComponentLookupException {
        RepositoryMetadataManagerMock repo = (RepositoryMetadataManagerMock)
              AgfMojoComponent.getComponent(RepositoryMetadataManagerMock.class);
        repo.mockResolveRelease(lastVersion);
    }


    public static void mockOldPomVersions(List oldVersions) throws ComponentLookupException {
        RepositoryMetadataManagerMock repo = (RepositoryMetadataManagerMock)
              AgfMojoComponent.getComponent(RepositoryMetadataManagerMock.class);
        repo.mockAllResolvedRelease(oldVersions);
    }
}

