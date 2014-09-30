package net.codjo.maven.mojo.codjo;
import net.codjo.confluence.ConfluenceException;
import net.codjo.confluence.ConfluenceServer;
import net.codjo.confluence.ConfluenceSession;
import net.codjo.confluence.Page;
import net.codjo.maven.common.mock.AgfMojoTestCase;
import org.apache.maven.plugin.Mojo;
/**
 *
 */
public class UpdateConfluenceAfterReleaseMojoTest extends AgfMojoTestCase {
    private String changelogFrameworkPageId;
    private ConfluenceServer server;


    public void test_empty() {

    }


    public void disabled_test_execute() throws Exception {
        setupEnvironment("/mojos/updateConfluence/pom-after-default.xml");

        Mojo mojo = lookupMojo("update-confluence-after-release");

        createFrameworkHomePage(homePageContent("1.18.1", "framework-1-18", "1.19", "framework-1-19"));
        PomUtils.mockLastPomVersion("1.19");

        assertEquals(1,
                     server.searchByLabelName(UpdateConfluenceAfterReleaseMojo.SNAPSHOT_LABEL_NAME).size());

        mojo.execute();

        assertEquals(homePageContent("1.19", "framework-1-19", "1.20", "framework-1-20"),
                     getFrameworkHomePage());
        assertChangelogPage("1.19", "framework-1-19");
        assertEquals(0,
                     server.searchByLabelName(UpdateConfluenceAfterReleaseMojo.SNAPSHOT_LABEL_NAME).size());
    }


    public void disabled_test_execute_versionPatch() throws Exception {
        setupEnvironment("/mojos/updateConfluence/pom-after-default.xml");

        Mojo mojo = lookupMojo("update-confluence-after-release");

        createFrameworkHomePage(homePageContent("1.18", "framework-1-18", "1.19", "framework-1-19"));
        PomUtils.mockLastPomVersion("1.19.1");

        mojo.execute();

        assertEquals(homePageContent("1.19.1", "framework-1-19", "1.20", "framework-1-20"),
                     getFrameworkHomePage());
        assertChangelogPage("1.19.1", "framework-1-19");
    }


    public void disabled_test_execute_previousVersionIsPatch() throws Exception {
        setupEnvironment("/mojos/updateConfluence/pom-after-default.xml");

        Mojo mojo = lookupMojo("update-confluence-after-release");

        createFrameworkHomePage(homePageContent("1.18.1", "framework-1-18-1", "1.19", "framework-1-19"));
        PomUtils.mockLastPomVersion("1.19");

        mojo.execute();

        assertEquals(homePageContent("1.19", "framework-1-19", "1.20", "framework-1-20"),
                     getFrameworkHomePage());
        assertChangelogPage("1.19", "framework-1-19");
    }


    private void createCoordinatorNeededPage() throws ConfluenceException {
        Page librairiesPage = createPage("Librairies Internes", null);
        Page myLib = createPage("codjo-malib", librairiesPage.getId());
        server.addLabel(UpdateConfluenceAfterReleaseMojo.SNAPSHOT_LABEL_NAME, myLib.getId());
        Page buildPage = createPage("Infrastructure Build", null);
        createPage("maven-my-plugin", buildPage.getId());
    }


    private Page createPage(String pageTitle, String parentId) throws ConfluenceException {
        Page page;
        try {
            page = server.getPage("sandbox", pageTitle);
        }
        catch (ConfluenceException e) {
            page = new Page();
            page.setTitle(pageTitle);
            if (parentId != null) {
                page.setParentPageId(parentId);
            }
            page.setSpaceKey("sandbox");
        }
        page.setContent(pageTitle);
        return server.storePage(page);
    }


    private void createFrameworkHomePage(String content) throws Exception {
        Page page;
        try {
            page = server.getPage("sandbox", "UpdateConfluenceAfterReleaseMojo");
        }
        catch (ConfluenceException e) {
            page = new Page();
            page.setTitle("UpdateConfluenceAfterReleaseMojo");
            page.setSpaceKey("sandbox");
        }
        page.setContent(content);
        server.storePage(page);
    }


    private void assertChangelogPage(String release, String releaseTag) throws Exception {
        Page frameworkNewVersionPage = getPage(releaseTag);
        assertEquals(changelogFrameworkPageId, frameworkNewVersionPage.getParentPageId());
        String expectedframeworkNewVersionPage = "h1. Version\n"
                                                 + "\n"
                                                 + "La version est " + release + "\n"
                                                 + "\n"
                                                 + "h1. Bugs connus\n"
                                                 + "{excerpt}{excerpt} n/a\n"
                                                 + "\\\\\n"
                                                 + "h1. Changelog\n"
                                                 + "{composition-setup}\n"
                                                 + "{deck:id=hotTopic}\n"
                                                 + "{card:label=Hot topics}\n"
                                                 + "{blog-posts:50|match-labels=all|labels=hot-topics , "
                                                 + releaseTag
                                                 + "|content=titles}\n"
                                                 + "{card}\n"
                                                 + "{deck}\n"
                                                 + "{div:style=margin-top:10px}\n"
                                                 + "{deck:id=unicId}\n"
                                                 + "{card:label=Modifications}\n"
                                                 + "{blog-posts:50|labels=" + releaseTag + "}\n"
                                                 + "{card}\n"
                                                 + "{card:label=Librairies}\n"
                                                 + "{related-labels:labels=" + releaseTag + "}\n"
                                                 + "{card}\n"
                                                 + "{deck}";
        assertEquals(expectedframeworkNewVersionPage, frameworkNewVersionPage.getContent());
    }


    private String homePageContent(String lastRelease,
                                   String lastReleaseChangelogPage,
                                   String nextRelease,
                                   String nextReleaseTag) {
        return "h4.\n"
               + "{section}\n"
               + "{column:width=50%}\n"
               + "h2. Menu\n"
               + "* [Page du Coordinateur]\n"
               + "** [Librairies Internes]\n"
               + "** [Infrastructure Build]\n"
               + "** [Utilitaires Internes]\n"
               + "* [Changelog framework]\n"
               + "* [Subversion]\n"
               + "* [Incubator]\n"
               + "* [WikiConfluence]\n"
               + "{column}\n"
               + "{column}\n"
               + "h2. Question de la semaine...\n"
               + "{vote:Faut-il mettre en place un forum?}\n"
               + "Evidemment\n"
               + "Pourquoi pas\n"
               + "{vote}\n"
               + "{column}\n"
               + "{section}\n"
               + "h2. Framework\n"
               + "Derniere version stable : " + lastRelease
               + " ([Changelog|" + lastReleaseChangelogPage + "])\n"
               + "h2. Changements en cours sur la version " + nextRelease
               + "\n"
               + "{composition-setup}\n"
               + "{div:style=margin-top:10px}\n"
               + "{div}\n"
               + "{deck:id=unicId}\n"
               + "{card:label=Changelog}\n"
               + "{blog-posts:50|labels=" + nextReleaseTag + "}\n"
               + "{card}\n"
               + "{card:label=Snapshot}\n"
               + "{contentbylabel:snapshot,release-candidate|maxResults=40}\n"
               + "{card}\n"
               + "{deck}";
    }


    private void createFrameworkChangelogHomePage() throws Exception {
        Page page;
        try {
            page = server.getPage("sandbox", "Changelog framework");
        }
        catch (ConfluenceException e) {
            page = new Page();
            page.setTitle("Changelog framework");
            page.setSpaceKey("sandbox");
        }
        page.setContent("Liste des versions :\n{children:excerpt=true|sort=creation|reverse=true} ");
        server.storePage(page);
        changelogFrameworkPageId = page.getId();
    }


    private Page getPage(String pageTitle) throws Exception {
        try {
            Page page;
            page = server.getPage("sandbox", pageTitle);
            return page;
        }
        catch (ConfluenceException e) {
            fail("la page " + pageTitle + " n'existe pas");
        }
        return null;
    }


    private void deleteChangelogPage(String pageTitle) throws Exception {
        try {
            Page page;
            page = server.getPage("sandbox", pageTitle);
            server.removePage(page);
        }
        catch (ConfluenceException e) {
            ;
        }
    }


    private String getFrameworkHomePage() throws Exception {
        Page page;
        page = server.getPage("sandbox", "UpdateConfluenceAfterReleaseMojo");
        return page.getContent();
    }


    protected Mojo lookupMojo(String goal) throws Exception {
        Mojo mojo = super.lookupMojo(goal);
        getProject().setRemoteArtifactRepositories(VersionUtilTest.defaultCodjoRepository());
        return mojo;
    }


    protected void setUp() throws Exception {
        super.setUp();
        server = new ConfluenceServer(
              new ConfluenceSession("http://wd-confluence/confluence", "user_dev", "user_dev"));
        server.login();
        deleteChangelogPage("framework-1-19");
        createFrameworkChangelogHomePage();
        createCoordinatorNeededPage();
    }


    protected void tearDown() throws Exception {
        deleteChangelogPage("framework-1-19");
        super.tearDown();
        server.logout();
    }
}
