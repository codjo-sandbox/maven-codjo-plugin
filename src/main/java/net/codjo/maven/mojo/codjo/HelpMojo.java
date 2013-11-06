package net.codjo.maven.mojo.codjo;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
/**
 * Cible permettant d'afficher une aide contextuelle sur les commandes maven pertinante.
 *
 * @goal help
 * @aggregator
 * @requiresProject false
 */
public class HelpMojo extends AbstractMojo {
    /**
     * @parameter expression="${project}"
     * @readonly
     * @noinspection UNUSED_SYMBOL
     */
    private MavenProject project;


    public void execute() throws MojoExecutionException {
        try {
            executeImpl();
        }
        catch (Exception e) {
            throw new MojoExecutionException("Aide en erreur : " + e.getLocalizedMessage(), e);
        }
    }


    private void executeImpl() throws Exception {
        ProjectType projectType = toProjectType(project);
        getLog().info("      type " + projectType);
        getLog().info(projectType.getHelp());
    }


    protected static ProjectType toProjectType(MavenProject project) {
        for (int i = 0; i < ProjectType.TYPES.length; i++) {
            ProjectType type = ProjectType.TYPES[i];
            if (type.match(project)) {
                return type;
            }
        }
        return ProjectType.UNKNOWN;
    }
}
