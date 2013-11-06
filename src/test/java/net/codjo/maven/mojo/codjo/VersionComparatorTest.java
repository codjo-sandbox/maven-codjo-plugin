package net.codjo.maven.mojo.codjo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import junit.framework.Assert;
import junit.framework.TestCase;
public class VersionComparatorTest extends TestCase {
    private VersionComparator versionComparator = new VersionComparator();


    public void test_comparator() throws Exception {
        assertTrue(compare("SNAPSHOT", "1.20") < 0);
        assertTrue(compare("1.20", "SNAPSHOT") > 0);
        assertEquals(0, compare("SNAPSHOT", "SNAPSHOT"));

        assertTrue(compare("1.2-rc1", "1.1") > 0);
        assertTrue(compare("1.2-rc1", "1.2") < 0);
        assertTrue(compare("1.2-rc1", "1.2.1") < 0);
        assertTrue(compare("1.2-rc1", "1.3") < 0);
        assertTrue(compare("1.2-rc1", "1.2-rc2") < 0);

        assertTrue(compare("1.2.1", "1.1") > 0);
        assertTrue(compare("1.2.1", "1.2") > 0);
        assertTrue(compare("1.2.1", "1.2.2") < 0);
        assertTrue(compare("1.2.1", "1.3") < 0);
        assertTrue(compare("1.2.1", "1.2-rc1") > 0);

        assertTrue(compare("1.0", "1.0-20061101") > 0);
        assertTrue(compare("1.0-20061101", "1.0") < 0);

        assertTrue(compare("1.0.fixbali", "1.0") < 0);
        assertTrue(compare("1.0-fixbali", "1.0") < 0);
        assertTrue(compare("1.0-rcfixbali", "1.0") < 0);
        assertTrue(compare("1.0.1.1", "1.0") < 0);
    }


    public void test_comparator_list() throws Exception {
        List versionsToSort = new ArrayList();
        versionsToSort.add(new Version("1.10.1"));
        versionsToSort.add(new Version("1.2-rc1"));
        versionsToSort.add(new Version("1.2.1"));
        versionsToSort.add(new Version("1.8"));
        versionsToSort.add(new Version("1.9"));
        versionsToSort.add(new Version("1.10"));
        versionsToSort.add(new Version("1.11-rc2"));
        versionsToSort.add(new Version("1.11-rc1"));
        versionsToSort.add(new Version("1.11"));
        versionsToSort.add(new Version("1.20"));
        versionsToSort.add(new Version("SNAPSHOT"));

        List expectedSortedVersions = new ArrayList();
        expectedSortedVersions.add(new Version("SNAPSHOT"));
        expectedSortedVersions.add(new Version("1.2-rc1"));
        expectedSortedVersions.add(new Version("1.2.1"));
        expectedSortedVersions.add(new Version("1.8"));
        expectedSortedVersions.add(new Version("1.9"));
        expectedSortedVersions.add(new Version("1.10"));
        expectedSortedVersions.add(new Version("1.10.1"));
        expectedSortedVersions.add(new Version("1.11-rc1"));
        expectedSortedVersions.add(new Version("1.11-rc2"));
        expectedSortedVersions.add(new Version("1.11"));
        expectedSortedVersions.add(new Version("1.20"));

        Collections.sort(versionsToSort, new VersionComparator());

        Assert.assertEquals(expectedSortedVersions, versionsToSort);
    }


    private int compare(String version1, String version2) {
        return versionComparator.compare(new Version(version1), new Version(version2));
    }
}
