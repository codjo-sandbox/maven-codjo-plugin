package net.codjo.maven.mojo.codjo;
import java.io.File;
import net.codjo.test.common.XmlUtil;
/**
 *
 */
public class RollbackSnapshotMojoTest extends SwitchLibTestCase {
    RollbackSnapshotMojo mojo;


    public RollbackSnapshotMojoTest() {
        pomFile = new File(PATH + "pom-libRollbackSnapshot.xml");
        childPomFile = new File(PATH + "\\pom-module\\pom-libRollbackSnapshot.xml");
    }


    protected void setUp() throws Exception {
        super.setUp();
        mojo = new RollbackSnapshotMojo();
        mojo.setProject(project);
    }


    public void test_execute_noParameter() throws Exception {
        mojo.execute();
        XmlUtil.assertEquivalent(loadContent(new File(PATH + "pom-libRollbackSnapshot.xml")), loadContent(pomFile));
    }


    public void test_execute_swichLib() throws Exception {
        mojo.setLib("mad");
        mojo.execute();

        XmlUtil.assertEquivalent(loadContent(new File(PATH + "pom-libRollbackSnapshot-lib.etalon.xml")),
                                 loadContent(pomFile));
    }


    public void test_execute_swichLibInPluginManagement() throws Exception {
        mojo.setLib("database");
        mojo.execute();

        XmlUtil.assertEquivalent(loadContent(new File(
              PATH + "pom-libRollbackSnapshot-libInPluginManagement.etalon.xml")),
                                 loadContent(pomFile));
    }

//    public void test_execute_swichLibAndPlugin_inChild() throws Exception {
//        mojo.setLib("maven");
//        mojo.execute();
//
//        // on modifie une lib puis un plugin
//        mojo.setLib(null);
//        mojo.setPlugin("database");
//        mojo.execute();
//
//        XmlUtil.assertEquivalent(loadContent(new File(PATH + "\\pom-module\\pom-libRollbackSnapshot-lib.etalon.xml")),
//                                 loadContent(childPomFile));
//    }
//
//
//    public void test_execute_swichPlugin() throws Exception {
//        mojo.setPlugin("datagen");
//        mojo.execute();
//
//        XmlUtil.assertEquivalent(loadContent(new File(PATH + "pom-libRollbackSnapshot-plugin.etalon.xml")),
//                                 loadContent(pomFile));
//    }


    public void test_findReleaseVersion() throws Exception {
        assertEquals("2.55", mojo.findReleaseVersion("2.56-SNAPSHOT"));
        assertEquals("2.9", mojo.findReleaseVersion("2.10-SNAPSHOT"));
    }


    public void test_findAGIReleaseVersion() throws Exception {
        assertEquals("2.55-agi", mojo.findReleaseVersion("2.56-agi-SNAPSHOT"));
        assertEquals("2.9-agi", mojo.findReleaseVersion("2.10-agi-SNAPSHOT"));
    }


    public void test_computeNewVersion_alreadySnapshot() throws Exception {
        try {
            mojo.findReleaseVersion("2.55");
            fail();
        }
        catch (Exception e) {
            assertEquals("N'est pas en SNAPSHOT", e.getLocalizedMessage());
        }
    }


    public void test_execute_badLibName() throws Exception {
        try {
            mojo.setLib("maaaad");
            mojo.execute();
            fail("maaad ne peut pas etre trouve...");
        }
        catch (Exception e) {
            assertEquals(
                  "Impossible de changer la version des librairies : pas de libraire ni de plugin correspondant.",
                  e.getLocalizedMessage());
            assertEquals(loadContent(new File(PATH + "pom-libRollbackSnapshot.xml")), loadContent(pomFile));
        }
    }

//    public void test_execute_badPluginName() throws Exception {
//        try {
//            mojo.setLib("daatagen");
//            mojo.execute();
//            fail("daatagen ne peut pas etre trouve...");
//        }
//        catch (Exception e) {
//            assertEquals(
//                  "Impossible de changer la version des librairies : pas de libraire ni de plugin correspondant.",
//                  e.getLocalizedMessage());
//            assertEquals(loadContent(new File(PATH + "pom-libRollbackSnapshot.xml")), loadContent(pomFile));
//        }
//    }
}