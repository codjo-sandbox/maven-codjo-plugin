package net.codjo.maven.mojo.codjo;
import java.io.IOException;
import java.util.Iterator;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.filter.Filter;
/**
 * Bascule une lib ou un plugin donn� en param�tre en SNAPSHOT dans le pom parent.
 *
 * @goal switch-to-snapshot
 * @aggregator
 */
public class SwitchToSnapshotMojo extends SwitchAbstractMojo {
    /**
     * @parameter expression="${lib}"
     * @noinspection UnusedDeclaration
     */
    private String lib;

    /**
     * @parameter expression="${plugin}"
     * @noinspection UnusedDeclaration
     */
    private String plugin;


    public SwitchToSnapshotMojo() {
        this.myCustomizer = new MyCustomizer();
    }


    public void setLib(String lib) {
        this.lib = lib;
    }


    public void setPlugin(String plugin) {
        this.plugin = plugin;
    }


    public void execute() throws MojoExecutionException, MojoFailureException {
        if (lib == null && plugin == null) {
            printUsage();
        }
        else {
            super.execute();
        }
    }


    protected boolean proposeCommit() {
        return false;
    }


    public String getLogMessage() {
        return null;
    }


    void doEndExecute() throws IOException, MojoExecutionException {
    }


    public String computeNewVersion(String globalVersion) throws MojoFailureException {
        if (globalVersion.contains("-SNAPSHOT")) {
            throw new MojoFailureException("Deja en SNAPSHOT");
        }

        VersionInfo versionInfo = new VersionInfo(globalVersion);
        int separatorIndex = versionInfo.getVersion().lastIndexOf('.') + 1;
        String majorVersion = versionInfo.getVersion().substring(0, separatorIndex);
        int minorVersion = Integer.valueOf(versionInfo.getVersion().substring(separatorIndex)).intValue();
        return majorVersion + (minorVersion + 1) + VersionInfo.AGI_SUFFIX + "-SNAPSHOT";
    }


    private void printUsage() {
        getLog().info("");
        getLog().info("Usage : ");
        getLog().info("");
        getLog()
              .info("    mvn codjo:switch-to-snapshot [-Dlib=nom de la librairie] [-Dplugin=nom du plugin]");
        getLog().info("      e.g. mvn codjo:switch-to-snapshot -Dlib=mad");
        getLog().info("           mvn codjo:switch-to-snapshot -Dplugin=datagen");
        getLog().info("Ce goal doit etre appele avec au moins un parametre.");
        getLog().info("");
    }


    private class MyCustomizer implements SwitchPomCustomizer {
        private Namespace namespace;
        private boolean dependencyfound = false;


        public void transform(MavenProject project, Element rootElement)
              throws MojoExecutionException, MojoFailureException {
            namespace = rootElement.getNamespace();

            if (lib != null && !"".equals(lib.trim())) {
                replaceAllVersions(getDependencyIterator(rootElement,
                                                         libTagsHierarchy,
                                                         new MyFilterLib(lib, namespace)));
                replaceAllVersions(getDependencyIterator(rootElement,
                                                         pluginTagsHierarchy,
                                                         new MyFilterLib(lib, namespace)));
            }
            if (plugin != null && !"".equals(plugin.trim())) {
                replaceAllVersions(getDependencyIterator(rootElement,
                                                         pluginTagsHierarchy,
                                                         new MyFilterPlugin(plugin, namespace)));
            }
        }


        private Iterator getDependencyIterator(Element rootElement, String[] tagsHierarchy, Filter filter) {
            Element element = rootElement;
            for (int i = 0; i < tagsHierarchy.length - 1; i++) {
                element = element.getChild(tagsHierarchy[i], namespace);
                if (element == null) {
                    return null;
                }
            }
            return element.getDescendants(filter);
        }


        public void printLog() throws MojoExecutionException {
            if (!dependencyfound) {
                throw new MojoExecutionException("pas de libraire ni de plugin correspondant.");
            }
            else {
                getLog().info("");
                getLog().info("Les fichiers du super-pom ont ete correctement mis a jour.");
                getLog().info("Etapes suivantes :");
                getLog().info("");
                getLog().info("1 - Installation locale du super-pom :");
                getLog().info("");
                getLog().info("\t\tmvn clean install");
                getLog().info("");
                getLog().info("2 - Depoiement de la version SNAPSHOT de la librairie :");
                getLog().info("");
                if (lib != null && !"".equals(lib.trim())) {
                    getLog().info("\t\tlib " + lib.trim());
                }
                else if (plugin != null && !"".equals(plugin.trim())) {
                    getLog().info("\t\tplugin " + plugin.trim());
                }
                getLog().info("\t\tmvn clean deploy");
                getLog().info("");
                getLog().info("3 - Deploiement du super-pom :");
                getLog().info("");
                getLog().info("\t\tsuper-pom");
                getLog().info("\t\tmvn clean deploy");
                getLog().info("");
                getLog().info("4 - Commit des modifications :");
                getLog().info("");
                if (lib != null && !"".equals(lib.trim())) {
                    logCommitInfo("library", lib.trim());
                }
                else if (plugin != null && !"".equals(plugin.trim())) {
                    logCommitInfo("plugin", plugin.trim());
                }
                getLog().info("");
                getLog().info("");
            }
        }


        private void logCommitInfo(String prefix, String artifact) {
            getLog().info("\t\tgit add .");
            getLog().info("\t\tgit commit -m \"Switch " + prefix + " " + artifact
                          + " to snapshot version\"");
            getLog().info("\t\tpush");
        }


        private void replaceAllVersions(Iterator it) throws MojoExecutionException, MojoFailureException {
            while (it != null && it.hasNext()) {
                Element dependency = (Element)(it.next());
                Element versionElement = dependency.getChild("version", namespace);

                String globalVersion = versionElement.getText();
                versionElement.setText(computeNewVersion(globalVersion));
                dependencyfound = true;
            }
        }
    }

    private class MyFilterLib implements Filter {
        private String lib;
        private Namespace namespace;


        private MyFilterLib(String lib, Namespace namespace) {
            this.lib = lib;
            this.namespace = namespace;
        }


        public boolean matches(Object obj) {
            if (obj instanceof Element) {
                Element el = (Element)obj;
                Element groupId = el.getChild("groupId", namespace);
                return groupId != null && groupId.getText().equals("net.codjo." + lib);
            }
            return false;
        }
    }

    private class MyFilterPlugin implements Filter {
        private String plugin;
        private Namespace namespace;


        private MyFilterPlugin(String plugin, Namespace namespace) {
            this.plugin = plugin;
            this.namespace = namespace;
        }


        public boolean matches(Object obj) {
            if (obj instanceof Element) {
                Element el = (Element)obj;
                Element artifactId = el.getChild("artifactId", namespace);
                return artifactId != null && artifactId.getText().equals("maven-" + plugin + "-plugin");
            }
            return false;
        }
    }
}