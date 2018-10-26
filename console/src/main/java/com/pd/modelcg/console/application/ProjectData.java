package com.pd.modelcg.console.application;

import com.pd.modelcg.codegen.FileUtility;

public class ProjectData {

    private String              projectName;
    private String              projectDirectory;
    private String              generatorTargetDirectory;

    static ProjectData openProject(String projectFileName) {

        String projectDesc;

        try {
            projectDesc = FileUtility.readFile(projectFileName);
        } catch (java.io.IOException ex) {
            ErrorDialog.error("Unable to open project: " + projectFileName);
            return null;
        }

        String[] projectData = projectDesc.split("\n");

        //project data should have 6 elements: name, projDir, genTargetDir
        if (projectData.length != 3) {
            ErrorDialog.error("Invalid project file: " + projectFileName);
            return null;
        }

        return new ProjectData(projectData[0], projectData[1], projectData[2]);
    }

    private ProjectData(String name, String directory, String genTargetDir) {
        this.projectName = name;
        this.projectDirectory = directory;
	    this.generatorTargetDirectory = genTargetDir;
    }

    public String getName() {
        return this.projectName;
    }
    String getDirectory() {
        return this.projectDirectory;
    }
    String getGeneratorTargetDirectory() {
        return this.generatorTargetDirectory;
    }
}
