package tr.project.FileManager;

import static tr.project.FileManager.Utils.addMedia;
import static tr.project.FileManager.Utils.deleteFolder;
import static tr.project.FileManager.Utils.deleteMediaStoreFile;
import static tr.project.FileManager.Utils.getUri;
import static tr.project.FileManager.Utils.internalStoragePath;


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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

public class ExplorerFragment extends Fragment implements OnFileSelected {

    private View view;
    private FastScrollRecyclerView recyclerView;
    private TextView pathHolder;
    private String path;
    private MyAdapter adapter;
    private String category;
    ArrayList<File> filesArray;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ExplorerActivity activity = (ExplorerActivity) getActivity();
        view = inflater.inflate(R.layout.explorerfragment, container, false);
        recyclerView = view.findViewById(R.id.recycler_explorer);
        pathHolder = view.findViewById(R.id.tv_pathHolder);
        path = getArguments().getString("path");
        category = getArguments().getString("category");

        pathHolder.setText(internalStoragePath(path));
        pathHolder.post(new Runnable() {
            @Override
            public void run() {
                pathHolder.setSelected(true);
            }
        });

        File root = new File(path);
        filesArray = listFiles(root);

        if (category.equals("Downloads")) {
            filesArray.sort(Comparator.comparing(File::lastModified).reversed());
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new MyAdapter(getContext(), filesArray, this);
        recyclerView.setAdapter(adapter);

        return view;
    }


    @Override
    public void onFileClicked(File file) {
        if (file.isDirectory()) {
            Bundle bundle = new Bundle();
            bundle.putString("path", file.getAbsolutePath());
            bundle.putString("category", "All Files");
            ExplorerFragment explorerFragment = new ExplorerFragment();
            explorerFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, explorerFragment).addToBackStack(null).commit();
        } else {
            try {
                OpenFiles.openFiles(getContext(), file);
            } catch (IOException e) {
                Toast.makeText(getContext(), "OPEN FAILED", Toast.LENGTH_SHORT).show();
            }
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
                share.setType("image/jpeg");
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
                name.setText(file.getName());
                if (!file.isDirectory()) {
                    try {
                        name.setText(file.getName().substring(0, file.getName().lastIndexOf(".")));
                        if (name.getText().toString().equals("")) {
                            name.setText(file.getName());
                        }
                    } catch (Exception e) {
                        name.setText(file.getName());
                    }
                }
                name.setPadding(90, 30, 0, 30);
                renameDialog.setView(name);

                renameDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String newName = name.getEditableText().toString();
                        String extention = "";
                        if (!file.isDirectory()) {
                            extention = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("."));
                        }
                        File cur = new File(file.getAbsolutePath());
                        File dest = new File(file.getAbsolutePath().replace(file.getName(), newName) + extention);
                        if (cur.renameTo(dest)) {
                            if (file.isDirectory()) {
                                filesArray.set(position, dest);
                                adapter.notifyItemChanged(position);
                                bottomSheetDialog.cancel();
                            } else {
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
                            }
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
                        if (!file.isDirectory()) {
                            deleteMediaStoreFile(file.getAbsolutePath(), getContext());

                        } else {
                            deleteFolder(file, getContext());

                        }
                        filesArray.remove(position);
                        bottomSheetDialog.cancel();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
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

    private ArrayList<File> listFiles(File root) {

        ArrayList<File> filelist = new ArrayList<>();
        File[] fileArray = root.listFiles();

        if (fileArray == null) {
            return filelist;
        }

        filelist = new ArrayList<>(Arrays.asList(fileArray));

        return filelist;
    }

}
