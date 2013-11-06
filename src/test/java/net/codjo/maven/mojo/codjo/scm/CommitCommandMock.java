package net.codjo.maven.mojo.codjo.scm;
import net.codjo.test.common.LogString;
/**
 *
 */
public class CommitCommandMock extends CommitCommand {
    private final LogString log;


    public CommitCommandMock(LogString logString) {
        this.log = logString;
    }


    public void execute(CommitConfig config) {
        log.call("commit.execute", config.getScmFileSet());
    }
}
