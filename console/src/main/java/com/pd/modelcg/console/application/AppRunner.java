package com.pd.modelcg.console.application;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.swing.JFrame;
import java.awt.*;

@Component
public class AppRunner implements CommandLineRunner {

    @Override
    public void run(String... arg0) {
        EventQueue.invokeLater(() -> {
            try {
                Console console = new Console();
                console.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                console.pack();
                console.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}

