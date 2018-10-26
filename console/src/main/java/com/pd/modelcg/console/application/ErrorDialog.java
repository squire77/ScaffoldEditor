package com.pd.modelcg.console.application;

import com.pd.modelcg.console.docviewer.TextDocumentViewer;

import javax.swing.*;

public class ErrorDialog {

    private static ErrorDialog instance = null;
    private TextDocumentViewer logs;

    public static ErrorDialog getInstance() {
        if (null == instance)
            instance = new ErrorDialog();
        return instance;
    }

    private ErrorDialog() {
        this.logs = new TextDocumentViewer(null);
    }

    TextDocumentViewer getLogViewer() {
        return logs;
    }

    public static void error(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void error(String message, String detail) {
        logs.setText("");
        if (message.length() < 500)
            JOptionPane.showMessageDialog(null, message, "Scaffold Error", JOptionPane.ERROR_MESSAGE);
        logs.getPrintWriter().println(detail);
    }

    public void error(String message, Exception ex) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
        logs.setText("");
        ex.printStackTrace(logs.getPrintWriter());
    }
}
