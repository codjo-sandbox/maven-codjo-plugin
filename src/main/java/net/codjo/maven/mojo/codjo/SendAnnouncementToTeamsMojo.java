package net.codjo.maven.mojo.codjo;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import net.codjo.confluence.ConfluenceException;
import net.codjo.confluence.ConfluenceServer;
import net.codjo.confluence.ConfluenceSession;
import net.codjo.confluence.Page;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.settings.Settings;
import org.codehaus.plexus.components.interactivity.InputHandler;
/**
 * @goal send-announcement-to-teams
 * @aggregator
 */
public class SendAnnouncementToTeamsMojo extends ConfluenceMojo {
    /**
     * @parameter expression="${confluenceSpaceKey}" default-value="swdev"
     * @noinspection UnusedDeclaration
     */
    private String confluenceSpaceKey;
    /**
     * @parameter expression="${confluencePage}" default-value="Destinataire equipe java"
     * @noinspection UnusedDeclaration
     */
    private String confluencePage;
    /**
     * @parameter expression="${smtpServer}" default-value="smtpecon"
     * @required
     * @noinspection UnusedDeclaration
     */
    private String smtpServer;
    /**
     * @parameter expression="${smtpPort}" default-value="25"
     * @required
     * @noinspection UnusedDeclaration
     */
    private String smtpPort;
    /**
     * @parameter expression="${mailDomain}" default-value="@allianz.fr"
     * @required
     * @noinspection UnusedDeclaration
     */
    private String mailDomain = "@allianz.fr";
    /**
     * Maven's default input handler
     *
     * @component
     * @required
     * @readonly
     * @noinspection UnusedDeclaration
     */
    private InputHandler inputHandler;
    /**
     * @parameter expression="${settings}"
     * @required
     * @readonly
     */
    protected Settings settings;
    /**
     * @parameter expression="${isStarting}" default-value="false"
     * @required
     * @noinspection UnusedDeclaration
     */
    private boolean isStarting;

    public static final boolean START = true;


    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            String from = System.getProperty("user.name");
            Set to = extractUserListFromConfluence();
            String subject = "[Plateforme Java]";
            String mailBody;

            if (!isStarting) {
                String frameworkVersion = findLastFrameworkVersion();
                subject += " Nouvelle version du framework - " + frameworkVersion;
                mailBody = createNewReleaseAvailableMailBody(frameworkVersion);
            }
            else {
                subject += " Démarrage d'une stabilisation";
                mailBody = createNewReleaseStartMailBody();
            }

            getLog().info("----------------------------------------------------------------------");
            getLog().info("");
            getLog().info("   From    : " + from);
            getLog().info("   To      : " + to);
            getLog().info("   Subject : " + subject);
            getLog().info("   Body    : \n"
                          + mailBody.replaceAll("(<br>|</ul>|<li>)", "\n").replaceAll("<ul>", ""));

            String response = "yes";
            if (settings.isInteractiveMode()) {
                getLog().info("Voulez-vous envoyer le mail ? (y/n)");
                response = inputHandler.readLine();
            }
            else {
                getLog().info("");
                getLog().info("- Envoi du mail");
            }

            if ("y".equals(response.trim().toLowerCase()) || "yes".equals(response.trim().toLowerCase())) {
                sendMail(from, to, subject, mailBody);
            }
        }
        catch (Exception error) {
            getLog().error(error.getLocalizedMessage(), error);
            throw new MojoExecutionException("Impossible d'envoyer le mail aux equipes", error);
        }
    }


    private String createNewReleaseStartMailBody() {
        return "Bonjour,<br>"
               + "<br>"
               + "Une stabilisation est en cours.<br>"
               + "Merci de ne pas ouvrir de chantier avant de recevoir le mail de fin de stabilisation.<br>"
               + "Cordialement,<br>"
               + System.getProperty("user.name");
    }


    private String createNewReleaseAvailableMailBody(String frameworkVersion) {
        String version = removeFixVersion(frameworkVersion).replaceAll("\\.", "-");
        return "Bonjour,<br>"
               + "<br>"
               + "Une nouvelle version du framework est disponible :<br>"
               + "<br>"
               + "<ul><li>Version " + frameworkVersion
               + " (<a href='http://wp-confluence/confluence/display/framework/framework-"
               + version + "'>changelog</a>)</ul>"
               + "Cordialement,<br>"
               + System.getProperty("user.name");
    }


    private Set extractUserListFromConfluence() throws ConfluenceException {
        ConfluenceServer server = new ConfluenceServer(
              new ConfluenceSession(confluenceUrl, confluenceUser, confluencePassword));
        server.login();
        try {
            Page page = server.getPage(confluenceSpaceKey, confluencePage);
            return extractRecipientList(page.getContent(), isStarting);
        }
        finally {
            server.logout();
        }
    }


    private void sendMail(String from, Set toSet, String subject, String body) throws Exception {
        Properties props = new Properties();
        props.setProperty("mail.smtp.host", smtpServer);
        props.setProperty("mail.smtp.port", smtpPort);

        Session session = Session.getInstance(props);
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(from + mailDomain));

        for (Iterator iter = toSet.iterator(); iter.hasNext(); ) {
            String to = iter.next() + mailDomain;
            msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
        }

        msg.setSubject(subject);
        msg.setContent(body, "text/html; charset=ISO-8859-1");

        msg.setHeader("X-Mailer", "maven");
        msg.setSentDate(new Date());

        Transport.send(msg);
    }


    public static Set extractRecipientList(String content, boolean subscriberOnly) {
        Set userList = new TreeSet();
        for (StringTokenizer tokenizer = new StringTokenizer(content, "\n");
             tokenizer.hasMoreTokens(); ) {
            String row = tokenizer.nextToken();
            if (row.startsWith("* ")) {
                if (!subscriberOnly || row.contains("(*g)")) {
                    userList.add(extractUser(row));
                }
            }
        }

        return userList;
    }


    private static String extractUser(String row) {

        String tmp = row.substring(2).trim();
        int pos = tmp.indexOf(" ");
        if (pos != -1) {
            tmp = tmp.substring(0, pos);
        }
        if (tmp.startsWith("*")) {
            tmp = tmp.substring(1);
        }
        if (tmp.endsWith("*")) {
            tmp = tmp.substring(0, tmp.length() - 1);
        }
        return tmp;
    }


    public void setStarting(boolean starting) {
        this.isStarting = starting;
    }


    public interface UserListBuilder {

        Set buildUserList(boolean subscriberOnly) throws Exception;
    }

    public class ConfluenceUserListExtractor implements UserListBuilder {

        public Set buildUserList(boolean subscriberOnly)
              throws Exception {
            ConfluenceServer server = new ConfluenceServer(
                  new ConfluenceSession(confluenceUrl, confluenceUser, confluencePassword));
            server.login();
            try {
                Page page = server.getPage(confluenceSpaceKey, confluencePage);
                return extractRecipientList(page.getContent(), isStarting);
            }
            finally {
                server.logout();
            }
        }
    }
}
