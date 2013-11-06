package net.codjo.maven.mojo.codjo;
import net.codjo.confluence.ConfluenceException;
import net.codjo.confluence.ConfluenceServer;
import net.codjo.confluence.ConfluenceSession;
import net.codjo.confluence.Page;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
/**
 * @goal update-confluence-before-release
 * @aggregator
 */
public class UpdateConfluenceBeforeReleaseMojo extends UpdateConfluenceMojo {

    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            updateConfluenceBeforeRelease();
        }
        catch (Exception error) {
            getLog().error(error.getLocalizedMessage(), error);
            throw new MojoExecutionException(
                  "Impossible de mmetre à jour les pages de confluence avant la stabilisation", error);
        }
    }


    private void updateConfluenceBeforeRelease() throws ConfluenceException {
        ConfluenceServer server = new ConfluenceServer(
              new ConfluenceSession(confluenceUrl, confluenceUser, confluencePassword));
        server.login();
        try {
            Page page = server.getPage(confluenceSpaceKey, confluencePage);
            updateHomePage(page);
            server.storePage(page);
        }
        finally {
            server.logout();
        }
    }


    public void updateHomePage(Page homePage) {
        String homeContent = homePage.getContent();
        homeContent = addWarning(homeContent);
        homePage.setContent(homeContent);
    }


    private String addWarning(String homeContent) {
        String title = "h2. Framework";
        return homeContent.replaceFirst(title, title + WARNING_RELEASE_STARTED);
    }
}
