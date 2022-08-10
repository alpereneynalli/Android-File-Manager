package tr.project.FileManager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class ExplorerActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private String path;
    private String category;
    private TextView categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorer);
        categoryName = findViewById(R.id.tv_categoryName);
        category = getIntent().getStringExtra("category");
        path = getIntent().getStringExtra("path");


        switch (category) {

            case "All Files": {
                categoryName.setText("All Files");

                ExplorerFragment explorerFragment = new ExplorerFragment();
                Bundle bundle = new Bundle();
                bundle.putString("path", path);
                bundle.putString("category", "All Files");
                explorerFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, explorerFragment).commit();
                break;
            }

            case "Images":
                categoryName.setText("Images");

                ImagesFragment imagesFragment = new ImagesFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, imagesFragment).commit();
                break;

            case "Videos":
                categoryName.setText("Videos");

                VideosFragment videosFragment = new VideosFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, videosFragment).commit();
                break;

            case "Downloads": {
                categoryName.setText("Downloads");

                ExplorerFragment explorerFragment = new ExplorerFragment();
                Bundle bundle = new Bundle();
                path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                bundle.putString("path", path);
                bundle.putString("category", "Downloads");
                explorerFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, explorerFragment).commit();
                break;
            }

            case "Documents": {
                categoryName.setText("Documents");

                DocumentsFragment documentsFragment = new DocumentsFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, documentsFragment).commit();
                break;
            }

            case "Audio": {
                categoryName.setText("Audio");

                AudioFragment audioFragment = new AudioFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, audioFragment).commit();
                break;
            }

        }

        btnBack = findViewById(R.id.btn_Back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();

        } else {
            getSupportFragmentManager().popBackStackImmediate();
        }

    }
}
