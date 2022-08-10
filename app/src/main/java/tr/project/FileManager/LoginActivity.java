package tr.project.FileManager;

import static tr.project.FileManager.Utils.*;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    private LinearLayout btnFolders;
    private LinearLayout btnPhotos;
    private LinearLayout btnVideos;
    private LinearLayout btnDownloads;
    private LinearLayout btnDocuments;
    private LinearLayout btnAudio;
    private long[] storageStats;
    private ProgressBar storageBar;
    private TextView storageBarText;
    private TextView availableStorageText;
    private TextView usedStorageText;
    private double storagePercent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        btnFolders = findViewById(R.id.buttonFolders);
        btnPhotos = findViewById(R.id.buttonPhotos);
        btnVideos = findViewById(R.id.buttonVideos);
        btnDownloads = findViewById(R.id.buttonDownloads);
        btnDocuments = findViewById(R.id.buttonDocuments);
        btnAudio = findViewById(R.id.buttonAudios);

        storageBar = findViewById(R.id.progressBar);
        storageBarText = findViewById(R.id.circleBarMiddleText);
        availableStorageText = findViewById(R.id.textTotalSpace);
        usedStorageText = findViewById(R.id.textUsedSpace);


        btnFolders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ExplorerActivity.class);
                String path = Environment.getExternalStorageDirectory().getPath();
                intent.putExtra("path", path);
                intent.putExtra("category", "All Files");
                startActivity(intent);
            }
        });

        btnPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ExplorerActivity.class);
                String path = Environment.getExternalStorageDirectory().getPath();
                intent.putExtra("path", path);
                intent.putExtra("category", "Images");
                startActivity(intent);
            }
        });

        btnVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ExplorerActivity.class);
                String path = Environment.getExternalStorageDirectory().getPath();
                intent.putExtra("path", path);
                intent.putExtra("category", "Videos");
                startActivity(intent);
            }
        });

        btnDownloads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ExplorerActivity.class);
                String path = Environment.getExternalStorageDirectory().getPath();
                intent.putExtra("path", path);
                intent.putExtra("category", "Downloads");
                startActivity(intent);
            }
        });

        btnDocuments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ExplorerActivity.class);
                String path = Environment.getExternalStorageDirectory().getPath();
                intent.putExtra("path", path);
                intent.putExtra("category", "Documents");
                startActivity(intent);
            }
        });

        btnAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ExplorerActivity.class);
                String path = Environment.getExternalStorageDirectory().getPath();
                intent.putExtra("path", path);
                intent.putExtra("category", "Audio");
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        storageStats = getStorageStats();
        storagePercent = (double) (storageStats[2]) / storageStats[0] * 100;
        storageBar.setProgress((int) (storagePercent));
        storageBarText.setText("%" + (int) (storagePercent));
        availableStorageText.setText("" + setPrecision((double) storageStats[0] / 1024, 2) + " GB");
        usedStorageText.setText("" + setPrecision((double) storageStats[2] / 1024, 2) + " GB");
    }
}