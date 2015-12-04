package ua.icedragon.json;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class ParseAssetsTask extends AsyncTask<String, String, JSONObject[]> {

    private ProgressDialog progressDialog;

    public Context context;
    public Activity activity;
    public AsyncAssetsResponse infoAssets;

    public ParseAssetsTask(Context _cxt, Activity _activity) {
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
            JSONArray assets = jsonObjects[0].getJSONArray("assets");

            String[] projectAssets = new String[assets.length()];

            for (int i = 0; i < assets.length(); i++) {
                JSONObject asset = assets.getJSONObject(i);

                //1.Ссылки на assets
                projectAssets[i] = asset.getString("url");
            }

            infoAssets.processFinish(projectAssets);

            progressDialog.dismiss();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
