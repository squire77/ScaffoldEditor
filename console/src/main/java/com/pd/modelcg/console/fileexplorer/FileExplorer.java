package com.pd.modelcg.console.fileexplorer;

import com.pd.modelcg.console.fileexplorer.impl.FileSystemTreePanel;

import java.io.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.event.*;
import javax.swing.tree.TreeSelectionModel;

/**
 * Provides a FileSystemTreePanel rooted at the rootDirAbsolutePath.
 *  - use getComponent() returns UI Component for enable UI refresh
 *  - use getRootDirAbsolutionPath() to get the root directory
 *  - use clear() to clear any current selection
 */
public class FileExplorer {
    public FileExplorer(IFileOpener opener, String rootDirAbsolutePath) {
        this.opener = opener;

        createFileSystemTreePanel(rootDirAbsolutePath);
    }
    
    public Component getComponent() { return fileTree; }
        
    public String getRootDirAbsolutePath() { return this.rootDirAbsolutePath; }
    
    public void clear() {
        fileTree.getTree().clearSelection();
        lastSelected = null;
    }

    // ----------- INTERNAL MEMBERS ---------------------------------------

    private void createFileSystemTreePanel(String rootDirAbsolutePath) {
        this.rootDirAbsolutePath = rootDirAbsolutePath;
        fileTree = new FileSystemTreePanel(rootDirAbsolutePath);
        fileTree.getTree().getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        fileTree.getTree().addTreeSelectionListener(new TreeListener());
        fileTree.getTree().addMouseListener(new MouseAdapter() {
            @Override  
            public void mouseClicked(MouseEvent e) {  
                int row = fileTree.getTree().getRowForLocation(e.getX(),e.getY());  
                if (row == -1) {
                    fileTree.getTree().clearSelection();  
                }
            }  
        } );
    }
    
    protected class TreeListener implements TreeSelectionListener {
        public void valueChanged(TreeSelectionEvent e) {
            File fileSysEntity = (File)e.getPath().getLastPathComponent();
            
            if (fileSysEntity == null) {
                return;
            }
            
            if (!fileSysEntity.isDirectory()) {
                if (lastSelected == fileSysEntity) {
                    //don't reopen a file we already have open
                } else {
                    opener.openFile(fileSysEntity.getAbsolutePath());
                    lastSelected = (File)e.getPath().getLastPathComponent();
                }
            }
        }                
    }

    private IFileOpener         opener;
    private FileSystemTreePanel fileTree;
    private String              rootDirAbsolutePath;
    private File                lastSelected;
}

