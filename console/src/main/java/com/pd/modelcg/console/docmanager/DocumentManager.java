package com.pd.modelcg.console.docmanager;

import com.pd.modelcg.console.fileexplorer.FileExplorer;
import com.pd.modelcg.console.fileexplorer.IFileOpener;
import com.pd.modelcg.console.docviewer.IDocumentViewer;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.HashMap;
import java.util.Map;

public class DocumentManager implements IDocumentManager, IFileOpener
{
    public DocumentManager(ITitleIndicator titleIndicator, JFrame docFrame)
    {
        this.titleIndicator = titleIndicator;
        this.docFrame = docFrame;
        this.tabbedPane = new JTabbedPane();
        this.explorers = new HashMap<String, FileExplorer>();
        this.viewers = new HashMap<String, IDocumentViewer>();
    }

    // call this AFTER setting the explorers and viewers
    public void initialize() {
        //set initial view
        explorerPane = new JScrollPane();
        explorerPane.setViewportView(getExplorerByName(VIEW_NAME_GRAPHICAL_MODEL).getComponent());
        currentViewer = getViewerByName(VIEW_NAME_GRAPHICAL_MODEL);

        //add change listener to detect when the selected tab changes
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JTabbedPane tp = (JTabbedPane)e.getSource();

                int index = tp.getSelectedIndex();
                switch (index) {
                    case 0: //Start
                        currentViewer = getViewerByName(VIEW_NAME_START);
                        break;
                    case 1: //Graphical Model
                        currentViewer = getViewerByName(VIEW_NAME_GRAPHICAL_MODEL);
                        explorerPane.setViewportView(getExplorerByName(VIEW_NAME_GRAPHICAL_MODEL).getComponent());
                        titleIndicator.setDocumentTitle(currentViewer.getDocumentTitle());
                        break;
                    case 2: //Templates
                        currentViewer = getViewerByName(VIEW_NAME_TEMPLATES);
                        explorerPane.setViewportView(getExplorerByName(VIEW_NAME_TEMPLATES).getComponent());
                        titleIndicator.setDocumentTitle(currentViewer.getDocumentTitle());
                        break;
                    case 3: //Generated Code
                        currentViewer = getViewerByName(VIEW_NAME_GENERATED_CODE);
                        explorerPane.setViewportView(getExplorerByName(VIEW_NAME_GENERATED_CODE).getComponent());
                        titleIndicator.setDocumentTitle(currentViewer.getDocumentTitle());
                        break;
                    default: //5: Logs
                        currentViewer = getViewerByName(VIEW_NAME_LOGS);
                }
            }
        });
    }

    @Override
    public JComponent getExplorerComponent()        { return this.explorerPane; }

    @Override
    public JComponent getTabbedDocumentComponent()  { return this.tabbedPane; }

    @Override
    public void setCurrentViewerByName(String name) {
        IDocumentViewer viewer = viewers.get(name);
        tabbedPane.setSelectedComponent(viewer.getComponent());
    }
    @Override
    public IDocumentViewer getCurrentViewer() {
        return this.currentViewer;
    }

    @Override
    public FileExplorer addExplorerByName(String name, FileExplorer explorer) {
        this.explorers.put(name, explorer);
        return explorer;
    }
    @Override
    public FileExplorer getExplorerByName(String name) {
        return this.explorers.get(name);
    }

    @Override
    public IDocumentViewer addViewerByName(String name, IDocumentViewer viewer) {
        this.viewers.put(name, viewer);
        tabbedPane.add(name, viewer.getComponent());
        return viewer;
    }
    @Override
    public IDocumentViewer getViewerByName(String name) {
        return this.viewers.get(name);
    }
                          /*
    @Override
    public void openMostRecentFiles() {
        String fileName = project.getLastReadModelFileName();
        if (fileName.contains("<"))
            modelViewer.newFile(DocumentViewer.DEFAULT_NEW_FILENAME);
        else
            modelViewer.openFile(fileName);

        fileName = project.getLastReadTemplateFileName();
        if (fileName.contains("<"))
            templateViewer.newFile("<New File>");
        else
            templateViewer.openFile(fileName);

        fileName = project.getLastReadGeneratedFileName();
        if (fileName.contains("<"))
            generatedViewer.newFile("<New File>");
        else
            generatedViewer.openFile(fileName);
    }
    */

    @Override
    public void refreshDirectories() {
        //HACK to refresh directories
       // templateViewer = new TemplateViewer(templateFilePath);
        //generatedFileExplorer = new FileExplorer(this, generatedFilePath);
    }

    @Override
    public void saveAllChanges(boolean confirm) {
        for (IDocumentViewer viewer: this.viewers.values()) {
            saveDocument(viewer, confirm);
        }
    }
    private void saveDocument(IDocumentViewer doc, boolean confirm) {
        if (doc.hasChanged() || doc.getShortFileName().startsWith("<")) {
            boolean saveFile = false;
            if (confirm) {
                int n = JOptionPane.showConfirmDialog(docFrame, "The file " +
                        doc.getShortFileName() + " has been modified. Save?",
                        "Save Confirmation", JOptionPane.YES_NO_OPTION);
                if (JOptionPane.YES_OPTION == n)
                    saveFile = true;
            }
            if (saveFile) {
                doc.saveFile();
            }
        }
    }
    
    @Override
    public void newFile(String fileName) {
        currentViewer.newFile(fileName);
        titleIndicator.setDocumentTitle(currentViewer.getDocumentTitle());
        currentViewer.clear();
    }

    @Override
    public void openFile(String fileNameAbsolutePath) {
        currentViewer.openFile(fileNameAbsolutePath);
        titleIndicator.setDocumentTitle(currentViewer.getDocumentTitle());
    }

    @Override
    public void saveFileAs(String newFileName) {
        currentViewer.saveAsFile(newFileName);
        titleIndicator.setDocumentTitle(currentViewer.getDocumentTitle());
    }

    @Override
    public void saveFile() {
        currentViewer.saveFile();
    }

    private ITitleIndicator                 titleIndicator;
    private JFrame                          docFrame;
    private JTabbedPane                     tabbedPane;
    private JScrollPane                     explorerPane;
    private IDocumentViewer                 currentViewer;
    private Map<String, FileExplorer>       explorers;
    private Map<String, IDocumentViewer>    viewers;
}
