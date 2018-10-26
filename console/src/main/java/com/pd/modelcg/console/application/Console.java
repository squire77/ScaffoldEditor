package com.pd.modelcg.console.application;

import com.pd.modelcg.codegen.CmGenerator;
import com.pd.modelcg.codegen.model.uml.ClassModel;
import com.pd.modelcg.console.docmanager.DocumentManager;
import com.pd.modelcg.console.docmanager.IDocumentManager;
import com.pd.modelcg.console.docmanager.ITitleIndicator;
import com.pd.modelcg.console.docviewer.DocumentViewer;
import com.pd.modelcg.console.docviewer.ModelDocumentViewer;
import com.pd.modelcg.console.docviewer.TemplateViewer;
import com.pd.modelcg.console.docviewer.TextDocumentViewer;
import com.pd.modelcg.console.fileexplorer.FileExplorer;
import com.pd.modelcg.codegen.FileUtility;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import javax.swing.border.EtchedBorder;

//
//           |--> title
// Console <>---> docMgr <>---------------> tabbedPane <>---------> GraphCanvas
//           |                         |                                |
//           |--> STATUS_LABEL         |--> explorerPane                |
//                                              modelExplorer ----------|
//                                              templateExplorer
//                                              generatedExplorer
//
@Component
@ComponentScan(basePackages = "com.pd.modelcg.console")
public class Console extends JFrame implements ITitleIndicator {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(Console.class);

    private final ConsoleConfig consoleConfig;

    public Console(ConsoleConfig consoleConfig) {
        super(FRAME_TITLE);

        this.consoleConfig = consoleConfig;

        log.info(String.format("mostRecentProject = %s", consoleConfig.getMostRecentProject()));
        log.info(String.format("licenseFile = %s", consoleConfig.getLicenseFile()));

        validLicense = true;
        
        try {
            String licenseKey = FileUtility.readFile(consoleConfig.getLicenseFile());
            if (LicenseValidator.isValidLicenseKey(licenseKey)) {
                validLicense = true;
            }
        } catch (IOException e) {
        }

        if (!validLicense) {
            buildDisabledFrame(DEFAULT_FRAME_SIZE_WIDTH, DEFAULT_FRAME_SIZE_HEIGHT);
            return;
        }
        
        ClassModel.initialize();
        
        //build and layout components
        buildFrame(DEFAULT_FRAME_SIZE_WIDTH, DEFAULT_FRAME_SIZE_HEIGHT);
        layoutComponents(DEFAULT_FRAME_SIZE_WIDTH, DEFAULT_FRAME_SIZE_HEIGHT);
        addComponentListener(new ComponentAdapter(){
            @Override
            public void componentResized(ComponentEvent event){
                layoutComponents(getWidth()-30, getHeight()-120);
                centerPane.revalidate();
                docMgr.getExplorerComponent().revalidate();
                docMgr.getTabbedDocumentComponent().revalidate();
                docMgr.getViewerByName(DocumentManager.VIEW_NAME_GRAPHICAL_MODEL).getComponent().revalidate();
				commandLinePanel.revalidate();
                repaint();
            }
        });

        docMgr.setCurrentViewerByName(DocumentManager.VIEW_NAME_GRAPHICAL_MODEL);
	    //docMgr.openMostRecentFiles();
        
        String rootDir = docMgr.getExplorerByName(DocumentManager.VIEW_NAME_TEMPLATES).getRootDirAbsolutePath();
        
        try {
            templateViewer.setText(FileUtility.readFile(rootDir + "/java-class.tem"));
        } catch (IOException ex) {
            ErrorDialog.getInstance().error("Problem opening template: java-class.tem", ex);
        }
    }

    public String toString() {
        return COMPANY_NAME + "\n" + PRODUCT_NAME;
    }

    public void setDocumentTitle(String title) {
        if (title.startsWith("<"))
        {
            super.setTitle(FRAME_TITLE + " - [" + title.substring(1, title.length()-1) + "]");
        }
        else
        {
            String fileName = FileUtility.extractFileName(title);
            super.setTitle(FRAME_TITLE + " - [" + fileName + "]");
        }
    }

    public IDocumentManager getDocMgr() {
        return this.docMgr;
    }
    
    private void buildDisabledFrame(int w, int h) {
        //setLocationRelativeTo(null);
        //setLocation(200, 80);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((dim.width-w)/2, (dim.height-h)/2);
        
        //create menu bar
        setJMenuBar(createDisabledMenuBar());

        //create center pane
        centerPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, null, null);
        centerPane.setContinuousLayout( true );
        getContentPane().add(centerPane, BorderLayout.NORTH);

        //initialize status bar
        STATUS_LABEL.setBorder(new EtchedBorder());
        getContentPane().add(STATUS_LABEL, BorderLayout.SOUTH); 
    }
    
    private JMenuBar createDisabledMenuBar() {
        JMenu fileMenu = new JMenu("File");
        JMenuItem exit = new JMenuItem("Exit");
        fileMenu.add(exit);
        exit.addActionListener((event) -> {
            dispose();
            System.exit(0);
        });

        JMenu modelMenu = new JMenu("Model");
        JMenu generateMenu = new JMenu("Generate");        
        JMenu helpMenu = new JMenu( "Help" );
        
        JMenu aboutMenu = new JMenu( "About" );
        JMenuItem license = new JMenuItem( "License..." );
        aboutMenu.add(license);
        
        license.addActionListener((event) -> {
            String licenseKey = JOptionPane.showInputDialog(Console.this, "Enter License Key:");

            if (LicenseValidator.isValidLicenseKey(licenseKey)) {
                validLicense = true;

                try {
                    FileUtility.writeFile(consoleConfig.getLicenseFile(), licenseKey);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(Console.this,
                            "Problem writing to license file: " + consoleConfig.getLicenseFile() + ".\n\n" +
                                    "To enable your product without the need to re-enter your license key, " +
                                    "please paste your license key into a file named \"license.txt\" and " +
                                    "place the file in the installation directory.",
                            "Problem writing license key",
                            JOptionPane.WARNING_MESSAGE);
                }

                JOptionPane.showMessageDialog(Console.this,
                        "Congratulations!\nThank you for purchasing " + PRODUCT_NAME + " by " + COMPANY_NAME + "." +
                                "\n\nPlease restart " + PRODUCT_NAME + ".",
                        "License Key Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(Console.this,
                        "Invalid license key." +
                                "\nPlease purchase a valid license from http://www.pluggabledesignsoftware.com",
                        "License Key Failure",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        // menu bar
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(modelMenu);
	    menuBar.add(generateMenu);
        menuBar.add(helpMenu);
        menuBar.add(aboutMenu);

        return menuBar;
    }
    
    private void buildFrame(int w, int h) {
        //setLocationRelativeTo(null);
        //setLocation(200, 80);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((dim.width-w)/2, (dim.height-h)/2);

        projectData = ProjectData.openProject(consoleConfig.getMostRecentProject());
        docMgr = new DocumentManager(this, this);

        //designate paths
        String modelFilePath = projectData.getDirectory() + File.separator + "models";
        String templateFilePath = projectData.getDirectory() + File.separator + "templates";
        String generatedFilePath = projectData.getGeneratorTargetDirectory();

        //create document viewers
        startViewer = (TextDocumentViewer) docMgr.addViewerByName(DocumentManager.VIEW_NAME_START, new TextDocumentViewer(null));
        graphicalModelViewer = (ModelDocumentViewer) docMgr.addViewerByName(DocumentManager.VIEW_NAME_GRAPHICAL_MODEL, new ModelDocumentViewer(modelFilePath));
        templateViewer = (TextDocumentViewer) docMgr.addViewerByName(DocumentManager.VIEW_NAME_TEMPLATES, new TemplateViewer(templateFilePath));
        generatedCodeViewer = (TextDocumentViewer) docMgr.addViewerByName(DocumentManager.VIEW_NAME_GENERATED_CODE, new TextDocumentViewer(generatedFilePath));
        logViewer = (TextDocumentViewer) docMgr.addViewerByName(DocumentManager.VIEW_NAME_LOGS, ErrorDialog.getInstance().getLogViewer());

        //create file explorers
        docMgr.addExplorerByName(DocumentManager.VIEW_NAME_GRAPHICAL_MODEL,
                new FileExplorer(graphicalModelViewer, modelFilePath));
        docMgr.addExplorerByName(DocumentManager.VIEW_NAME_TEMPLATES,
                new FileExplorer(templateViewer, templateFilePath));
        docMgr.addExplorerByName(DocumentManager.VIEW_NAME_GENERATED_CODE,
                new FileExplorer(generatedCodeViewer, generatedFilePath));

        docMgr.initialize();

        //create menu bar
        setJMenuBar(createMenuBar());

       /*
        cList = new ArrayList<String>();
        cList.add("Beate");
        cList.add("Claudia");
        cList.add("Fjodor");
        cList.add("Fred");
        cList.add("Friedrich");
        cList.add("Fritz");
        cList.add("Frodo");
        cList.add("Hermann");
        cList.add("Willi");
	cBox= new Java2sAutoComboBox(cList);
         */

        //create command line
        commandLinePanel = new JPanel();
        //autoComplete = new AutoComplete(commandField);
        //autoComplete.setItems(new String[] {"if", "while"});
        commandLinePanel.add(new JLabel("Command: ", JLabel.RIGHT)); //roughly 60 width
        commandField = new JTextField(); //new Java2sAutoTextField(cList, cBox);
        commandLinePanel.add(commandField); //roughly 520 width
        commandField.addActionListener((event) -> {
            //generate(((JTextField) event.getSource()).getText());
        });
        getContentPane().add(commandLinePanel, BorderLayout.CENTER);

        //create center pane
        centerPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
        docMgr.getExplorerComponent(), docMgr.getTabbedDocumentComponent());
        centerPane.setContinuousLayout( true );
        getContentPane().add(centerPane, BorderLayout.NORTH);

        //initialize status bar
        STATUS_LABEL.setBorder(new EtchedBorder());
        getContentPane().add(STATUS_LABEL, BorderLayout.SOUTH);       
    }

    private void layoutComponents(int width, int height) {
        docMgr.getTabbedDocumentComponent().setPreferredSize(new Dimension(width, height));

        //resize fileExplorerPane
        Dimension dim = new Dimension (200, height-10);
        docMgr.getExplorerComponent().setPreferredSize(dim);

        //inset docs within tabbedPane
        dim = new Dimension (width-20, height-10);
        logViewer.setPreferredSize(dim);
        templateViewer.setPreferredSize(dim);
        generatedCodeViewer.setPreferredSize(dim);
        startViewer.setPreferredSize(dim);

        //update graphical view component
        graphicalModelViewer.getComponent().setPreferredSize(dim);

        //update command field
        commandField.setPreferredSize(new Dimension(width - 80, 30));
    }

    private JMenuBar createMenuBar() {
        //"File" menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem openProject = new JMenuItem("Open Project");
        fileMenu.add(openProject);
        JMenuItem saveProject = new JMenuItem("Save Project");
        fileMenu.add(saveProject);
        fileMenu.insertSeparator(2);
        JMenuItem newFile = new JMenuItem("New");
        fileMenu.add(newFile);
        JMenuItem openFile = new JMenuItem("Open");
        fileMenu.add(openFile);
        JMenuItem saveFile = new JMenuItem("Save");
        fileMenu.add(saveFile);
        JMenuItem saveFileAs = new JMenuItem("Save As...");
        fileMenu.add(saveFileAs);
        fileMenu.insertSeparator(7);
        JMenuItem exit = new JMenuItem("Exit");
        fileMenu.add(exit);
        newFile.addActionListener((event) -> {
            docMgr.newFile(DocumentViewer.DEFAULT_NEW_FILENAME);
        });
        openFile.addActionListener((event) -> {
            FC.setCurrentDirectory(new File(docMgr.getCurrentViewer().getDirectoryAbsolutePath()));
            int returnVal = FC.showOpenDialog(Console.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = FC.getSelectedFile();

                if (file != null)
                    docMgr.openFile(file.getAbsolutePath());
            }
        });
        saveFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                docMgr.saveFile();
            }
        } );
        saveFileAs.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                FC.setCurrentDirectory(new File(docMgr.getCurrentViewer().getDirectoryAbsolutePath()));
                int returnVal = FC.showSaveDialog(Console.this);

                if (returnVal == JFileChooser.APPROVE_OPTION){
                    File file = FC.getSelectedFile();

                    if (file != null) {
                        docMgr.saveFileAs(file.getAbsolutePath());
                        
                        //update file explorers to show new files got added
                        docMgr.refreshDirectories();
                    }
                }
            }
        } );
        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                docMgr.saveAllChanges(true);
                dispose();
                System.exit(0);
            }
        } );

        //"Model" menu
        JMenu modelMenu = new JMenu("Model");
        JMenuItem addClass = new JMenuItem("Add Class");
        modelMenu.add(addClass);
        addClass.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                graphicalModelViewer.getUMLCanvas().addUMLClass(false, 200, 400);
            }
        } );
        JMenuItem addInterface = new JMenuItem("Add Interface");
        modelMenu.add(addInterface);
        addInterface.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                graphicalModelViewer.getUMLCanvas().addUMLClass(true, 200, 400);
            }
        } );
				
        //"Generate" menu
        JMenu generateMenu = new JMenu("Generate");
        JMenuItem generateCode = new JMenuItem("Generate Code");
        generateMenu.add(generateCode);
        generateCode.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                generate();
                
                //update file explorers in case new files got added
                docMgr.refreshDirectories();
            }
        } );

        //"Help" menu
        JMenu helpMenu = new JMenu( "Help" );
        JMenuItem clicheHelp = new JMenuItem( "Cliche Overview" );
        JMenuItem classHelp = new JMenuItem( "Class Model Usage" );
        JMenuItem projectHelp = new JMenuItem( "Project Model Usage" );
        helpMenu.add(clicheHelp);
        helpMenu.add(classHelp);
        helpMenu.add(projectHelp);
        clicheHelp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                startViewer.openFile("C:/GitHub/ScaffoldEditor/console/src/main/resources/data/help/cliche_help.txt");
            }
        } );
        classHelp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                startViewer.openFile("C:\\GitHub\\ScaffoldEditor\\console\\src\\main\\resources/data/help/class_help.txt");
            }
        } );
        projectHelp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                startViewer.openFile("C:\\GitHub\\ScaffoldEditor\\console\\src\\main\\resources/data/help/project_help.txt");
            }
        } );
        
        //"About" menu
        JMenu aboutMenu = new JMenu( "About" );
        JMenuItem license = new JMenuItem( "License..." );
        aboutMenu.add(license);
        license.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {  
                String licenseKey = "";
                
                try {
                    licenseKey = FileUtility.readFile(consoleConfig.getLicenseFile());
                    
                    JOptionPane.showMessageDialog(Console.this,
                        PRODUCT_NAME + " by " + COMPANY_NAME + "\n\n" + licenseKey,
                        "License Key",                        
                        JOptionPane.INFORMATION_MESSAGE);
                    
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(Console.this,                            
                        "Problem reading license key from " + consoleConfig.getLicenseFile(),
                        "License File Error",
                        JOptionPane.ERROR_MESSAGE);
                }                                        
            }
        } );        
        
        //set default start text
        startViewer.openFile("C:/GitHub/ScaffoldEditor/console/src/main/resources/data/help/class_help.txt");
  
        // menu bar
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(modelMenu);
	    menuBar.add(generateMenu);
        menuBar.add(helpMenu);
        menuBar.add(aboutMenu);

        return menuBar;
    }

    private static void setStatusText(String s) {
        STATUS_LABEL.setText(s);
    }

    private void generate() {	
        generatedCodeViewer.setText("");

		/*

		"p.writeAll( \"" + scaffoldProject.getGeneratorTargetDirectory() + "\" )"

        //Run language extensions
        if (!scriptConn.runScript("src/metamodel/LangExt.groovy")) {
            return;
        }
		
        //Update the model
        String plugins;
        try {
            plugins = FileUtility.readFile("data/config/plugins.txt"); // header files
        } catch (IOException ex) {
            ErrorDialog.getInstance().error("Unable to read file: data/config/plugins.txt", ex);
            return;
        }
        try {
            FileUtility.writeFile("tempModel.groovy", plugins + docMgr.getScaffoldViewer().getText() + '\n' + command);
        } catch (IOException ex) {
            ErrorDialog.getInstance().error("Unable to read file: tempModel.groovy", ex);
            return;
        }

        //Run the model
        if (!scriptConn.runScript("tempModel.groovy")) {
            return;
        }

        //Save interactive command
        if (saveCommand && !command.equals("")) {
            docMgr.getGeneratedViewer().setText(docMgr.getGeneratedViewer().getText() + "\n" + command);
            commandField.setText( "" );
        }
		*/
		
        //Update the model
        String model;        
        try {                                 
            model = CmGenerator.generate(projectData.getGeneratorTargetDirectory(), templateViewer.getText());
        } catch (Exception ex) {
            ErrorDialog.getInstance().error("Problem generating code.", ex);
            return;
        }

        generatedCodeViewer.setText(model);
    }
    
    private static final String         COMPANY_NAME = "Pluggable Design Software";
    private static final String         PRODUCT_NAME = Character.toString((char) 169) + " UML Simple CodeGen Assistant";
    
    private static final String         FRAME_TITLE = "Scaffold Editor";
    private static final int            DEFAULT_FRAME_SIZE_WIDTH = 800;
    private static final int            DEFAULT_FRAME_SIZE_HEIGHT = 690;    
    private static final JLabel         STATUS_LABEL = new JLabel();
    private static final JFileChooser   FC = new JFileChooser();

    private TextDocumentViewer startViewer;
    private ModelDocumentViewer graphicalModelViewer;
    private TextDocumentViewer templateViewer;
    private TextDocumentViewer generatedCodeViewer;
    private TextDocumentViewer logViewer;

    private ProjectData         projectData;    
	private ScriptConnector     scriptConn = new ScriptConnector();
    private IDocumentManager    docMgr;
    private JSplitPane          centerPane;
    private boolean             validLicense;
	private JPanel              commandLinePanel;
    private JTextField          commandField;
}
