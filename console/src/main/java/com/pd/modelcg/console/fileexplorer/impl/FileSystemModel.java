package com.pd.modelcg.console.fileexplorer.impl;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.*;
import java.io.*;

public class FileSystemModel implements TreeModel, Serializable {
    public FileSystemModel(String rootDir) {
        this.rootDir = rootDir;
    }

    public void addTreeModelListener(TreeModelListener listener) {
        this.listener = listener;
    }

    public void removeTreeModelListener(TreeModelListener listener) {
        this.listener = null;
    }

    public Object getRoot() { 
        return new File(rootDir); 
    }

    public Object getChild(Object parent, int index) {
        File directory = (File)parent;
        String[] children = directory.list();
        return new File(directory, children[index]);
    }

    public int getChildCount(Object parent) {
        File fileSysEntity = (File)parent;
        if (fileSysEntity.isDirectory()) {
            String[] children = fileSysEntity.list();
            return children.length;
        }
        else {
            return 0;
        }
    }

    public boolean isLeaf(Object node) { 
        return ((File)node).isFile(); 
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    public int getIndexOfChild(Object parent, Object child) {
        File directory = (File)parent;
        File fileSysEntity = (File)child;
        String[] children = directory.list();
        int result = -1;

        for (int i=0; i<children.length; ++i) {
            if (fileSysEntity.getName().equals(children[i])) {
                result = i;
                break;
            }
        }

        return result;
    }

    private String rootDir;
    private TreeModelListener listener;
}


