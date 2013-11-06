/*
 * Team : CODJO AM / OSI / SI / BO
 *
 * Copyright (c) 2001 CODJO Asset Management.
 */
package net.codjo.maven.mojo.codjo;
import net.codjo.maven.mojo.codjo.scm.CommitCommandMock;
import net.codjo.test.common.LogString;
import net.codjo.test.common.XmlUtil;
import java.io.File;
/**
 *
 */
public class SwitchToReleaseMojoTest extends SwitchLibTestCase {
    private LogString log = new LogString();
    private SwitchToReleaseMojo mojo;


    protected void setUp() throws Exception {
        super.setUp();
        setupEnvironment(getClass().getSimpleName() + "-pom.xml", DUPLICATE_POM);

        mojo = (SwitchToReleaseMojo)lookupMojo("switch-to-release");
        mojo.setCommitCommand(new CommitCommandMock(log));

        mojo.setProject(project);
    }


    public SwitchToReleaseMojoTest() {
        pomFile = new File(PATH + "pom-libToRelease-childDependencies.xml");
        childPomFile = new File(PATH + "\\pom-module\\pom-libToRelease-childDependencies.xml");
    }


    public void test_swichLibToRelease_childDependenciesTest_noCom() throws Exception {
        mockUserAnswer("no");
        mojo.execute();

        XmlUtil.assertEquivalent(
              loadContent(new File(PATH + "pom-libToRelease-childDependencies.etalon.xml")),
              loadContent(pomFile));
        XmlUtil.assertEquivalent(
              loadContent(new File(PATH + "\\pom-module\\pom-libToRelease-childDependencies.etalon.xml")),
              loadContent(childPomFile));

        log.assertContent("");
    }


    public void test_swichLibToRelease_commit() throws Exception {
        mockUserAnswer("yes");
        String fileName = "target\\result.txt";
        mojo.setStabilisationFileName(fileName);

        mojo.execute();

        XmlUtil.assertEquivalent(
              loadContent(new File(PATH + "pom-libToRelease-childDependencies.etalon.xml")),
              loadContent(pomFile));
        XmlUtil.assertEquivalent(
              loadContent(new File(PATH + "\\pom-module\\pom-libToRelease-childDependencies.etalon.xml")),
              loadContent(childPomFile));

        log.assertContent("commit.execute(basedir = " + pomFile.getParent() + "; files = [])");

        assertEquals(
              loadContent(new File(PATH + "\\pom-libToRelease-childDependencies.txt")),
              loadContent(new File(fileName)));
    }
}
