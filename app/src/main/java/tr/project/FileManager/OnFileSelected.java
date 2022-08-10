package tr.project.FileManager;

import java.io.File;

public interface OnFileSelected {

    public void onFileClicked(File file);

    public void onFileLongClicked(File file, int position);
}
