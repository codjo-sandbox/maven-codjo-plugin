package net.codjo.maven.mojo.codjo;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import net.codjo.util.file.FileUtil;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.jdom.Comment;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.filter.ContentFilter;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
/**
 *
 */
public class PomCustomizer {
    private static final String LS = System.getProperty("line.separator");


    private PomCustomizer() {
    }


    public static void applyCustomizer(Customizer customizer, MavenProject project)
          throws IOException, JDOMException, MojoExecutionException, MojoFailureException {
        applyCustomizer(customizer, project, project.getFile());
    }


    public static void applyCustomizer(Customizer customizer, MavenProject project, File output)
          throws IOException, JDOMException, MojoExecutionException, MojoFailureException {
        Document document;
        String intro = null;
        String outtro = null;
        String content = FileUtil.loadContent(project.getFile());
        // we need to eliminate any extra whitespace inside elements, as JDOM will nuke it
        content = content.replaceAll("<([^!][^>]*?)\\s{2,}([^>]*?)>", "<$1 $2>");
        content = content.replaceAll("(\\s{2,}|[^\\s])/>", "$1 />");

        SAXBuilder builder = new SAXBuilder();
        document = builder.build(new StringReader(content));

        // Normalise line endings. For some reason, JDOM replaces \r\n inside a comment with \n.
        normaliseLineEndings(document);

        // rewrite DOM as a string to find differences, since text outside the root element is not tracked
        StringWriter writer = new StringWriter();
        Format format = Format.getRawFormat();
        format.setLineSeparator(LS);
        XMLOutputter out = new XMLOutputter(format);
        out.output(document.getRootElement(), writer);

        int index = content.indexOf(writer.toString());
        if (index >= 0) {
            intro = content.substring(0, index);
            outtro = content.substring(index + writer.toString().length());
        }

        customizer.transform(project, document.getRootElement());

        writePom(output, document, intro, outtro);
    }


    private static void normaliseLineEndings(Document document) {
        for (Iterator i = document.getDescendants(new ContentFilter(ContentFilter.COMMENT)); i.hasNext(); ) {
            Comment comment = (Comment)i.next();
            comment.setText(comment.getText().replaceAll("\n", LS));
        }
    }


    private static void writePom(File pomFile, Document document, String intro, String outtro)
          throws IOException {

        Writer writer = null;
        try {
            writer = new FileWriter(pomFile);

            if (intro != null) {
                writer.write(intro);
            }

            Format format = Format.getRawFormat();
            format.setLineSeparator(LS);
            XMLOutputter out = new XMLOutputter(format);
            out.output(document.getRootElement(), writer);

            if (outtro != null) {
                writer.write(outtro);
            }
        }
        finally {
            if (writer != null) {
                try {
                    writer.close();
                }
                catch (IOException ex) {
                    // ignore
                }
            }
        }
    }


    public static Customizer changeParentVersion(String version) {
        return new ChangeParentVersion(version);
    }


    public static interface Customizer {
        void transform(MavenProject project, Element rootElement)
              throws MojoExecutionException, MojoFailureException;
    }

    private static class ChangeParentVersion implements Customizer {
        private final String newParentVersion;


        ChangeParentVersion(String newParentVersion) {
            this.newParentVersion = newParentVersion;
        }


        public void transform(MavenProject project, Element rootElement) {
            if (project.getParent() == null) {
                throw new IllegalArgumentException("Ce projet ne contient pas de POM parent");
            }
            Namespace namespace = rootElement.getNamespace();
            rootElement
                  .getChild("parent", namespace)
                  .getChild("version", namespace)
                  .setText(newParentVersion);
        }
    }
}
