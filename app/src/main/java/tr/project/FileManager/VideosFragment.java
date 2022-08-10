package tr.project.FileManager;

import static tr.project.FileManager.Utils.addMedia;
import static tr.project.FileManager.Utils.deleteMediaStoreFile;
import static tr.project.FileManager.Utils.getAllVideos;
import static tr.project.FileManager.Utils.getUri;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class VideosFragment extends Fragment implements OnFileSelected {

    private View view;
    private RecyclerView recyclerView;
    private GalleryAdapter adapter;
    private TextView fileNumber;
    private int size;
    ArrayList<File> filesArray;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ExplorerActivity activity = (ExplorerActivity) getActivity();

        view = inflater.inflate(R.layout.explorerfragment, container, false);
        recyclerView = view.findViewById(R.id.recycler_explorer);
        fileNumber = view.findViewById(R.id.tv_pathHolder);


        filesArray = getVideos();
        size = filesArray.size();
        fileNumber.setText(size + " Videos");

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        adapter = new GalleryAdapter(getContext(), filesArray, this);
        recyclerView.setAdapter(adapter);

        return view;
    }


    @Override
    public void onFileClicked(File file) {
        try {
            OpenFiles.openFiles(getContext(), file);
        } catch (IOException e) {
            Toast.makeText(getContext(), "OPEN FAILED", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFileLongClicked(File file, int position) {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                getContext(), R.style.BottomSheetDialogTheme);

        View bottomSheetView = LayoutInflater.from(getContext())
                .inflate(R.layout.hold_option_dialog,
                        (LinearLayout) getActivity().findViewById(R.id.hold_options));

        LinearLayout buttonShare = bottomSheetView.findViewById(R.id.shareLayout);
        LinearLayout buttonRename = bottomSheetView.findViewById(R.id.renameLayout);
        LinearLayout buttonDelete = bottomSheetView.findViewById(R.id.deleteLayout);
        LinearLayout buttonDetails = bottomSheetView.findViewById(R.id.detailsLayout);

        buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String filename = file.getName();
                Intent share = new Intent();
                Uri uri = getUri(getContext(), file);
                share.setAction(Intent.ACTION_SEND);
                share.setType("video/mp4");
                share.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(share);
                bottomSheetDialog.dismiss();
            }
        });

        buttonDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder detailsDialog = new AlertDialog.Builder(getContext(), R.style.MyDialogTheme);
                detailsDialog.setTitle("Details:");
                final TextView details = new TextView(getContext());
                detailsDialog.setView(details);
                Date lastModified = new Date(file.lastModified());
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                String formattedDate = formatter.format(lastModified);
                details.setBackgroundResource(R.color.greyy);
                details.setPadding(90, 30, 0, 30);
                details.setText("File Name: " + file.getName() + "\n \n" +
                        "Size: " + Formatter.formatShortFileSize(getContext(), file.length()) + "\n \n" +
                        "Path: " + file.getAbsolutePath() + "\n \n" +
                        "Last Modified: " + formattedDate);

                AlertDialog alertDialog_details = detailsDialog.create();
                alertDialog_details.show();
            }
        });

        buttonRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder renameDialog = new AlertDialog.Builder(getContext());
                renameDialog.setTitle("Rename: ");
                final EditText name = new EditText(getContext());
                name.setText(file.getName().substring(0, file.getName().lastIndexOf(".")));
                name.setPadding(90, 30, 0, 30);
                renameDialog.setView(name);
                renameDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String newName = name.getEditableText().toString();
                        String extention = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("."));
                        File cur = new File(file.getAbsolutePath());
                        File dest = new File(file.getAbsolutePath().replace(file.getName(), newName) + extention);
                        if (cur.renameTo(dest)) {
                            deleteMediaStoreFile(file.getAbsolutePath(), getContext());
                            addMedia(getContext(), dest);
                            bottomSheetDialog.cancel();
                            filesArray.set(position, dest);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                }
                            });
                            Toast.makeText(getContext(), "Renamed!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Couldn't rename", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                renameDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        bottomSheetDialog.cancel();
                    }
                });

                AlertDialog alertDialogRename = renameDialog.create();
                alertDialogRename.show();

            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder deleteDialog = new AlertDialog.Builder(getContext());
                deleteDialog.setTitle("Delete " + file.getName() + " ?");
                deleteDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteMediaStoreFile(file.getAbsolutePath(), getContext());
                        filesArray.remove(position);
                        bottomSheetDialog.cancel();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                        size--;
                        fileNumber.setText(size + " Videos");
                    }

                });

                deleteDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        bottomSheetDialog.cancel();
                    }
                });


                AlertDialog alertDialogDelete = deleteDialog.create();
                alertDialogDelete.show();
            }


        });


        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private ArrayList<File> getVideos() {
        ArrayList<String> allVideos = getAllVideos(getActivity());
        System.out.println(allVideos.size());
        ArrayList<File> videoFiles = new ArrayList<>();
        for (String video : allVideos) {
            File vid = new File(video);
            videoFiles.add(vid);
        }
        return videoFiles;
    }


}
