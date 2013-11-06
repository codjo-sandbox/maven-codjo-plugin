package net.codjo.maven.mojo.codjo;
import net.codjo.maven.common.mock.AgfMojoTestCase;
import net.codjo.util.file.FileUtil;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.maven.project.MavenProject;
/**
 *
 */
public abstract class SwitchLibTestCase extends AgfMojoTestCase {
    protected static final String PATH = "./target/test-classes/mojos/";
    protected MavenProject project;
    protected File pomFile;
    protected File childPomFile;
    private File copyPomFile = new File(PATH + "copyPom.xml");
    private File copyChildPomFile = new File(PATH + "copyChildPom.xml");


    private void initializeModules() {
        List modules = new ArrayList();
        modules.add("pom-module");
        modules.add("pom-module-nodependencies");
        project.getModel().setModules(modules);
    }


    protected String loadContent(File file) throws IOException {
        return FileUtil.loadContent(file);
    }


    protected void setUp() throws Exception {
        super.setUp();
        copiePomFile(pomFile, copyPomFile);
        copiePomFile(childPomFile, copyChildPomFile);
        project = new MavenProject();
        project.setFile(pomFile);
        initializeModules();
    }


    protected void tearDown() throws Exception {
        super.tearDown();
        copiePomFile(copyPomFile, pomFile);
        copiePomFile(copyChildPomFile, childPomFile);
    }


    private void copiePomFile(File fileReader, File fileWriter) throws IOException {

        FileReader fread = new FileReader(fileReader);
        FileWriter fwrite = new FileWriter(fileWriter);
        int ss = fread.read();
        while (ss != -1) {
            fwrite.write(ss);
            ss = fread.read();
        }
        fread.close();
        fwrite.close();
    }
}
