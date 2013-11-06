package net.codjo.maven.mojo.codjo.scm;
import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.manager.ScmManager;
import org.apache.maven.scm.repository.ScmRepository;
/**
 *
 */
public interface CommitConfig {

    ScmFileSet getScmFileSet();


    String getLogMessage();


    ScmRepository getScmRepository() throws ScmException;


    ScmManager getManager();
}
