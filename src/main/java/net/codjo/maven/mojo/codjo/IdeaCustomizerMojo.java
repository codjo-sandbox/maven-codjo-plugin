package net.codjo.maven.mojo.codjo;
import net.codjo.maven.mojo.codjo.model.Feature;
import net.codjo.maven.mojo.codjo.model.IdeaConfiguration;
import net.codjo.maven.mojo.codjo.model.ModelManager;
import com.oopsconsultancy.xmltask.ant.XmlTask;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
/**
 * Cible permettant de customiser les fichiers IDEA.
 *
 * @goal idea-customizer
 * @aggregator
 */
public class IdeaCustomizerMojo extends AbstractMojo {
    public static final String IDEA_FILE_MISSING_MESSAGE = "Aucun fichier IDEA present.";

    private static final String[] FEATURE_TEMPLATES = new String[]{
          "templates/junitDefault.xml",
          "templates/execConfig.xml",
          "templates/guiToolbar.xml",
          "templates/svnConfiguration.xml",
          "templates/gitConfiguration.xml",
          "templates/projectView.xml"
    };

    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     * @noinspection UNUSED_SYMBOL
     */
    private MavenProject project;

    /**
     * @parameter expression="${ideaConfigFile}" default-value="${project.basedir}/src/idea/idea.xml"
     * @noinspection UNUSED_SYMBOL
     */
    private File ideaConfigFile;


    public void execute() throws MojoExecutionException {
        if (!getIprFile().exists() || !getIwsFile().exists()) {
            getLog().info("");
            getLog().info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            getLog().info("");
            getLog().info(IDEA_FILE_MISSING_MESSAGE);
            getLog().info("Veuillez utiliser la commande suivante :");
            getLog().info("");
            getLog().info("\tmvn idea:clean idea:idea codjo:idea-customizer");
            getLog().info("");
            getLog().info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            getLog().info("");
            throw new MojoExecutionException(IDEA_FILE_MISSING_MESSAGE);
        }
        try {
            executeImpl();
        }
        catch (Exception e) {
            throw new MojoExecutionException("Generation en erreur : " + e.getLocalizedMessage(), e);
        }
    }


    private void executeImpl() throws Exception {
        ModelManager manager = new ModelManager();
        IdeaConfiguration ideaConfiguration = manager.loadConfiguration(ideaConfigFile);

        List iwsCommands = new ArrayList();
        List iprCommands = new ArrayList();
        for (int i = 0; i < FEATURE_TEMPLATES.length; i++) {
            Feature feature = loadFeature(manager, ideaConfiguration, FEATURE_TEMPLATES[i]);
            getLog().info("Customizer > '" + feature.getName() + "'");
            if (feature.getIws() != null) {
                iwsCommands.addAll(feature.getIws());
            }
            if (feature.getIpr() != null) {
                iprCommands.addAll(feature.getIpr());
            }
        }

        getLog().info("Customize IWS file...");
        applyFeature(iwsCommands, getIwsFile());

        getLog().info("Customize IPR file...");
        applyFeature(iprCommands, getIprFile());
    }


    private File getIwsFile() {
        return new File(project.getBasedir() + "/" + project.getArtifactId() + ".iws");
    }


    private File getIprFile() {
        return new File(project.getBasedir() + "/" + project.getArtifactId() + ".ipr");
    }


    private void applyFeature(List commands, File inputFile)
          throws Exception {
        if (commands.size() == 0) {
            return;
        }
        File customizedFile = new File(inputFile.getPath() + ".tmp");

        XmlTask task = new XmlTask();
        initAntStuff(task);

        task.setSource(inputFile.getPath());
        task.setDest(customizedFile.getPath());
        task.setEncoding("ISO-8859-1");

        for (int i = 0; i < commands.size(); i++) {
            Feature.Command command = (Feature.Command)commands.get(i);
            command.applyCommand(task);
        }

        task.execute();

        inputFile.delete();
        customizedFile.renameTo(inputFile);
    }


    private Feature loadFeature(ModelManager manager, IdeaConfiguration ideaConfiguration, String featureName)
          throws Exception {
        Generator generator = new Generator(featureName);
        String featuresString = generator.generate(ideaConfiguration, project.getProperties());
        return manager.readFeature(featuresString);
    }


    private void initAntStuff(Task task) {
        Project ant = new Project();
        setProjectProperties(ant);
        ant.setProperty("project.version", project.getVersion());
        ant.setProperty("project.name", project.getName());
        ant.setProperty("project.groupId", project.getGroupId());
        ant.setProperty("project.artifactId", project.getArtifactId());
        ant.setProperty("project.url", project.getUrl());

        ant.setBaseDir(project.getBasedir());
        task.setTaskName("xmltask");
        task.setTaskType("xmltask");
        task.setProject(ant);
        ant.init();
    }


    private void setProjectProperties(Project ant) {
        for (Iterator iter = project.getProperties().entrySet().iterator(); iter.hasNext();) {
            Map.Entry entry = (Map.Entry)iter.next();
            ant.setProperty((String)entry.getKey(), (String)entry.getValue());
        }
    }
}
