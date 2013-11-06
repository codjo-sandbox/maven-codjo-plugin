package net.codjo.maven.mojo.codjo.scm;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmResult;
import org.apache.maven.scm.command.checkin.CheckInScmResult;
import org.apache.maven.scm.provider.ScmProvider;
import org.apache.maven.scm.repository.ScmRepository;
import org.codehaus.plexus.util.StringUtils;
/**
 *
 */
public class CommitCommand {
    private Log log = new SystemStreamLog();


    public void setLog(Log log) {
        this.log = log;
    }


    public void execute(CommitConfig config) throws MojoExecutionException {

        try {
            ScmRepository repository = config.getScmRepository();
            ScmProvider provider = config.getManager().getProviderByRepository(repository);

            CheckInScmResult result = provider
                  .checkIn(repository, config.getScmFileSet(), (String)null, config.getLogMessage());

            checkResult(result);
        }
        catch (ScmException e) {
            throw new MojoExecutionException("Cannot run checkin command : ", e);
        }
    }


    private void checkResult(ScmResult result) throws MojoExecutionException {
        if (!result.isSuccess()) {
            log.error("Provider message:");

            log.error(result.getProviderMessage() == null ? "" : result.getProviderMessage());

            log.error("Command output:");

            log.error(result.getCommandOutput() == null ? "" : result.getCommandOutput());

            throw new MojoExecutionException("Command failed."
                                             + StringUtils.defaultString(result.getProviderMessage()));
        }
    }
}
