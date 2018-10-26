package com.pd.modelcg.console.docviewer;

import com.pd.modelcg.console.fileexplorer.IFileOpener;

import javax.swing.JComponent;
import java.awt.Dimension;

public interface IDocumentViewer extends IFileOpener {
    void setPreferredSize(Dimension d);
    JComponent getComponent();

    void clear();
    boolean hasChanged();
    String getDocumentTitle();
    String getFileNameAbsolutePath();
    String getDirectoryAbsolutePath();
    String getShortFileName();
    
    void newFile(String fileNameAbsolutePath);
    void openFile(String fileNameAbsolutePath);
    void saveAsFile(String fileNameAbsolutePath);
    void saveFile();
}
