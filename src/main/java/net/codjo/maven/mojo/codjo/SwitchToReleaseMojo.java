package net.codjo.maven.mojo.codjo;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.jdom.Element;
import org.jdom.Namespace;
/**
 * Bascule les lib dans le pom parent sur leur futur version stable.
 *
 * @goal switch-to-release
 * @aggregator
 */
public class SwitchToReleaseMojo extends SwitchAbstractMojo {
    /**
     * @parameter expression="${stabilisationFileName}"
     * @noinspection UnusedDeclaration
     */
    private String stabilisationFileName;


    public SwitchToReleaseMojo() {
        this.myCustomizer = new MyCustomizer();
    }


    public void setStabilisationFileName(String stabilisationFileName) {
        this.stabilisationFileName = stabilisationFileName;
    }


    protected boolean proposeCommit() {
        return true;
    }


    public String getLogMessage() {
        return "Switch libraries from snapshot to their future stabilized version";
    }


    private class MyCustomizer implements SwitchPomCustomizer {
        private Namespace namespace;
        private Set artifactSet = new TreeSet();


        public void transform(MavenProject project, Element rootElement) {
            namespace = rootElement.getNamespace();

            removeSnapshotAndLog(searchElementsWithVersionTag(rootElement, libTagsHierarchy, 0));
            removeSnapshotAndLog(searchElementsWithVersionTag(rootElement, libInPluginTagsHierarchy, 0));
            removeSnapshotAndLog(searchElementsWithVersionTag(rootElement, pluginTagsHierarchy, 0));
            removeSnapshotAndLog(searchElementsWithVersionTag(rootElement, libTagsHierarchy2, 0));
        }


        private List searchElementsWithVersionTag(Element rootElement, String[] tagsHierarchy, int level) {
            List arrayList = new ArrayList();
            if (level < tagsHierarchy.length - 1) {
                List childrens = rootElement.getChildren(tagsHierarchy[level], namespace);

                for (int i = 0; i < childrens.size(); i++) {
                    List elementsWithVersionTag = searchElementsWithVersionTag((Element)childrens.get(i),
                                                                               tagsHierarchy,
                                                                               level + 1);

                    arrayList.addAll(elementsWithVersionTag);
                }
            }
            else {
                List childrens = rootElement.getChildren(tagsHierarchy[level], namespace);
                arrayList.addAll(childrens);
            }

            return arrayList;
        }


        public void printLog() throws MojoExecutionException, IOException {
            BufferedWriter bufferedWriter = createFileBuffer();

            getLog().info("");
            getLog().info("Les dependences suivantes devront etre stabilisees :");
            getLog().info("");
            for (Iterator iterator = artifactSet.iterator(); iterator.hasNext(); ) {
                String artifact = (String)iterator.next();
                getLog().info("\t\t" + artifact);

                bufferedWriter.append(artifact);
                bufferedWriter.newLine();
            }
            getLog().info("");

            bufferedWriter.close();
        }


        private BufferedWriter createFileBuffer() throws IOException {
            File stabilisationFile;
            if (stabilisationFileName == null) {
                stabilisationFile = new File(System.getProperty("java.io.tmpdir"),
                                             "stabilisationBuildList.txt");
            }
            else {
                stabilisationFile = new File(stabilisationFileName);
            }

            if (stabilisationFile.exists()) {
                stabilisationFile.delete();
            }
            stabilisationFile.createNewFile();
            getLog().info("Fichier contenant la liste ci-dessous : ");
            getLog().info(stabilisationFile.getAbsolutePath());
            return new BufferedWriter(new FileWriter(stabilisationFile));
        }


        private void removeSnapshotAndLog(List dependencies) {
            for (int i = 0; i < dependencies.size(); i++) {
                Element element = (Element)dependencies.get(i);
                String version = getChildValue(element, "version");
                if (version != null) {
                    int index = version.indexOf("-SNAPSHOT");
                    if (index != -1) {
                        element.getChild("version", namespace).setText(version.substring(0, index));

                        fillArtifactList(element);
                    }
                }
            }
        }


        private String getChildValue(Element element, String name) {
            Element childElement = element.getChild(name, namespace);
            if (childElement == null) {
                return null;
            }
            return childElement.getText().trim();
        }


        private void fillArtifactList(Element element) {
            String artifactId = getChildValue(element, "artifactId");
            String libGroupId = getChildValue(element, "groupId");

            if ("plugin".equals(element.getName())) {
                artifactSet.add("plugin " + determinePluginName(artifactId));
            }
            else if ("net.codjo.maven".equals(libGroupId) && artifactId.startsWith("codjo-maven-")) {
                artifactSet.add("libmaven " + artifactId.replaceAll("codjo-maven-", ""));
            }
            else {
                artifactSet.add("lib " + determineLibName(libGroupId));
            }
        }


        private String determineLibName(String libGroupId) {
            return libGroupId.replaceFirst("net.codjo.", "").replaceAll("\\.", "-");
        }


        private String determinePluginName(String artifactId) {
            return artifactId.substring("maven-".length(), artifactId.lastIndexOf("-plugin"));
        }
    }
}
