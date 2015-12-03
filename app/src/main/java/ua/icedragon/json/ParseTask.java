package ua.icedragon.json;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class ParseTask extends AsyncTask<String, String, JSONObject[]> {

    public static String LOG_TAG = "myLogs";

    private final int CLIENTS = 1;
    private final int PROJECTS = 0;

    String[] clientName;
    String[] projectName;
    String[] projectImageURL;
    String[] projectAssetsUrl;
    String[] projectDescription;
    String[] projectTechnologies;
    String[] projectSolutionTypes;
    String[] projectSupportedScreens;

    ListView listView;
    TextView tvProjectName;
    ImageView tvProjectLogo;
    ImageLoader imageLoader;
    ProgressDialog progressDialog;

    Bundle bundle;

    public Context context;
    public Activity activity;

    public ParseTask(Context _cxt, Activity _activity) {
        this.activity = _activity;
        context = _cxt;
        progressDialog = new ProgressDialog(context);
    }

    private void initializeImageLoaderConfig() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(300 * 1024 * 1024).build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        listView = (ListView) activity.findViewById(R.id.list);
        tvProjectName = (TextView) activity.findViewById(R.id.project_name);
        tvProjectLogo = (ImageView) activity.findViewById(R.id.project_logo);
        initializeImageLoaderConfig();
        progressDialog.setMessage("Getting Data ...");
        progressDialog.setIndeterminate(false);
        progressDialog.show();
    }

    @Override
    protected JSONObject[] doInBackground(String... urls) {
        int JSON_PAGES_COUNT = 2;
        JSONParser jParser = new JSONParser();
        JSONObject[] jsonObjects = new JSONObject[JSON_PAGES_COUNT];
        jsonObjects[PROJECTS] = jParser.getJSONFromUrl(urls[PROJECTS]);
        jsonObjects[CLIENTS] = jParser.getJSONFromUrl(urls[CLIENTS]);
        return jsonObjects;
    }

    @Override
    protected void onPostExecute(JSONObject[] jsonObjects) {
        progressDialog.dismiss();
        try {
            JSONArray projects = jsonObjects[PROJECTS].getJSONArray("projects");
            JSONArray clients = jsonObjects[CLIENTS].getJSONArray("clients");

            clientName = new String[projects.length()];
            projectName = new String[projects.length()];
            projectImageURL = new String[projects.length()];
            projectAssetsUrl = new String[projects.length()];
            projectDescription = new String[projects.length()];
            projectTechnologies = new String[projects.length()];
            projectSolutionTypes = new String[projects.length()];
            projectSupportedScreens = new String[projects.length()];

            String[] clientId = new String[projects.length()];
            String[] projectId = new String[projects.length()];
            String[] clientsId = new String[clients.length()];

            for (int i = 0; i < projects.length(); i++) {
                JSONObject project = projects.getJSONObject(i);
                JSONObject image = project.getJSONObject("image");

                //1.имя проекта, картинка фирмы
                projectName[i] = project.getString("name");
                projectImageURL[i] = image.getString("url");

                //2.имя проекта, картинка фирмы, описание, технология, поддержка экранов, тип решения
                projectDescription[i] = project.getString("description");
                projectTechnologies[i] = project.getString("technologies");
                projectSupportedScreens[i] = project.getString("supportedScreens");
                projectSolutionTypes[i] = project.getString("solutionTypes");

                //2.имя клиента
                clientId[i] = project.getString("clientId");
                for (int j = 0; j < clients.length(); j++) {
                    JSONObject client = clients.getJSONObject(j);
                    clientsId[j] = client.getString("id");
                    if (clientId[i].equals(clientsId[j])) {
                        clientName[i] = client.getString("name");
                    }
                }

                //3.ссылка на assets json
                projectId[i] = project.getString("id");
                projectAssetsUrl[i] = "http://91.250.82.77:8081/3ssdemo/prj/json/galleryAssets.php?projectId=" + projectId[i];

                Log.d(LOG_TAG, "name: " + projectName[i]);
                Log.d(LOG_TAG, "image: " + projectImageURL[i]);
                Log.d(LOG_TAG, "description: " + projectDescription[i]);
                Log.d(LOG_TAG, "technologies: " + projectTechnologies[i]);
                Log.d(LOG_TAG, "supportedScreens: " + projectSupportedScreens[i]);
                Log.d(LOG_TAG, "solutionTypes: " + projectSolutionTypes[i]);
                Log.d(LOG_TAG, "clientName: " + clientName[i]);
                Log.d(LOG_TAG, "projectAssetsUrl: " + projectAssetsUrl[i]);
                Log.d(LOG_TAG, "______________________________________________________");

                bundle = new Bundle();
                bundle.putStringArray("name", projectName);
            }

            // упаковываем данные в понятную для адаптера структуру
            ArrayList<Map<String, Object>> data = new ArrayList<>(projectName.length);
            Map<String, Object> m;
            String ATTRIBUTE_PROJECT_NAME = "name";
            String ATTRIBUTE_PROJECT_IMAGE = "url";
            for (int i = 0; i < projectName.length; i++) {
                m = new HashMap<>();
                m.put(ATTRIBUTE_PROJECT_NAME, projectName[i]);
                m.put(ATTRIBUTE_PROJECT_IMAGE, projectImageURL[i]);
                data.add(m);
            }

            String[] from = {ATTRIBUTE_PROJECT_NAME, ATTRIBUTE_PROJECT_IMAGE};
            int[] to = {R.id.project_name, R.id.project_logo};

            SimpleAdapter sAdapter = new SimpleAdapter(
                    context,
                    data,
                    R.layout.item,
                    from,
                    to);

            sAdapter.setViewBinder(new MyViewBinder());

            listView.setAdapter(sAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
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
