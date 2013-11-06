package net.codjo.maven.mojo.codjo;
import java.util.Iterator;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;
import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.manager.ScmManager;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.provider.ScmProviderRepositoryWithHost;
import org.apache.maven.scm.provider.svn.repository.SvnScmProviderRepository;
import org.apache.maven.scm.repository.ScmRepository;
import org.apache.maven.scm.repository.ScmRepositoryException;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.codehaus.plexus.components.interactivity.InputHandler;
import org.codehaus.plexus.util.StringUtils;
/**
 *
 */
public abstract class ScmMojo extends AbstractMojo {
    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     * @noinspection UnusedDeclaration
     */
    protected MavenProject project;
    /**
     * Maven's default input handler
     *
     * @component
     * @required
     * @readonly
     * @noinspection UnusedDeclaration
     */
    protected InputHandler inputHandler;
    /**
     * The SCM connection URL.
     *
     * @parameter expression="${connectionUrl}" default-value="${project.scm.connection}"
     * @noinspection UnusedDeclaration
     */
    private String connectionUrl;
    /**
     * @parameter expression="${connectionUrl}" default-value="${project.scm.developerConnection}"
     * @noinspection UnusedDeclaration
     */
    private String developerConnectionUrl;
    /**
     * The type of connection to use (connection or developerConnection).
     *
     * @parameter expression="${connectionType}" default-value="connection"
     * @noinspection UnusedDeclaration
     */
    private String connectionType;
    /**
     * The user name (used by svn and starteam protocol).
     *
     * @parameter expression="${username}"
     */
    private String username;
    /**
     * The user password (used by svn and starteam protocol).
     *
     * @parameter expression="${password}"
     */
    private String password;
    /**
     * The private key (used by java svn).
     *
     * @parameter expression="${privateKey}"
     */
    private String privateKey;
    /**
     * The passphrase (used by java svn).
     *
     * @parameter expression="${passphrase}"
     */
    private String passphrase;
    /**
     * The url of tags base directory (used by svn protocol). Not necessary to set it if you use standard svn
     * layout (branches/tags/trunk).
     *
     * @parameter expression="${tagBase}"
     * @noinspection UnusedDeclaration
     */
    private String tagBase;
    /**
     * @parameter expression="${component.org.apache.maven.scm.manager.ScmManager}"
     * @required
     * @readonly
     * @noinspection UnusedDeclaration
     */
    private ScmManager manager;
    /**
     * @parameter expression="${settings}"
     * @required
     * @readonly
     */
    protected Settings settings;


    public ScmManager getManager() {
        return manager;
    }


    public String getConnectionUrl() {
        return connectionUrl;
    }


    public ScmRepository getScmRepository() throws ScmException {
        ScmRepository repository;

        try {
            repository = getManager().makeScmRepository(getConnectionUrl());

            ScmProviderRepository providerRepo = repository.getProviderRepository();

            if (!StringUtils.isEmpty(username)) {
                providerRepo.setUser(username);
            }

            if (!StringUtils.isEmpty(password)) {
                providerRepo.setPassword(password);
            }

            if (repository.getProviderRepository() instanceof ScmProviderRepositoryWithHost) {
                initRepositoryHost(repository);
            }

            if (!StringUtils.isEmpty(tagBase) && "svn".equals(repository.getProvider())) {
                SvnScmProviderRepository svnRepo =
                      (SvnScmProviderRepository)repository.getProviderRepository();

                svnRepo.setTagBase(tagBase);
            }
        }
        catch (ScmRepositoryException e) {
            if (!e.getValidationMessages().isEmpty()) {
                for (Iterator i = e.getValidationMessages().iterator(); i.hasNext();) {
                    String message = (String)i.next();
                    getLog().error(message);
                }
            }

            throw new ScmException("Can't load the scm provider.", e);
        }
        catch (Exception e) {
            throw new ScmException("Can't load the scm provider.", e);
        }

        return repository;
    }


    private void initRepositoryHost(ScmRepository repository) {
        ScmProviderRepositoryWithHost repo =
              (ScmProviderRepositoryWithHost)repository.getProviderRepository();

        loadInfosFromSettings(repo);

        if (!StringUtils.isEmpty(username)) {
            repo.setUser(username);
        }

        if (!StringUtils.isEmpty(password)) {
            repo.setPassword(password);
        }

        if (!StringUtils.isEmpty(privateKey)) {
            repo.setPrivateKey(privateKey);
        }

        if (!StringUtils.isEmpty(passphrase)) {
            repo.setPassphrase(passphrase);
        }
    }


    /**
     * Load username password from settings if user has not set them in JVM properties
     *
     * @param repo
     */
    private void loadInfosFromSettings(ScmProviderRepositoryWithHost repo) {
        if (username == null || password == null) {
            String host = repo.getHost();

            int port = repo.getPort();

            if (port > 0) {
                host += ":" + port;
            }

            Server server = settings.getServer(host);

            if (server != null) {
                if (username == null) {
                    username = settings.getServer(host).getUsername();
                }

                if (password == null) {
                    password = settings.getServer(host).getPassword();
                }

                if (privateKey == null) {
                    privateKey = settings.getServer(host).getPrivateKey();
                }

                if (passphrase == null) {
                    passphrase = settings.getServer(host).getPassphrase();
                }
            }
        }
    }
}
