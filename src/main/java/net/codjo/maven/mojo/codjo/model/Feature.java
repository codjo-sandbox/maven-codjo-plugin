/*
 * Team : CODJO AM / OSI / SI / BO
 *
 * Copyright (c) 2001 CODJO Asset Management.
 */
package net.codjo.maven.mojo.codjo.model;
import com.oopsconsultancy.xmltask.ant.Attr;
import com.oopsconsultancy.xmltask.ant.Insert;
import com.oopsconsultancy.xmltask.ant.XmlTask;
import java.util.ArrayList;
import java.util.List;
/**
 *
 */
public class Feature {
    private String name;
    private String comment;
    private List iws = new ArrayList();
    private List ipr = new ArrayList();


    public Feature(String name, String comment) {
        this.name = name;
        this.comment = comment;
    }


    public Feature() {
    }


    public String getComment() {
        return comment;
    }


    public String getName() {
        return name;
    }


    public List getIws() {
        return iws;
    }


    public List getIpr() {
        return ipr;
    }


    public static interface Command {
        void applyCommand(XmlTask task) throws Exception;
    }

    public static class InsertCommand implements Command {
        private String path;
        private String position;
        private String content;


        public InsertCommand(String path, String position, String content) {
            this.path = path;
            this.position = position;
            this.content = content;
        }


        public InsertCommand() {
        }


        public void applyCommand(XmlTask task) throws Exception {
            Insert insert = task.createInsert();
            insert.setPath(getPath());
            insert.setPosition(getPosition());
            insert.addText(getContent());
        }


        public String getPath() {
            return path;
        }


        public String getPosition() {
            return position;
        }


        public String getContent() {
            return content;
        }
    }

    public class AttributeCommand implements Command {
        private String path;
        private String name;
        private String content;


        public AttributeCommand(String path, String name, String content) {
            this.path = path;
            this.name = name;
            this.content = content;
        }


        public AttributeCommand() {
        }


        public void applyCommand(XmlTask task) throws Exception {
            Attr attr = task.createAttr();
            attr.setPath(path);
            attr.setAttr(name);
            attr.setValue(content);
        }

    }
}
