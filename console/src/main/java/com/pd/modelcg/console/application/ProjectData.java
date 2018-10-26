package com.pd.modelcg.console.application;

import com.pd.modelcg.console.docmanager.DocumentManager;
import com.pd.modelcg.console.docmanager.IDocumentManager;
import com.pd.modelcg.codegen.FileUtility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

public class ProjectData {

    private static String mostRecentProject;

    public static ProjectData openProject(String projectFileName) {
        String projectDesc;
        try {
            projectDesc = FileUtility.readFile(projectFileName);
            mostRecentProject = projectFileName;
        } catch (java.io.IOException ex) {
            ErrorDialog.error("Unable to open project: " + projectFileName);
            return null;
        }

        String[] projectData = projectDesc.split("\n");
        //project data should have 6 elements: 
        //  name, projDir, modelFN, templateFN, generatedFN, genTargetDir
        if (projectData.length != 6) {
            ErrorDialog.error("Invalid project file: " + projectFileName);
            return null;
        }

        return new ProjectData(projectData[0], projectData[1], projectData[2], 
                projectData[3], projectData[4], projectData[5]);
    }

    public ProjectData(String name, String directory, String modelFN, String templateFN, String generatedFN, String genTargetDir) {
        this.projectName = name;
        this.projectDirectory = directory;
        this.lastReadModelFN = modelFN;
        this.lastReadTemplateFN = templateFN;
        this.lastReadGeneratedFN = generatedFN;		
	    this.generatorTargetDirectory = genTargetDir;
    }
    public String getName() {
        return this.projectName;
    }
    public String getDirectory() {
        return this.projectDirectory;
    }
    public String getGeneratorTargetDirectory() {
        return this.generatorTargetDirectory;
    }
    public String getLastReadModelFileName() {
        return this.lastReadModelFN;
    }
    public String getLastReadTemplateFileName() {
        return this.lastReadTemplateFN;
    }
    public String getLastReadGeneratedFileName() {
        return this.lastReadGeneratedFN;
    }    
    public void saveProject(IDocumentManager docMgr) {
        //THIS METHOD IS INCOMPLETE. FIX IT AND USE IT.
        String projectData = projectDirectory + "," + 
                "," + docMgr.getViewerByName(DocumentManager.VIEW_NAME_GRAPHICAL_MODEL).getShortFileName() +
                "," + docMgr.getViewerByName(DocumentManager.VIEW_NAME_TEMPLATES).getShortFileName() +
                "," + docMgr.getViewerByName(DocumentManager.VIEW_NAME_GENERATED_CODE).getShortFileName() +
				"," + generatorTargetDirectory;

        try {
            FileUtility.writeFile(mostRecentProject, projectDirectory + "\\" + projectName + "\\" + projectName + ".csv");
        } catch (java.io.IOException ex) {
            ErrorDialog.getInstance().error("Unable to open file: " + mostRecentProject, ex);
        }
    }

    private String              projectName;
    private String              projectDirectory;
    private String              lastReadScaffoldFN;
    private String              lastReadModelFN;
    private String              lastReadTemplateFN;
    private String              lastReadGeneratedFN;    
    private String              generatorTargetDirectory;	
}
