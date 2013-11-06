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
public class FeatureTest extends TestCase {
    public void testConstructor() throws Exception {
        Feature feature = new Feature("name", "comment");
        assertEquals("name", feature.getName());
        assertEquals("comment", feature.getComment());
    }


    public void testEmptyConstructor() throws Exception {
        Feature feature = new Feature();
        assertEquals(null, feature.getName());
        assertEquals(null, feature.getComment());
    }


    public void testInsertCommand() throws Exception {
        Feature.InsertCommand command = new Feature.InsertCommand("path", "pos", "content");
        assertEquals("path", command.getPath());
        assertEquals("pos", command.getPosition());
        assertEquals("content", command.getContent());
    }


    public void testInsertCommandWithEmptyConstructor()
          throws Exception {
        Feature.InsertCommand command = new Feature.InsertCommand();
        assertEquals(null, command.getPath());
        assertEquals(null, command.getPosition());
        assertEquals(null, command.getContent());
    }
}
