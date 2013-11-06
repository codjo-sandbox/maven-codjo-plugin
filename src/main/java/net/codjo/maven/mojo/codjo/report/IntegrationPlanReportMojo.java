/*
 * Team : CODJO AM / OSI / SI / BO
 *
 * Copyright (c) 2001 CODJO Asset Management.
 */
package net.codjo.maven.mojo.codjo.report;
import net.codjo.maven.common.report.XslGenerator;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Locale;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;
/**
 * Generation d'un rapport portant sur les plans d'intégration.
 *
 * @goal integrationPlan-report
 */
public class IntegrationPlanReportMojo extends AbstractMavenReport {
    /**
     * Repertoire contenant les plans d'intégration.
     *
     * @parameter expression="${integrationPlansDirectory}" default-value="${project.basedir}/src/main/resources/META-INF/integrationPlans"
     * @required
     * @noinspection UNUSED_SYMBOL
     */
    private String integrationPlansDirectory;
    /**
     * Repertoire contenant les resources.
     *
     * @parameter expression="${project.basedir}/src/main/resources"
     * @required
     * @readonly
     * @noinspection UNUSED_SYMBOL
     */
    private String resourcesDirectory;
    /**
     * Repertoire de destination du rapport.
     *
     * @parameter expression="${project.reporting.outputDirectory}"
     * @required
     * @readonly
     * @noinspection UNUSED_SYMBOL
     */
    private String outputDirectory;
    /**
     * @parameter expression="${component.org.apache.maven.doxia.siterenderer.Renderer}"
     * @required
     * @readonly
     * @noinspection UNUSED_SYMBOL
     */
    private Renderer siteRenderer;
    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     * @noinspection UNUSED_SYMBOL
     */
    private MavenProject project;
    private static final String INTEGRATION_PLAN_OUTPUT = "integrationPlan";
    private XslGenerator xslGenerator = new XslGenerator();


    public boolean canGenerateReport() {
        return new File(integrationPlansDirectory).exists();
    }


    protected Renderer getSiteRenderer() {
        return siteRenderer;
    }


    protected String getOutputDirectory() {
        return outputDirectory;
    }


    protected MavenProject getProject() {
        return project;
    }


    public String getDescription(Locale locale) {
        return "Documentation sur les plans d'intégration du projet";
    }


    public String getName(Locale locale) {
        return "Plans d'intégration";
    }


    public String getOutputName() {
        return "integrationPlan-report";
    }


    public XslGenerator getXslGenerator() {
        return xslGenerator;
    }


    public void setXslGenerator(XslGenerator xslGenerator) {
        this.xslGenerator = xslGenerator;
    }


    protected void executeReport(Locale locale) throws MavenReportException {
        File[] planFiles = new File(integrationPlansDirectory).listFiles(new IntegrationPlanFilter());

        generateReportIndex(getSink(), planFiles);

        File outputIPDirectory = new File(this.outputDirectory + "/" + INTEGRATION_PLAN_OUTPUT);
        outputIPDirectory.mkdirs();
        xslGenerator.setXslResourceName("/IntegrationPlan.xsl", resourcesDirectory);

        for (int i = 0; i < planFiles.length; i++) {
            File planFile = planFiles[i];

            getLog().info("Generation de la documentation du plan " + planFile.getName());

            File output = new File(outputIPDirectory, planFile.getName() + ".html");
            xslGenerator.generate(planFile, output);
        }
    }


    private void generateReportIndex(Sink sink, File[] planFiles) {
        beginReport(sink);

        sink.table();

        sink.tableRow();
        createHeaderCell(sink, "Nom");
        createHeaderCell(sink, "Documentation");
        sink.tableRow_();

        for (int i = 0; i < planFiles.length; i++) {
            File planFile = planFiles[i];
            sink.tableRow();

            sink.tableCell();
            sink.text(planFile.getName());
            sink.tableCell_();

            sink.tableCell();
            sink.link("./" + INTEGRATION_PLAN_OUTPUT + "/" + planFile.getName() + ".html");
            sink.text("documentation");
            createFigure(sink, "images/external.png");
            sink.link_();
            sink.tableCell_();

            sink.tableRow_();
        }

        sink.table_();

        endReport(sink);
    }


    private void beginReport(Sink sink) {
        sink.head();
        sink.text("Plans d'intégration");
        sink.head_();

        sink.body();

        sink.sectionTitle1();
        sink.anchor("title-plan");
        sink.anchor_();
        sink.text("Liste des plans d'intégration");
        sink.sectionTitle1_();
    }


    private void endReport(Sink sink) {
        sink.body_();
        sink.flush();
        sink.close();
    }


    private void createHeaderCell(Sink sink, String header) {
        sink.tableHeaderCell();

        sink.text(header);

        sink.tableHeaderCell_();
    }


    private void createFigure(Sink sink, String image) {
        sink.figure();

        sink.figureGraphics(image);

        sink.figure_();
    }


    // *******************************************************************************************************
    private static class IntegrationPlanFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return name.endsWith(".xml");
        }
    }
}
