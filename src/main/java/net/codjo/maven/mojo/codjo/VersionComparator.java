package net.codjo.maven.mojo.codjo;
import java.util.Comparator;
public class VersionComparator implements Comparator {

    public int compare(Object o1, Object o2) {
        if (o1 instanceof String && o2 instanceof String) {
            return compare(new Version((String)o1), new Version((String)o2));
        }
        if (o1 instanceof Version && o2 instanceof Version) {
            return compare((Version)o1, (Version)o2);
        }
        throw new RuntimeException(
              "o1 and o2 must be of type net.codjo.maven.mojo.codjo.Version or java.lang.String");
    }


    private int compare(Version version1, Version version2) {
        if (version1.equals(version2)) {
            return 0;
        }

        if (version1.getType() == Version.BAD_VERSION) {
            return -1;
        }

        if (version2.getType() == Version.BAD_VERSION) {
            return 1;
        }

        if (version1.getMajor() == version2.getMajor()) {
            if (version1.getMinor() == version2.getMinor()) {
                if (version1.getType() == version2.getType()) {
                    return version1.getSuffix() - version2.getSuffix();
                }
                else {
                    return compareVersionsOfDifferentType(version1, version2);
                }
            }
            else {
                return version1.getMinor() - version2.getMinor();
            }
        }
        else {
            return version1.getMajor() - version2.getMajor();
        }
    }


    private int compareVersionsOfDifferentType(Version version1, Version version2) {
        switch (version1.getType()) {
            case Version.PATCH_VERSION:
                return 1;

            case Version.RC_VERSION:
                return -1;

            default:
                switch (version2.getType()) {
                    case Version.PATCH_VERSION:
                        return -1;

                    default:
                        return 1;
                }
        }
    }
}
