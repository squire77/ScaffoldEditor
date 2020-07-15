package com.pd.modelcg.console.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConsoleConfig {

    private final String mostRecentProject;
    private final String licenseFile;

    public ConsoleConfig(
            @Value("${project.recent:C:/home/pi/dev/ScaffoldEditor/projects/MyProject/MyProject.csv}")
            String mostRecentProject,
            @Value("${license.file:C:/home/pi/dev/ScaffoldEditor/projects/license.txt}")
            String licenseFile) {

        this.mostRecentProject = mostRecentProject;
        this.licenseFile = licenseFile;
    }

    String getMostRecentProject() {
        return mostRecentProject;
    }

    String getLicenseFile() {
        return licenseFile;
    }
}
