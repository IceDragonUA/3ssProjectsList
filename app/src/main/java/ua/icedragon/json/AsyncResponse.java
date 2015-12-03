package ua.icedragon.json;

public interface AsyncResponse {
    void processFinish(String[] projectName,
                       String[] projectImageUrl,
                       String[] projectDescription,
                       String[] projectTechnologies,
                       String[] projectSupportedScreens,
                       String[] projectSolutionTypes,
                       String[] clientName,
                       String[] projectAssetsUrl,
                       int projectsLength);
}
