package net.codjo.maven.mojo.codjo;
import java.util.ArrayList;
import java.util.List;
/**
 *
 */
public class SwitchToParentReleaseMojoTest extends SwitchToParentMojoTestCase {

    protected void setUp() throws Exception {
        super.setUp();
        PomUtils.mockLastPomVersion(getExpectedParentVersion());
    }


    protected String getExpectedParentVersion() {
        return "1.18";
    }


    protected String getGoal() {
        return "switch-to-parent-release";
    }


    public void test_lastVersionIsRC() throws Exception {
        List versions = new ArrayList();
        versions.add("1.17");
        versions.add("1.18");
        PomUtils.mockOldPomVersions(versions);
        PomUtils.mockLastPomVersion("1.19-rc1");

        assertParentVersion("1.18");
    }


    public void test_lastVersionIsRC_mustRetrieveThePatchVersion() throws Exception {
        List versions = new ArrayList();
        versions.add("1.17");
        versions.add("1.18");
        versions.add("1.18.1");
        PomUtils.mockOldPomVersions(versions);
        PomUtils.mockLastPomVersion("1.19-rc1");

        assertParentVersion("1.18.1");
    }
}
