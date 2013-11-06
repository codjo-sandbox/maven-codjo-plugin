package net.codjo.maven.mojo.codjo.report;
import net.codjo.maven.common.mock.AgfMojoTestCase;
import net.codjo.maven.common.mock.RendererMock;
import net.codjo.maven.common.report.XslGeneratorMock;
import net.codjo.maven.common.test.LogString;
/**
 *
 */
public class IntegrationPlanReportMojoTest extends AgfMojoTestCase {
    private LogString log = new LogString();
    private LogString report = new LogString();


    public void test_execute_ideaFailure() throws Exception {
        setupEnvironment("/mojos/pom-ipreport.xml");

        IntegrationPlanReportMojo mojo = (IntegrationPlanReportMojo)lookupMojo("integrationPlan-report");
        mojo.setXslGenerator(new XslGeneratorMock(log));

        ((RendererMock)mojo.getSiteRenderer()).setLog(report);

        mojo.execute();

        log.assertContent("setXslResourceName(/IntegrationPlan.xsl, ./target/test-classes/mojos)"
                          + ", generate(.\\target\\test-classes\\mojos\\integrationPlans\\first-integration-plan.xml, .\\target\\report\\integrationPlan\\first-integration-plan.xml.html)"
                          + ", generate(.\\target\\test-classes\\mojos\\integrationPlans\\second-integration-plan.xml, .\\target\\report\\integrationPlan\\second-integration-plan.xml.html)");

        report.assertContent(
              "Plans d'intégration, Liste des plans d'intégration, Nom, Documentation, first-integration-plan.xml, documentation, second-integration-plan.xml, documentation");
    }
}
