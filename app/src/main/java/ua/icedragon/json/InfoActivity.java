package ua.icedragon.json;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class InfoActivity extends AppCompatActivity implements View.OnTouchListener, AsyncAssetsResponse {

    public static String LOG_TAG = "myLogs";

    private float fromPosition;

    private ImageLoader imageLoader;
    private ViewFlipper viewFlipper;
    private LockableScrollView scrolling;
    private RelativeLayout.LayoutParams relativeLayoutParam, imageViewLayoutParam;

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

        ParseAssetsTask parseTask = new ParseAssetsTask(this, this);
        parseTask.infoAssets = this;
        parseTask.execute(projectAssetsUrl);

        scrolling = (LockableScrollView) findViewById(R.id.scrollView);
        initializeImageLoaderConfig();

        ImageView logo = (ImageView) findViewById(R.id.logo);
        imageLoader.displayImage(projectImageUrl, logo);

        TextView title = (TextView) findViewById(R.id.title);
        TextView description = (TextView) findViewById(R.id.description);
        TextView technologies = (TextView) findViewById(R.id.technologies);
        TextView supportedScreens = (TextView) findViewById(R.id.supportedScreens);
        TextView solutionTypes = (TextView) findViewById(R.id.solutionTypes);
        TextView client = (TextView) findViewById(R.id.clientName);

        title.setText("Project Name: " + projectName);
        description.setText("Description:\n\n" + projectDescription);
        technologies.setText("Technologies:\n" + projectTechnologies);
        supportedScreens.setText("Supported Screens:\n" + projectSupportedScreens);
        solutionTypes.setText("Solution Types:\n" + projectSolutionTypes);
        client.setText("Client: " + clientName);

        viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);

        Button left = (Button) findViewById(R.id.left);
        Button right = (Button) findViewById(R.id.right);
        Button back = (Button) findViewById(R.id.back);

        left.setBackgroundResource(R.drawable.button_left);
        right.setBackgroundResource(R.drawable.button_right);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.left:
                        motionLeft();
                        break;

                    case R.id.right:
                        motionRight();
                        break;

                    case R.id.back:
                        finish();
                        break;

                    default:
                        break;
                }
            }
        };

        left.setOnClickListener(onClickListener);
        right.setOnClickListener(onClickListener);
        back.setOnClickListener(onClickListener);

        viewFlipper.setOnTouchListener(this);

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

    public void processFinish(String[] projectAssets){
        final RelativeLayout relativeLayout[] = new RelativeLayout[projectAssets.length];
        ImageView imageView[] = new ImageView[projectAssets.length];
        initializeViewFlipperContentParams();
        for (int i = 0; i < projectAssets.length; i++) {
            relativeLayout[i] = new RelativeLayout(this);
            imageView[i] = new ImageView(this);
            relativeLayout[i].setLayoutParams(relativeLayoutParam);
            relativeLayout[i].addView(imageView[i], imageViewLayoutParam);
            imageLoader.displayImage(projectAssets[i], imageView[i]);
            viewFlipper.addView(relativeLayout[i]);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        viewFlipper.requestFocus();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                fromPosition = event.getX();
                scrolling.setScrollingEnabled(false);
                break;
            case MotionEvent.ACTION_UP:
                float toPosition = event.getX();
                if (fromPosition > toPosition) {
                    motionRight();
                } else if (fromPosition < toPosition) {
                    motionLeft();
                }
                scrolling.setScrollingEnabled(true);
                break;
            default:
                break;
        }
        return true;
    }

    private void motionLeft() {
        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.go_prev_in));
        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.go_prev_out));
        viewFlipper.showPrevious();
    }

    private void motionRight() {
        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.go_next_in));
        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.go_next_out));
        viewFlipper.showNext();
    }

    private void initializeImageLoaderConfig() {
        ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault(this);
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
    }

    private void initializeViewFlipperContentParams() {
        relativeLayoutParam = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        imageViewLayoutParam = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        imageViewLayoutParam.addRule(RelativeLayout.CENTER_IN_PARENT);
    }
}
