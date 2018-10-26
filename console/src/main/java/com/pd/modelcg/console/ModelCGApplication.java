package com.pd.modelcg.console;

import com.pd.modelcg.console.application.Console;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;

public class ModelCGApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context =
                new SpringApplicationBuilder(Console.class)
                        .web(WebApplicationType.NONE)
                        .headless(false)
                        .run(args);
        run(context.getBean(Console.class));
    }

    private static void run(Console console) {
        JFrame.setDefaultLookAndFeelDecorated(false);

        //Schedule a job for the event dispatching thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(() -> {
            try {
                //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            //Turn off metal's use of bold fonts
            UIManager.put("swing.boldMetal", Boolean.FALSE);
            createAndShowGUI(console);
        });
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    private static void createAndShowGUI(JFrame frame) {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
