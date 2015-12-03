package ua.icedragon.json;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);

        Intent intent = getIntent();

        Toast.makeText(InfoActivity.this, "Position: " + intent.getIntExtra("position", 0), Toast.LENGTH_LONG).show();
    }
}
