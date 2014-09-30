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
public class UpdateConfluenceBeforeReleaseMojoTest extends AgfMojoTestCase {

    public void disabled_test_execute() throws Exception {
        setupEnvironment("/mojos/updateConfluence/pom-before-default.xml");

        Mojo mojo = lookupMojo("update-confluence-before-release");

        PomUtils.mockLastPomVersion("1.19");
        mockChangeLogInConfluence();
        mojo.execute();

        String expected = "h4.\n"
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
                          + "{warning:title=Attention}Stabilisation en cours\n"
                          + "{warning}\n"
                          + "Derniere version stable : 1.18 ([Changelog|framework-1-18])\n"
                          + "h2. Changements en cours sur la version 1.19\n"
                          + "{composition-setup}\n"
                          + "{div:style=margin-top:10px}\n"
                          + "{div}\n"
                          + "{deck:id=unicId}\n"
                          + "{card:label=Changelog}\n"
                          + "{blog-posts:50|labels=framework-1-19}\n"
                          + "{card}\n"
                          + "{card:label=Snapshot}\n"
                          + "{contentbylabel:snapshot,release-candidate|maxResults=40}\n"
                          + "{card}\n"
                          + "{deck}";
        assertEquals(expected, getChangeLogInConfluence());
    }


    public void test_empty() throws Exception {
    }


    private void mockChangeLogInConfluence() throws Exception {
        ConfluenceServer server = new ConfluenceServer(
              new ConfluenceSession("http://wd-confluence/confluence", "user_dev", "user_dev"));
        server.login();
        try {
            Page page;
            try {
                page = server.getPage("sandbox", "UpdateConfluenceBeforeReleaseMojo");
            }
            catch (ConfluenceException e) {
                page = new Page();
                page.setTitle("UpdateConfluenceBeforeReleaseMojo");
                page.setSpaceKey("sandbox");
            }
            page.setContent("h4.\n"
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
                            + "Derniere version stable : 1.18 ([Changelog|framework-1-18])\n"
                            + "h2. Changements en cours sur la version 1.19\n"
                            + "{composition-setup}\n"
                            + "{div:style=margin-top:10px}\n"
                            + "{div}\n"
                            + "{deck:id=unicId}\n"
                            + "{card:label=Changelog}\n"
                            + "{blog-posts:50|labels=framework-1-19}\n"
                            + "{card}\n"
                            + "{card:label=Snapshot}\n"
                            + "{contentbylabel:snapshot,release-candidate|maxResults=40}\n"
                            + "{card}\n"
                            + "{deck}"
            );
            server.storePage(page);
        }
        finally {
            server.logout();
        }
    }


    private String getChangeLogInConfluence() throws Exception {
        ConfluenceServer server = new ConfluenceServer(
              new ConfluenceSession("http://wd-confluence/confluence", "user_dev", "user_dev"));
        server.login();
        try {
            Page page;
            page = server.getPage("sandbox", "UpdateConfluenceBeforeReleaseMojo");
            return page.getContent();
        }
        finally {
            server.logout();
        }
    }
}
