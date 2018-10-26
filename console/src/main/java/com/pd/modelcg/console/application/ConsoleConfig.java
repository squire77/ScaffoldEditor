package com.pd.modelcg.console.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConsoleConfig {

    private final String mostRecentProject;
    private final String licenseFile;


    public ConsoleConfig(
            @Value("${project.recent:C:/GitHub/ScaffoldEditor/projects/MyProject/MyProject.csv}")
            String mostRecentProject,
            @Value("${license.file:C:/GitHub/ScaffoldEditor/projects/license.txt}")
            String licenseFile) {

        this.mostRecentProject = mostRecentProject;
        this.licenseFile = licenseFile;
    }

    public String getMostRecentProject() {
        return mostRecentProject;
    }

    public String getLicenseFile() {
        return licenseFile;
    }
}
