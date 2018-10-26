package com.pd.modelcg.console.docviewer;

import com.pd.modelcg.codegen.FileUtility;
import com.pd.modelcg.console.application.ErrorDialog;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.io.IOException;

public class TextDocumentViewer extends DocumentViewer {
    public TextDocumentViewer(String directoryAbsolutePath) {
        super(directoryAbsolutePath);

        this.area = new JTextPane();//JavaEditorPane();
        this.scrollPane = new JScrollPane(area);
        this.writer = new TextDocumentPrintWriter(this, area);
    }

    public void clear() {
        this.setText("");
    }

    @Override
    public void setPreferredSize(Dimension d) {
        scrollPane.setPreferredSize(d);
    }
    
    @Override
    public JComponent getComponent() {
        return scrollPane;
    }
    
    @Override
    public void doNewFile(String fileName) {
        setText("");
    }
    @Override
    public void doOpenFile(String fileName) {
        try {
            setText(FileUtility.readFile(fileName));
        } catch (IOException ex) {
            ErrorDialog.getInstance().error("Unable to open file: " + fileName, ex);
        }
    }
    @Override
    public void doSaveAsFile(String fileName) {
        try {
            FileUtility.writeFile(fileName, getText());
        } catch (IOException ex) {
            ErrorDialog.getInstance().error("Unable to write to file: " + fileName, ex);
        }
    }

    //*** text specific methods

    public void setEditable(boolean e) {
        area.setEditable(e);
    }
    public void setText(String s){
        area.setText(s);
    }
    public String getText() {
        return area.getText();
    }
    public PrintWriter getPrintWriter() {
        return writer;
    }

    //package level access; used by TextDocumentPrintWriter
    void hasChanged(boolean hasChanged) {
        this.hasChanged = hasChanged;
    }

    private TextDocumentPrintWriter     writer;
    private JScrollPane                 scrollPane;
    private JEditorPane                 area;
}
