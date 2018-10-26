package com.pd.modelcg.console.docviewer;

import javax.swing.JComponent;
import java.awt.Dimension;

abstract public class DocumentViewer implements IDocumentViewer {
    public static final String DEFAULT_NEW_FILENAME = "<New File>";
    
    public DocumentViewer(String directoryAbsolutePath) {
        this.hasChanged = false;
        this.fileNameAbsolutePath = DEFAULT_NEW_FILENAME;
        this.directoryAbsolutePath = directoryAbsolutePath;
    }
    
    public void reset() {
        this.hasChanged = false;
        this.fileNameAbsolutePath = DEFAULT_NEW_FILENAME;
    }

    abstract public void setPreferredSize(Dimension d);

    abstract public JComponent getComponent();

    public boolean hasChanged() {
        return this.hasChanged;
    }

    public String getDocumentTitle() {
        return (this.hasChanged) ? this.getShortFileName() + " *" : this.getShortFileName();
    }

    public String getShortFileName() {
        return (this.fileNameAbsolutePath.contains(".") ?
                    this.fileNameAbsolutePath.substring(this.fileNameAbsolutePath.lastIndexOf('.') + 1)
                    : this.fileNameAbsolutePath);
    }

    public String getFileNameAbsolutePath() {
        return this.fileNameAbsolutePath;
    }

    public String getDirectoryAbsolutePath() {
        return this.directoryAbsolutePath;
    }

    public void newFile(String fileNameAbsolutePath) {
        doNewFile(fileNameAbsolutePath);
        setFileName(fileNameAbsolutePath); //change fileName AFTER successful new
    }

    public void openFile(String fileNameAbsolutePath) {
        doOpenFile(fileNameAbsolutePath);
        setFileName(fileNameAbsolutePath); //change fileName AFTER successful open
    }
    public void saveAsFile(String fileNameAbsolutePath) {
        doSaveAsFile(fileNameAbsolutePath);
        setFileName(fileNameAbsolutePath); //change fileName AFTER successful save
        this.hasChanged = false;
    }
    public void saveFile() {
        saveAsFile(fileNameAbsolutePath);
    }

    abstract public void doNewFile(String fileNameAbsolutePath);
    abstract public void doOpenFile(String fileNameAbsolutePath);
    abstract public void doSaveAsFile(String fileNameAbsolutePath);

    protected void setFileName(String fileNameAbsolutePath) {
        this.fileNameAbsolutePath = fileNameAbsolutePath;
    }

    protected boolean       hasChanged;

    private String          fileNameAbsolutePath;
    private String          directoryAbsolutePath;
}
