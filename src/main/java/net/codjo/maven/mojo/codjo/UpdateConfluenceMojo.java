package net.codjo.maven.mojo.codjo;
/**
 *
 */
public abstract class UpdateConfluenceMojo extends ConfluenceMojo {
    /**
     * @parameter expression="${confluenceSpaceKey}" default-value="framework"
     * @noinspection UnusedDeclaration
     */
    protected String confluenceSpaceKey;
    /**
     * @parameter expression="${confluencePage}" default-value="Home"
     * @noinspection UnusedDeclaration
     */
    protected String confluencePage;
    public static final String WARNING_RELEASE_STARTED
          = "\n{warning:title=Attention}Stabilisation en cours\n{warning}";
}
