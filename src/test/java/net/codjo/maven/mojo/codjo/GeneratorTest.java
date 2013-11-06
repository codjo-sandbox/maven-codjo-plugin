/*
 * Team : CODJO AM / OSI / SI / BO
 *
 * Copyright (c) 2001 CODJO Asset Management.
 */
package net.codjo.maven.mojo.codjo;
import net.codjo.maven.mojo.codjo.model.ExecConfiguration;
import net.codjo.maven.mojo.codjo.model.IdeaConfiguration;
import java.util.Properties;
import junit.framework.TestCase;
/**
 *
 */
public class GeneratorTest extends TestCase {
    public void testSimpleTemplate() throws Exception {
        Generator generator = new Generator("simple-template.vm");

        String result = generator.generate(new IdeaConfiguration(), new Properties());

        assertEquals("simple", result.trim());
    }


    public void testComplexTemplate() throws Exception {
        Generator generator = new Generator("complex-template.vm");

        IdeaConfiguration config = new IdeaConfiguration();
        config.addExecConfiguration(
              new ExecConfiguration("me", null, "${myClass}", null, null, null, null));

        Properties properties = new Properties();
        properties.setProperty("myClass", "net.codjo.Toto");

        String result = generator.generate(config, properties);

        assertEquals("class ${myClass}", result.trim());
    }


    public void testTemplateUsingIdeaVariable() throws Exception {
        Generator generator = new Generator("complex-usingIdeaVariable-template.vm");

        IdeaConfiguration config = new IdeaConfiguration();
        config.addExecConfiguration(
              new ExecConfiguration("me", null, "net.codjo.Toto", null, null, null, null));
        String result = generator.generate(config, new Properties());

        assertEquals("me / net.codjo.Toto", result.trim());
    }


    public void testTemplateUsingProperties() throws Exception {
        Generator generator = new Generator("complex-usingProperties-template.vm");

        IdeaConfiguration config = new IdeaConfiguration();
        Properties properties = new Properties();
        properties.setProperty("myProperty", "la valeur");
        String result = generator.generate(config, properties);

        assertEquals("la valeur", result.trim());
    }
}
