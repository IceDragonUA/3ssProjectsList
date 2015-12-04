package ua.icedragon.json;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class ParseTask extends AsyncTask<String, String, JSONObject[]> {

    private ProgressDialog progressDialog;

    public Context context;
    public Activity activity;
    public AsyncResponse info;

    public ParseTask(Context _cxt, Activity _activity) {
        this.activity = _activity;
        context = _cxt;
        progressDialog = new ProgressDialog(context, R.style.NewDialog);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.setMessage("Getting Data ...");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected JSONObject[] doInBackground(String... urls) {
        JSONParser jParser = new JSONParser();
        JSONObject[] jsonObjects = new JSONObject[urls.length];
        for (int i = 0; i < urls.length; i++){
            jsonObjects[i] = jParser.getJSONFromUrl(urls[i]);
        }
        return jsonObjects;
    }

    @Override
    protected void onPostExecute(JSONObject[] jsonObjects) {

        try {
            JSONArray projects = jsonObjects[0].getJSONArray("projects");
            JSONArray clients = jsonObjects[1].getJSONArray("clients");

            String[] clientName = new String[projects.length()];
            String[] projectName = new String[projects.length()];
            String[] projectImageUrl = new String[projects.length()];
            String[] projectAssetsUrl = new String[projects.length()];
            String[] projectDescription = new String[projects.length()];
            String[] projectTechnologies = new String[projects.length()];
            String[] projectSolutionTypes = new String[projects.length()];
            String[] projectSupportedScreens = new String[projects.length()];

            String[] clientId = new String[projects.length()];
            String[] projectId = new String[projects.length()];
            String[] clientsId = new String[clients.length()];

            for (int i = 0; i < projects.length(); i++) {
                JSONObject project = projects.getJSONObject(i);
                JSONObject image = project.getJSONObject("image");

                //1.имя проекта, картинка фирмы
                projectName[i] = project.getString("name");
                projectImageUrl[i] = image.getString("url");

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

            }

            info.processFinish(
                    projectName,
                    projectImageUrl,
                    projectDescription,
                    projectTechnologies,
                    projectSupportedScreens,
                    projectSolutionTypes,
                    clientName,
                    projectAssetsUrl,
                    projects.length()
            );

            progressDialog.dismiss();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
