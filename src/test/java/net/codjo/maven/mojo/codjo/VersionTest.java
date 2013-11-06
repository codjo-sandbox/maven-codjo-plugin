package net.codjo.maven.mojo.codjo;
import junit.framework.TestCase;
public class VersionTest extends TestCase {

    public void test_constructeur() throws Exception {
        assertEquals(Version.BAD_VERSION, new Version(null).getType());
        assertEquals(Version.BAD_VERSION, new Version("").getType());

        Version normalVersion = new Version("1.60");
        assertEquals(1, normalVersion.getMajor());
        assertEquals(60, normalVersion.getMinor());
        assertEquals(0, normalVersion.getSuffix());
        assertEquals(Version.RELEASE_VERSION, normalVersion.getType());

        Version normalVersionWithZero = new Version("1.0");
        assertEquals(1, normalVersionWithZero.getMajor());
        assertEquals(0, normalVersionWithZero.getMinor());
        assertEquals(0, normalVersionWithZero.getSuffix());
        assertEquals(Version.RELEASE_VERSION, normalVersionWithZero.getType());

        Version patchVersion = new Version("1.60.1");
        assertEquals(1, patchVersion.getMajor());
        assertEquals(60, patchVersion.getMinor());
        assertEquals(1, patchVersion.getSuffix());
        assertEquals(Version.PATCH_VERSION, patchVersion.getType());

        Version rcVersion = new Version("1.60-rc1");
        assertEquals(1, rcVersion.getMajor());
        assertEquals(60, rcVersion.getMinor());
        assertEquals(1, rcVersion.getSuffix());
        assertEquals(Version.RC_VERSION, rcVersion.getType());

        Version badVersion = new Version("1.0-20061101");
        assertEquals(0, badVersion.getMajor());
        assertEquals(0, badVersion.getMinor());
        assertEquals(0, badVersion.getSuffix());
        assertEquals(Version.BAD_VERSION, badVersion.getType());
    }


    public void test_toString() throws Exception {
        assertEquals("1.0", new Version("1.0").toString());
        assertEquals("1.0.1", new Version("1.0.1").toString());
        assertEquals("1.0-rc1", new Version("1.0-rc1").toString());
        assertEquals("BAD_VERSION", new Version("1.0-20061101").toString());
    }


    public void test_equals() {
        assertTrue(new Version("1.1").equals(new Version("1.1")));
        assertFalse(new Version("1.1").equals(new Version("1.1.1")));

        assertTrue(new Version("df:g,m").equals(new Version("wdfgjl")));
    }


    public void test_nextRelease() throws Exception {
        assertEquals(new Version("1.0"), new Version("1.0-rc2").nextRelease());
        assertEquals(new Version("1.1"), new Version("1.0").nextRelease());
        assertEquals(new Version("1.1"), new Version("1.0.1").nextRelease());
    }


    public void test_nextPatch() throws Exception {
        assertEquals(new Version("1.0.1"), new Version("1.0").nextPatch());
        assertEquals(new Version("1.0.6"), new Version("1.0.5").nextPatch());

        try {
            new Version("1.0-rc2").nextPatch();
            fail("Impossible de determiner une version patch a partir d'une release candidate");
        }
        catch (Exception e) {
        }
    }


    public void test_nextReleaseCandidate() throws Exception {
        assertEquals(new Version("1.1-rc1"), new Version("1.0").nextReleaseCandidate());
        assertEquals(new Version("1.1-rc1"), new Version("1.0.5").nextReleaseCandidate());
        assertEquals(new Version("1.0-rc9"), new Version("1.0-rc8").nextReleaseCandidate());
    }
}
