package net.codjo.maven.mojo.codjo;
import java.util.Set;
import net.codjo.confluence.ConfluenceException;
import net.codjo.confluence.ConfluenceServer;
import net.codjo.confluence.ConfluenceSession;
import net.codjo.confluence.Page;
import net.codjo.maven.common.mock.AgfMojoTestCase;
import net.codjo.test.common.fixture.MailFixture;
import org.apache.maven.plugin.Mojo;
/**
 *
 */
public class SendAnnouncementToTeamsMojoTest extends AgfMojoTestCase {
    private MailFixture mailFixture = new MailFixture(89);


    public void disabled_test_execute() throws Exception {
        setupEnvironment("/mojos/announcement/pom-default.xml");

        Mojo mojo = lookupMojo("send-announcement-to-teams");

        PomUtils.mockLastPomVersion("1.18");
        mockUserListInConfluence("* USER1\n* USER2");
        mockUserAnswer("yes");

        mojo.execute();

        mailFixture.getReceivedMessage(0).assertThat()
              .from(System.getProperty("user.name") + "@allianz.fr")
              .to("USER1@allianz.fr", "USER2@allianz.fr")
              .subject("[Plateforme Java] Nouvelle version du framework - 1.18")
              .bodyContains("Version 1.18")
              .bodyContains("'http://wp-confluence/confluence/display/framework/framework-1-18'")
              .bodyContains("Cordialement,")
              .bodyContains(System.getProperty("user.name"));

        mailFixture.assertReceivedMessagesCount(1);
    }


    public void disabled_test_execute_begining() throws Exception {
        setupEnvironment("/mojos/announcement/pom-default.xml");

        SendAnnouncementToTeamsMojo mojo = (SendAnnouncementToTeamsMojo)lookupMojo("send-announcement-to-teams");
        mojo.setStarting(SendAnnouncementToTeamsMojo.START);

        PomUtils.mockLastPomVersion("1.18.1");
        mockUserListInConfluence("* USER1 (*g)\n* USER2");
        mockUserAnswer("yes");

        mojo.execute();

        mailFixture.getReceivedMessage(0).assertThat()
              .from(System.getProperty("user.name") + "@allianz.fr")
              .to("USER1@allianz.fr")
              .subject("[Plateforme Java] Démarrage d'une stabilisation")
              .bodyContains("Une stabilisation est en cours.")
              .bodyContains("Merci de ne pas ouvrir de chantier avant de recevoir le mail de fin de stabilisation.")
              .bodyContains("Cordialement,")
              .bodyContains(System.getProperty("user.name"));

        mailFixture.assertReceivedMessagesCount(1);
    }


    public void disabled_test_execute_userRefused() throws Exception {
        setupEnvironment("/mojos/announcement/pom-default.xml");
        Mojo mojo = lookupMojo("send-announcement-to-teams");
        PomUtils.mockLastPomVersion("1.18");
        mockUserListInConfluence("* USER1\n* USER2");

        mockUserAnswer("n");

        mojo.execute();

        mailFixture.assertReceivedMessagesCount(0);
    }


    public void test_extractUserList() throws Exception {
        String confluenceContent = "ignored text\n"
                                   + "* VILLARD   \n"
                                   + "\n"
                                   + "+Equipe 5+\n"
                                   + "\n"
                                   + "* *GALABER* (Responsable de liste)\n"
                                   + "* externe.toto\n"
                                   + "*   CASSAGC";

        Set userList = SendAnnouncementToTeamsMojo.extractRecipientList(confluenceContent, false);

        assertEquals("[CASSAGC, GALABER, VILLARD, externe.toto]", userList.toString());
    }


    public void test_extractDeveloperList() throws Exception {
        String confluenceContent = "ignored text\n"
                                   + "* VILLARD   \n"
                                   + "\n"
                                   + "+Equipe 5+\n"
                                   + "\n"
                                   + "* *GALABER* (Responsable de liste) (*g)\n"
                                   + "* externe.toto\n"
                                   + "*   CASSAGC (*g)";

        Set userList = SendAnnouncementToTeamsMojo.extractRecipientList(confluenceContent, true);

        assertEquals("[CASSAGC, GALABER]", userList.toString());
    }


    public void disabled_test_execute_batchMode() throws Exception {
        setupEnvironment("/mojos/announcement/pom-default.xml");
        SendAnnouncementToTeamsMojo mojo = (SendAnnouncementToTeamsMojo)lookupMojo("send-announcement-to-teams");
        mojo.settings.setInteractiveMode(false);
        mockUserListInConfluence("* USER1\n* USER2");

        mojo.execute();

        mailFixture.getReceivedMessage(0).assertThat()
              .from(System.getProperty("user.name") + "@allianz.fr")
              .subject("[Plateforme Java] Nouvelle version du framework - n/a");

        mailFixture.assertReceivedMessagesCount(1);
    }


    private void mockUserListInConfluence(String userList) throws Exception {
        ConfluenceServer server = new ConfluenceServer(
              new ConfluenceSession("http://wd-confluence/confluence", "user_dev", "user_dev"));
        server.login();
        try {
            Page page;
            try {
                page = server.getPage("sandbox", "SendAnnouncementToTeamsMojo");
                server.removePage(page);
            }
            catch (ConfluenceException e) {
                ; // The page does not exist
            }
            page = new Page();
            page.setTitle("SendAnnouncementToTeamsMojo");
            page.setSpaceKey("sandbox");
            page.setContent("Page utilisé pour des tests\n" + userList);
            server.storePage(page);
        }
        finally {
            server.logout();
        }
    }


    protected Mojo lookupMojo(String goal) throws Exception {
        Mojo mojo = super.lookupMojo(goal);
        getProject().setRemoteArtifactRepositories(VersionUtilTest.defaultCodjoRepository());
        return mojo;
    }


    protected void setUp() throws Exception {
        super.setUp();
        mailFixture.doSetUp();
    }


    protected void tearDown() throws Exception {
        mailFixture.doTearDown();
        super.tearDown();
    }
}
