package tr.project.FileManager;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryViewHolder> {

    private Context context;
    private ArrayList<File> filesList;
    private OnFileSelected listener;

    public GalleryAdapter(Context context, ArrayList<File> filesList, OnFileSelected listener) {
        this.context = context;
        this.filesList = filesList;
        this.listener = listener;
    }


    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.galleryitemslayout, parent, false);

        return new GalleryViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull GalleryViewHolder holder, int position) {

        File selectedFile = filesList.get(position);
        holder.name.setText(selectedFile.getName());

        Uri uri = Uri.fromFile(filesList.get(position));

        Glide
                .with(context)
                .load(uri)
                .into(holder.thumbnail);

        holder.itemContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onFileClicked(selectedFile);
            }
        });

        holder.itemContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onFileLongClicked(selectedFile, holder.getAdapterPosition());
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return filesList.size();
    }

}
