package net.codjo.maven.mojo.codjo;
import net.codjo.confluence.ConfluenceException;
import net.codjo.confluence.ConfluenceServer;
import net.codjo.confluence.ConfluenceSession;
import net.codjo.confluence.Page;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
/**
 * @goal update-confluence-after-release
 * @aggregator
 */
public class UpdateConfluenceAfterReleaseMojo extends UpdateConfluenceMojo {
    private String releasedVersion;
    /**
     * @parameter expression="${librairiesPage}" default-value="Librairies Internes"
     * @noinspection UnusedDeclaration
     */
    protected String librairiesPage;
    protected static final String SNAPSHOT_LABEL_NAME = "snapshot";


    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            releasedVersion = findLastFrameworkVersion();
            updateConfluenceAfterRelease();
        }
        catch (Exception error) {
            getLog().error(error.getLocalizedMessage(), error);
            throw new MojoExecutionException(
                  "Impossible de mmetre à jour les pages de confluence après la stabilisation (pour la version "
                  + releasedVersion + ")",
                  error);
        }
    }


    private void updateConfluenceAfterRelease() throws ConfluenceException {
        ConfluenceServer server = new ConfluenceServer(
              new ConfluenceSession(confluenceUrl, confluenceUser, confluencePassword));
        server.login();
        try {
            createFrameworkNewVersionPage(server);
            server.removeLabelByName(SNAPSHOT_LABEL_NAME);

            Page homePage = server.getPage(confluenceSpaceKey, confluencePage);
            updateHomePage(homePage);
            server.storePage(homePage);
        }
        finally {
            server.logout();
        }
    }


    private void createFrameworkNewVersionPage(ConfluenceServer server)
          throws ConfluenceException {
        Page page = new Page();
        String title = "framework-" + tag(removeFixVersion(releasedVersion));
        page.setTitle(title);
        page.setSpaceKey(confluenceSpaceKey);

        page.setParentPageId(server.getPage(confluenceSpaceKey, "Changelog framework").getId());

        String content = "h1. Version\n"
                         + "\n"
                         + "La version est " + releasedVersion + "\n"
                         + "\n"
                         + "h1. Bugs connus\n"
                         + "{excerpt}{excerpt} n/a\n"
                         + "\\\\\n"
                         + "h1. Changelog\n"
                         + "{composition-setup}\n"
                         + "{deck:id=hotTopic}\n"
                         + "{card:label=Hot topics}\n"
                         + "{blog-posts:50|match-labels=all|labels=hot-topics , " + title
                         + "|content=titles}\n"
                         + "{card}\n"
                         + "{deck}\n"
                         + "{div:style=margin-top:10px}\n"
                         + "{deck:id=unicId}\n"
                         + "{card:label=Modifications}\n"
                         + "{blog-posts:50|labels=" + title + "}\n"
                         + "{card}\n"
                         + "{card:label=Librairies}\n"
                         + "{related-labels:labels=" + title + "}\n"
                         + "{card}\n"
                         + "{deck}";

        page.setContent(content);
        server.storePage(page);
    }


    private void updateHomePage(Page homePage) {
        String homeContent = homePage.getContent();
        homeContent = changeVersion(homeContent, releasedVersion);
        homeContent = homeContent.replace(UpdateConfluenceMojo.WARNING_RELEASE_STARTED, "");
        homePage.setContent(homeContent);
    }


    private String changeVersion(String homeContent, String releasedVersion) {
        String nextVersionName = computeVersionName(1, '.');
        String previousVersionName = computeVersionName(-1, '.');

        String lastVersionName = removeFixVersion(releasedVersion);
        String lastVersionTag = tag(lastVersionName);
        String previousVersionTag = tag(previousVersionName);
        String nextVersionTag = tag(nextVersionName);

        homeContent = homeContent.replace(lastVersionName, nextVersionName);
        homeContent = homeContent.replace(lastVersionTag, nextVersionTag);
        homeContent = homeContent.replaceAll(previousVersionName.replaceAll("\\.", "\\\\.") + "[\\.0-9]*",
                                             releasedVersion);
        homeContent = homeContent.replaceAll(previousVersionTag.replaceAll("\\-", "\\\\-") + "[\\-0-9]*",
                                             lastVersionTag);

        return homeContent;
    }


    public String computeVersionName(int indice, char separator) {
        String lastVersionName = removeFixVersion(releasedVersion);
        int separatorIndex = lastVersionName.lastIndexOf(separator) + 1;
        int version = Integer.valueOf(lastVersionName.substring(separatorIndex, lastVersionName.length()))
              .intValue();
        int newVersion;
        newVersion = version + indice;
        return lastVersionName.substring(0, separatorIndex) + newVersion;
    }


    private String tag(String versionName) {
        return versionName.replace('.', '-');
    }
}
