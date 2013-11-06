package net.codjo.maven.mojo.codjo;
public class Version {
    public static final int BAD_VERSION = -1;
    public static final int RELEASE_VERSION = 1;
    public static final int PATCH_VERSION = 2;
    public static final int RC_VERSION = 3;
    private int major = 0;
    private int minor = 0;
    private int suffix = 0;
    private int type = BAD_VERSION;


    public Version(String version) {
        try {
            String[] tokens = version.split("\\.");
            major = Integer.parseInt(tokens[0]);
            if (tokens.length == 2) {
                String[] rcTokens = tokens[1].split("-rc");
                if (rcTokens.length == 1) {
                    minor = Integer.parseInt(rcTokens[0]);
                    type = RELEASE_VERSION;
                }
                else if (rcTokens.length == 2) {
                    minor = Integer.parseInt(rcTokens[0]);
                    suffix = Integer.parseInt(rcTokens[1]);
                    type = RC_VERSION;
                }
            }
            else if (tokens.length == 3) {
                minor = Integer.parseInt(tokens[1]);
                suffix = Integer.parseInt(tokens[2]);
                type = PATCH_VERSION;
            }
        }
        catch (Exception e) {
            ;
        }

        if (type == BAD_VERSION) {
            major = 0;
            minor = 0;
            suffix = 0;
        }
    }


    protected Version(int major, int minor) {
        this.type = RELEASE_VERSION;
        this.major = major;
        this.minor = minor;
    }


    protected Version(int type, int major, int minor, int suffix) {
        this.type = type;
        this.major = major;
        this.minor = minor;
        this.suffix = suffix;
    }


    public int getType() {
        return type;
    }


    public int getMajor() {
        return major;
    }


    public int getMinor() {
        return minor;
    }


    public int getSuffix() {
        return suffix;
    }


    public String toString() {
        switch (type) {
            case RELEASE_VERSION:
                return major + "." + minor;

            case PATCH_VERSION:
                return major + "." + minor + "." + suffix;

            case RC_VERSION:
                return major + "." + minor + "-rc" + suffix;

            default:
                return "BAD_VERSION";
        }
    }


    public boolean equals(Object obj) {
        if (obj instanceof Version) {
            Version version = (Version)obj;
            return version.type == type
                   && version.major == major
                   && version.minor == minor
                   && version.suffix == suffix;
        }
        return false;
    }


    public Version nextRelease() {
        switch (type) {
            case RC_VERSION:
                return new Version(major, minor);

            default:
                return new Version(major, minor + 1);
        }
    }


    public Version nextPatch() {
        switch (type) {
            case RC_VERSION:
                throw new RuntimeException("");

            default:
                return new Version(PATCH_VERSION, major, minor, suffix + 1);
        }
    }


    public Version nextReleaseCandidate() {
        switch (type) {
            case RC_VERSION:
                return new Version(RC_VERSION, major, minor, suffix + 1);

            default:
                return new Version(RC_VERSION, major, minor + 1, 1);
        }
    }
}
