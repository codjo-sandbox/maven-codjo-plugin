/*
 * Team : CODJO AM / OSI / SI / BO
 *
 * Copyright (c) 2001 CODJO Asset Management.
 */
package net.codjo.maven.mojo.codjo.model;
import com.thoughtworks.xstream.XStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
/**
 *
 */
public class ModelManager {
    private XStream xstream;


    public ModelManager() {
        xstream = new XStream();

        // Configuration
        xstream.alias("idea", IdeaConfiguration.class);
        xstream.alias("execConfiguration", ExecConfiguration.class);
        xstream.addImplicitCollection(IdeaConfiguration.class, "configurationExec");
        xstream.alias("defaultJUnitConfiguration", ExecConfiguration.class);

        // Feature
        xstream.alias("feature", Feature.class);
        xstream.alias("insert", Feature.InsertCommand.class);
        xstream.alias("attribute", Feature.AttributeCommand.class);
        
        xstream.alias("uiPalette", ExecConfiguration.class);
    }


    public IdeaConfiguration loadConfiguration(File inputFile)
          throws IOException {
        if (!inputFile.exists()) {
            return new IdeaConfiguration();
        }

        FileReader reader = new FileReader(inputFile);
        try {
            return (IdeaConfiguration)xstream.fromXML(reader);
        }
        finally {
            reader.close();
        }
    }


    public Feature readFeature(String featureInString) {
        return (Feature)xstream.fromXML(new StringReader(featureInString));
    }
}
