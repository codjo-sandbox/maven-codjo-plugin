/*
 * Team : CODJO AM / OSI / SI / BO
 *
 * Copyright (c) 2001 CODJO Asset Management.
 */
package net.codjo.maven.mojo.codjo.model;
import java.util.List;
import junit.framework.TestCase;
/**
 *
 */
public class IdeaConfigurationTest extends TestCase {
    private IdeaConfiguration configuration = new IdeaConfiguration();


    public void testAddExecConfiguration() throws Exception {
        configuration.addExecConfiguration(new ExecConfiguration());

        List list = configuration.getConfigurationExec();

        assertEquals(1, list.size());
    }
}
