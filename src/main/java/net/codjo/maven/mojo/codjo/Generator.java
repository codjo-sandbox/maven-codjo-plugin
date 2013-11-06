/*
 * Team : CODJO AM / OSI / SI / BO
 *
 * Copyright (c) 2001 CODJO Asset Management.
 */
package net.codjo.maven.mojo.codjo;
import net.codjo.maven.mojo.codjo.model.IdeaConfiguration;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.log.NullLogSystem;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
/**
 *
 */
public class Generator {
    private VelocityEngine engine = new VelocityEngine();
    private final String templateName;


    public Generator(String templateName) throws Exception {
        this.templateName = templateName;
        configure();
    }


    public String generate(IdeaConfiguration config, Properties properties)
          throws Exception {
        Template template = engine.getTemplate(templateName);

        VelocityContext context = createContext();
        context.put("idea", config);

        for (Iterator iter = properties.entrySet().iterator(); iter.hasNext();) {
            Map.Entry entry = (Map.Entry)iter.next();
            context.put((String)entry.getKey(), entry.getValue());
        }

        Writer writer = new StringWriter();
        template.merge(context, writer);
        return writer.toString();
    }


    private VelocityContext createContext() {
        return new VelocityContext();
    }


    private void configure() throws Exception {
        Properties props = new Properties();
        props.setProperty(VelocityEngine.RESOURCE_LOADER,
                          "classpath");
        props.setProperty("classpath." + VelocityEngine.RESOURCE_LOADER + ".class",
                          ClasspathResourceLoader.class.getName());
        props.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM_CLASS,
                          NullLogSystem.class.getName());
        engine.init(props);
    }
}
