/*
 * Team : CODJO AM / OSI / SI / BO
 *
 * Copyright (c) 2001 CODJO Asset Management.
 */
package net.codjo.maven.mojo.codjo.model;
import net.codjo.maven.common.test.PathUtil;
import java.io.File;
import java.util.List;
import junit.framework.TestCase;
/**
 *
 */
public class ModelManagerTest extends TestCase {
    private final ModelManager modelManager = new ModelManager();


    public void testReadConfigurationFromAnInexistantFile()
          throws Exception {
        IdeaConfiguration idea =
              modelManager.loadConfiguration(new File("/doNotExist.xml"));

        assertNotNull(idea);
        assertNotNull(idea.getConfigurationExec());
        assertEquals(0, idea.getConfigurationExec().size());
    }


    public void testReadAnEmptyFileConfiguration()
          throws Exception {
        IdeaConfiguration idea =
              modelManager.loadConfiguration(toFile("/idea-empty.xml"));
        assertNotNull(idea);
    }


    public void testReadOneConfiguration() throws Exception {
        IdeaConfiguration idea =
              modelManager.loadConfiguration(toFile("/idea-oneConfiguration.xml"));

        List execConfigurationList = idea.getConfigurationExec();
        assertEquals(1, execConfigurationList.size());

        ExecConfiguration first = (ExecConfiguration)execConfigurationList.get(0);
        assertEquals("Client GABI(AutoLogin)", first.getName());
        assertEquals("Lance le client Gabi en login automatique", first.getDescription());
        assertEquals("net.codjo.gabi.gui.GabiMain", first.getExecClass());
        assertEquals("GABI GABIUSER localhost 15700", first.getParameter());
        assertEquals("-Dlog4j.configuration=...", first.getVmParameter());
        assertEquals("./application/client", first.getWorkdir());
        assertEquals("gabi-client", first.getModule());
        
        ExecConfiguration defaultJUnitConfiguration = idea.getDefaultJUnitConfiguration();
        assertEquals("-Djava.library.path=...", defaultJUnitConfiguration.getVmParameter());

    }


    public void testReadOneSimpleFeature() throws Exception {
        String featureInString =
              "<feature xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"features.xsd\">"
              + "    <name>my name</name>" + "    <comment>Un commentaire.</comment>"
              + "</feature>";

        Feature feature = modelManager.readFeature(featureInString);

        assertNotNull(feature);
        assertEquals("my name", feature.getName());
        assertEquals("Un commentaire.", feature.getComment());
    }


    public void testReadFeatureWithIwsPart() throws Exception {
        String featureInString =
              "<feature>                                       "
              + " <iws>                                        "
              + "   <insert>                                   "
              + "      <path>//project</path>                  "
              + "      <position>after</position>              "
              + "      <content><![CDATA[xml to be inserted]]></content>"
              + "    </insert>                                 "
              + " </iws>                                       "
              + "</feature>                                    ";

        Feature feature = modelManager.readFeature(featureInString);

        assertOneCommand(new Feature.InsertCommand("//project", "after", "xml to be inserted"),
                         feature.getIws());
    }


    public void testReadFeatureWithIprPart() throws Exception {
        String featureInString =
              "<feature>                                       "
              + " <ipr>                                        "
              + "   <insert>                                   "
              + "      <path>//project</path>                  "
              + "      <position>after</position>              "
              + "      <content><![CDATA[xml to be inserted]]></content>"
              + "    </insert>                                 "
              + " </ipr>                                       "
              + "</feature>                                    ";

        Feature feature = modelManager.readFeature(featureInString);

        assertOneCommand(new Feature.InsertCommand("//project", "after", "xml to be inserted"),
                         feature.getIpr());
    }


    private void assertOneCommand(Feature.InsertCommand expected, List commandList) {
        assertNotNull(commandList);
        assertEquals(1, commandList.size());

        Feature.InsertCommand command = (Feature.InsertCommand)commandList.get(0);
        assertCommand(expected, command);
    }


    private void assertCommand(Feature.InsertCommand expected,
                               Feature.InsertCommand command) {
        assertEquals(expected.getPath(), command.getPath());
        assertEquals(expected.getPosition(), command.getPosition());
        assertEquals(expected.getContent(), command.getContent().trim());
    }


    protected File toFile(String resourceName) {
        return PathUtil.find(ModelManagerTest.class, resourceName);
    }
}
