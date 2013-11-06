package net.codjo.maven.mojo.codjo;
/**
 *
 */
public class SwitchToParentSnapshotMojoTest extends SwitchToParentMojoTestCase {

    protected String getExpectedParentVersion() {
        return "SNAPSHOT";
    }


    protected String getGoal() {
        return "switch-to-parent-snapshot";
    }
}
