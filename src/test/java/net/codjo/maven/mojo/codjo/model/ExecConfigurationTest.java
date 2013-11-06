/*
 * Team : CODJO AM / OSI / SI / BO
 *
 * Copyright (c) 2001 CODJO Asset Management.
 */
package net.codjo.maven.mojo.codjo.model;
import junit.framework.TestCase;
/**
 *
 */
public class ExecConfigurationTest extends TestCase {
    public void testConstructor() throws Exception {
        ExecConfiguration configuration =
              new ExecConfiguration("name", "description", "execClass", "parameter",
                                    "vmParameter", "workdir", "module");

        assertEquals("name", configuration.getName());
        assertEquals("description", configuration.getDescription());
        assertEquals("execClass", configuration.getExecClass());
        assertEquals("parameter", configuration.getParameter());
        assertEquals("vmParameter", configuration.getVmParameter());
        assertEquals("workdir", configuration.getWorkdir());
        assertEquals("module", configuration.getModule());
    }
}
