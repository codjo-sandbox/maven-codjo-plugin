/*
 * Team : CODJO AM / OSI / SI / BO
 *
 * Copyright (c) 2001 CODJO Asset Management.
 */
package net.codjo.maven.mojo.codjo.model;
import java.util.ArrayList;
import java.util.List;
/**
 *
 */
public class IdeaConfiguration {
    private List configurationExec = new ArrayList();
    private ExecConfiguration defaultJUnitConfiguration;

    public IdeaConfiguration() {}


    public void addExecConfiguration(ExecConfiguration execConfiguration) {
        configurationExec.add(execConfiguration);
    }


    public List getConfigurationExec() {
        return configurationExec;
    }


    public ExecConfiguration getDefaultJUnitConfiguration() {
        return defaultJUnitConfiguration;
    }
}
