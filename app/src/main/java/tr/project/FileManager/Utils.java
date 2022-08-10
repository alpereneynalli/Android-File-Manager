package tr.project.FileManager;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.core.content.FileProvider;

import java.io.File;
import java.util.ArrayList;


public class Utils {

    static String pdf = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf");
    static String doc = MimeTypeMap.getSingleton().getMimeTypeFromExtension("doc");
    static String docx = MimeTypeMap.getSingleton().getMimeTypeFromExtension("docx");
    static String xls = MimeTypeMap.getSingleton().getMimeTypeFromExtension("xls");
    static String xlsx = MimeTypeMap.getSingleton().getMimeTypeFromExtension("xlsx");
    static String ppt = MimeTypeMap.getSingleton().getMimeTypeFromExtension("ppt");
    static String pptx = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pptx");
    static String txt = MimeTypeMap.getSingleton().getMimeTypeFromExtension("txt");
    static String rtx = MimeTypeMap.getSingleton().getMimeTypeFromExtension("rtx");
    static String rtf = MimeTypeMap.getSingleton().getMimeTypeFromExtension("rtf");


    public static ArrayList<String> getAllShownImagesPath(Activity activity) {
        Uri uri;
        Cursor cursor;
        int column_index;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        String absolutePathOfImage = null;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA};

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, MediaStore.Images.Media.DATE_ADDED + " DESC");

        column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index);
            listOfAllImages.add(absolutePathOfImage);
        }

        return listOfAllImages;
    }

    public static ArrayList<String> getAudioFiles(Activity activity) {
        Uri uri;
        Cursor cursor;
        int column_index;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        String absolutePathOfImage = null;
        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA};

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, MediaStore.Audio.Media.DATE_ADDED + " DESC");

        column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index);
            listOfAllImages.add(absolutePathOfImage);
        }

        return listOfAllImages;
    }

    public static ArrayList<String> getAllVideos(Activity activity) {
        ArrayList<String> videoList = new ArrayList<String>();
        Cursor cursor;
        int column_index;
        Uri uri;
        String pathOfVideo = null;

        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.Video.VideoColumns.DATA, MediaStore.Video.Media.DISPLAY_NAME};

        cursor = activity.getContentResolver().query(uri, projection, null, null, MediaStore.Video.Media.DATE_ADDED + "  DESC");

        column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DATA);
        while (cursor.moveToNext()) {
            pathOfVideo = cursor.getString(column_index);
            videoList.add(pathOfVideo);
        }

        return videoList;
    }

    public static ArrayList<String> getDocumentFiles(Activity activity) {
        ArrayList<String> listOfAllFiles = new ArrayList<>();
        String path;
        Uri uri = MediaStore.Files.getContentUri("external");
        ContentResolver cr = activity.getContentResolver();
        String[] projection = {MediaStore.Files.FileColumns.DATA};
        // only pdf
        String selectionMimeType = MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
                + " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?";


        String[] args = new String[]{pdf, doc, docx, xls, xlsx, ppt, pptx, txt, rtx, rtf};
        Cursor allPdfFiles = cr.query(uri, projection, selectionMimeType, args, MediaStore.Files.FileColumns.DATE_ADDED + " DESC");

        int nameIndex = allPdfFiles.getColumnIndex(MediaStore.Files.FileColumns.DATA);

        while (allPdfFiles.moveToNext()) {
            path = allPdfFiles.getString(nameIndex);
            listOfAllFiles.add(path);
        }
        return listOfAllFiles;
    }

    public static ArrayList<String> getRecentFiles(Activity activity) {
        ArrayList<String> listOfAllFiles = new ArrayList<>();

        ContentResolver cr = activity.getContentResolver();
        Uri uri = MediaStore.Files.getContentUri("external");
        String path = null;
// every column, although that is huge waste, you probably need
// BaseColumns.DATA (the path) only.
        String[] projection = {MediaStore.Files.FileColumns.DATA};
// exclude media files, they would be here also.
        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE_DOCUMENT + "=?" + "";
        String[] selectionArgs = null; // there is no ? in selection so null here

        String sortOrder = null; // unordered
        Cursor allNonMediaFiles = cr.query(uri, projection, selection, selectionArgs, MediaStore.Files.FileColumns.DATE_ADDED + " DESC");
        int nameIndex = allNonMediaFiles.getColumnIndex(MediaStore.Files.FileColumns.DATA);

        while (allNonMediaFiles.moveToNext()) {
            path = allNonMediaFiles.getString(nameIndex);
            listOfAllFiles.add(path);
        }
        return listOfAllFiles;
    }

    public static long[] getStorageStats() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long bytesAvailable;
        long totalBytes;

        bytesAvailable = stat.getBlockSizeLong() * stat.getAvailableBlocksLong();
        totalBytes = stat.getBlockSizeLong() * stat.getBlockCountLong();

        long gigAvailable = bytesAvailable / (1024 * 1024);
        long gigTotal = totalBytes / (1024 * 1024);
        long[] stats = new long[]{gigTotal, gigAvailable, gigTotal - gigAvailable};

        return stats;
    }

    public static String setPrecision(double amt, int precision) {
        return String.format("%." + precision + "f", amt);
    }

    public static String getMimeType(Uri uri, Context context) {
        String mimeType = null;
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            ContentResolver cr = context.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }

    public static String internalStoragePath(String path) {
        String newPath = path;
        newPath = newPath.replace("/storage/emulated/0", "This Device");
        newPath = newPath.replaceAll("/", " > ");
        return newPath;
    }

    public static Uri getUri(Context context, File file) {
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }

    public static void addMedia(Context c, File f) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(f));
        c.sendBroadcast(intent);
    }


    public static void deleteFolder(File file, Context context) {
        if (!file.isDirectory()) {
            deleteMediaStoreFile(file.getAbsolutePath(), context);
        } else {
            if (file.listFiles() != null) {
                for (File f : file.listFiles()) {
                    deleteFolder(f, context);
                }
            }
            deleteMediaStoreFile(file.getAbsolutePath(), context);
            Log.d("delete", "folder deleted");
        }
    }

    public static void deleteMediaStoreFile(String path, Context context) {
        try {
            MediaScannerConnection.scanFile(context, new String[]{path},
                    null, new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            try {
                                context.getContentResolver()
                                        .delete(uri, null, null);
                            } catch (Exception e) {

                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
