package tr.project.FileManager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;


import java.io.File;
import java.io.IOException;

public class OpenFiles {

    public static void openFiles(Context context, File file) throws IOException {

        File selected = file;
        Uri uri = Utils.getUri(context, file);
        Intent intent = new Intent(Intent.ACTION_VIEW);

        if (uri.toString().contains(".doc")) {
            intent.setDataAndType(uri, "application/msword");
        } else if (uri.toString().contains(".pdf")) {
            intent.setDataAndType(uri, "application/pdf");
        } else if (uri.toString().contains(".mp3") || uri.toString().contains(".wav")) {
            intent.setDataAndType(uri, "audio/x-wav");
        } else if (uri.toString().toLowerCase().contains(".jpeg") || uri.toString().toLowerCase().contains(".jpg") || uri.toString().toLowerCase().contains(".png")) {
            intent.setDataAndType(uri, "image/jpeg");
        } else if (uri.toString().toLowerCase().contains(".mp4")) {
            intent.setDataAndType(uri, "video/*");
        } else {
            intent.setDataAndType(uri, "*/*");
        }

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);
    }
}
