package net.codjo.maven.mojo.codjo;
import java.io.File;
import net.codjo.test.common.XmlUtil;
/**
 *
 */
public class SwitchToSnapshotMojoTest extends SwitchLibTestCase {
    SwitchToSnapshotMojo mojo;


    public SwitchToSnapshotMojoTest() {
        pomFile = new File(PATH + "pom-libToSnapshot.xml");
        childPomFile = new File(PATH + "\\pom-module\\pom-libToSnapshot.xml");
    }


    protected void setUp() throws Exception {
        super.setUp();
        mojo = new SwitchToSnapshotMojo();
        mojo.setProject(project);
    }


    public void test_execute_noParameter() throws Exception {
        mojo.execute();
        XmlUtil.assertEquivalent(loadContent(new File(PATH + "pom-libToSnapshot.xml")), loadContent(pomFile));
    }


    public void test_execute_swichLib() throws Exception {
        mojo.setLib("mad");
        mojo.execute();

        XmlUtil.assertEquivalent(loadContent(new File(PATH + "pom-libToSnapshot-lib.etalon.xml")),
                                 loadContent(pomFile));
    }


    public void test_execute_swichLibInPluginManagement() throws Exception {
        mojo.setLib("database");
        mojo.execute();

        XmlUtil.assertEquivalent(loadContent(new File(PATH + "pom-libToSnapshot-libInPluginManagement.etalon.xml")),
                                 loadContent(pomFile));
    }


    public void test_execute_swichLibAndPlugin_inChild() throws Exception {
        mojo.setLib("maven");
        mojo.execute();

        // on modifie une lib puis un plugin
        mojo.setLib(null);
        mojo.setPlugin("database");
        mojo.execute();

        XmlUtil.assertEquivalent(loadContent(new File(PATH + "\\pom-module\\pom-libToSnapshot.etalon.xml")),
                                 loadContent(childPomFile));
    }


    public void test_execute_swichPlugin() throws Exception {
        mojo.setPlugin("datagen");
        mojo.execute();

        XmlUtil.assertEquivalent(loadContent(new File(PATH + "pom-libToSnapshot-plugin.etalon.xml")),
                                 loadContent(pomFile));
    }


    public void test_computeNewVersion() throws Exception {
        assertEquals("2.56-SNAPSHOT", mojo.computeNewVersion("2.55"));
        assertEquals("2.10-SNAPSHOT", mojo.computeNewVersion("2.9"));
    }


    public void test_computeNewVersion_alreadySnapshot() throws Exception {
        try {
            mojo.computeNewVersion("2.55-SNAPSHOT");
            fail();
        }
        catch (Exception e) {
            assertEquals("Deja en SNAPSHOT", e.getLocalizedMessage());
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
            assertEquals(loadContent(new File(PATH + "pom-libToSnapshot.xml")), loadContent(pomFile));
        }
    }


    public void test_execute_badPluginName() throws Exception {
        try {
            mojo.setLib("daatagen");
            mojo.execute();
            fail("daatagen ne peut pas etre trouve...");
        }
        catch (Exception e) {
            assertEquals(
                  "Impossible de changer la version des librairies : pas de libraire ni de plugin correspondant.",
                  e.getLocalizedMessage());
            assertEquals(loadContent(new File(PATH + "pom-libToSnapshot.xml")), loadContent(pomFile));
        }
    }
}