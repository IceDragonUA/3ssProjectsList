package ua.icedragon.json;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AsyncResponse {

    private Intent intent;
    private ListView listView;
    private ImageLoader imageLoader;

    private String[] clientNameExt;
    private String[] projectNameExt;
    private String[] projectImageUrlExt;
    private String[] projectAssetsUrlExt;
    private String[] projectDescriptionExt;
    private String[] projectTechnologiesExt;
    private String[] projectSolutionTypesExt;
    private String[] projectSupportedScreensExt;

    private String[] url = {
            "http://91.250.82.77:8081/3ssdemo/prj/json/projects.php",
            "http://91.250.82.77:8081/3ssdemo/prj/json/clients.php",
    };

    private void initializeImageLoaderConfig() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                this)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(350 * 1024 * 1024).build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ParseTask parseTask = new ParseTask(this, this);
        parseTask.info = this;
        parseTask.execute(url);

        initializeImageLoaderConfig();

        intent = new Intent(MainActivity.this, InfoActivity.class);

        listView = (ListView) findViewById(R.id.list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> av, View view, int i, long l) {

                intent.putExtra("client name", clientNameExt[i]);
                intent.putExtra("project name", projectNameExt[i]);
                intent.putExtra("project image url", projectImageUrlExt[i]);
                intent.putExtra("project assets url", projectAssetsUrlExt[i]);
                intent.putExtra("project description", projectDescriptionExt[i]);
                intent.putExtra("project technologies", projectTechnologiesExt[i]);
                intent.putExtra("project solution types", projectSolutionTypesExt[i]);
                intent.putExtra("project supported screens", projectSupportedScreensExt[i]);

                startActivity(intent);
            }
        });
    }

    public void processFinish(String[] projectName,
                              String[] projectImageUrl,
                              String[] projectDescription,
                              String[] projectTechnologies,
                              String[] projectSupportedScreens,
                              String[] projectSolutionTypes,
                              String[] clientName,
                              String[] projectAssetsUrl,
                              int projectsLength){

        clientNameExt = new String[projectsLength];
        projectNameExt = new String[projectsLength];
        projectImageUrlExt = new String[projectsLength];
        projectAssetsUrlExt = new String[projectsLength];
        projectDescriptionExt = new String[projectsLength];
        projectTechnologiesExt = new String[projectsLength];
        projectSolutionTypesExt = new String[projectsLength];
        projectSupportedScreensExt = new String[projectsLength];

        clientNameExt = clientName;
        projectNameExt = projectName;
        projectImageUrlExt = projectImageUrl;
        projectAssetsUrlExt = projectAssetsUrl;
        projectDescriptionExt = projectDescription;
        projectTechnologiesExt = projectTechnologies;
        projectSolutionTypesExt = projectSolutionTypes;
        projectSupportedScreensExt = projectSupportedScreens;

        ArrayList<Map<String, Object>> data = new ArrayList<>(projectName.length);
        Map<String, Object> m;
        String ATTRIBUTE_PROJECT_NAME = "name";
        String ATTRIBUTE_PROJECT_IMAGE = "url";
        for (int i = 0; i < projectName.length; i++) {
            m = new HashMap<>();
            m.put(ATTRIBUTE_PROJECT_NAME, projectName[i]);
            m.put(ATTRIBUTE_PROJECT_IMAGE, projectImageUrl[i]);
            data.add(m);
        }

        String[] from = {ATTRIBUTE_PROJECT_NAME, ATTRIBUTE_PROJECT_IMAGE};
        int[] to = {R.id.project_name, R.id.project_logo};

        SimpleAdapter sAdapter = new SimpleAdapter(
                this,
                data,
                R.layout.item,
                from,
                to);

        sAdapter.setViewBinder(new MyViewBinder());

        listView.setAdapter(sAdapter);
    }

    class MyViewBinder implements SimpleAdapter.ViewBinder {

        @Override
        public boolean setViewValue(View view, Object data,
                                    String textRepresentation) {
            if (view instanceof ImageView) {
                switch (view.getId()) {
                    case R.id.project_logo:
                        ImageView logo = (ImageView) view;
                        imageLoader.displayImage(textRepresentation, logo);
                        return true;
                }
            }
            return false;
        }
    }
}
