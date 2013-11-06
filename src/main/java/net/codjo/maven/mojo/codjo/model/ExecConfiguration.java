package net.codjo.maven.mojo.codjo.model;
/**
 *
 */
public class ExecConfiguration {
    private String name;
    private String description;
    private String execClass;
    private String parameter;
    private String vmParameter;
    private String workdir;
    private String module;


    public ExecConfiguration() {
    }


    public ExecConfiguration(String name,
                             String description,
                             String execClass,
                             String parameter,
                             String vmParameter,
                             String workdir,
                             String module) {
        this.name = name;
        this.description = description;
        this.execClass = execClass;
        this.parameter = parameter;
        this.vmParameter = vmParameter;
        this.workdir = workdir;
        this.module = module;
    }


    public String getName() {
        return name;
    }


    public String getDescription() {
        return description;
    }


    public String getExecClass() {
        return execClass;
    }


    public String getParameter() {
        return parameter;
    }


    public String getVmParameter() {
        return vmParameter;
    }


    public String getWorkdir() {
        return workdir;
    }


    public String getModule() {
        return module;
    }
}
