package ua.icedragon.json;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class InfoActivity extends AppCompatActivity {

    public static String LOG_TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);

        Intent intent = getIntent();

        String clientName = intent.getStringExtra("client name");
        String projectName = intent.getStringExtra("project name");
        String projectImageUrl = intent.getStringExtra("project image url");
        String projectAssetsUrl = intent.getStringExtra("project assets url");
        String projectDescription = intent.getStringExtra("project description");
        String projectTechnologies = intent.getStringExtra("project technologies");
        String projectSolutionTypes = intent.getStringExtra("project solution types");
        String projectSupportedScreens = intent.getStringExtra("project supported screens");


        Log.d(LOG_TAG, "name: " + projectName);
        Log.d(LOG_TAG, "image: " + projectImageUrl);
        Log.d(LOG_TAG, "description: " + projectDescription);
        Log.d(LOG_TAG, "technologies: " + projectTechnologies);
        Log.d(LOG_TAG, "supportedScreens: " + projectSupportedScreens);
        Log.d(LOG_TAG, "solutionTypes: " + projectSolutionTypes);
        Log.d(LOG_TAG, "clientName: " + clientName);
        Log.d(LOG_TAG, "projectAssetsUrl: " + projectAssetsUrl);
        Log.d(LOG_TAG, "______________________________________________________");

    }
}
