package tr.project.FileManager;

import static tr.project.FileManager.Utils.getUri;

import android.content.Context;
import android.net.Uri;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MyAdapter extends RecyclerView.Adapter<ViewHolder> {

    private Context context;
    private ArrayList<File> filesList;
    private OnFileSelected listener;

    public MyAdapter(Context context, ArrayList<File> filesList, OnFileSelected listener) {
        this.context = context;
        this.filesList = filesList;
        this.listener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.itemslayout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        File selectedFile = filesList.get(position);
        holder.name.setText(selectedFile.getName());

        if (selectedFile.isDirectory()) {
            holder.icon.setImageResource(R.drawable.icons8_folder_100);
            Date lastModified = new Date(selectedFile.lastModified());
            SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
            String formattedDate = formatter.format(lastModified);
            holder.size.setText(formattedDate);
        } else {
            holder.size.setText(Formatter.formatShortFileSize(context, filesList.get(position).length()));

            Uri uri = getUri(context, filesList.get(position));

            String mimeType = Utils.getMimeType(uri, context);

            if (mimeType == null) {
                holder.icon.setImageResource(R.drawable.icons8_file_96);
            } else if (mimeType.substring(0, 5).equals("audio")) {
                holder.icon.setImageResource(R.drawable.icons8_audio_file_96);
            } else if (mimeType.substring(0, 5).equals("video")) {
                Glide.with(context).load(uri).into(holder.icon);
            } else if (mimeType.substring(0, 5).equals("image")) {
                Glide.with(context).load(uri).into(holder.icon);
            } else if (mimeType.equals(Utils.pdf)) {
                holder.icon.setImageResource(R.drawable.icons8_pdf_96);
            } else if (mimeType.equals(Utils.doc) || mimeType.equals(Utils.docx)) {
                holder.icon.setImageResource(R.drawable.icons8_microsoft_word_2019_96);
            } else if (mimeType.equals(Utils.xls) || mimeType.equals(Utils.xlsx)) {
                holder.icon.setImageResource(R.drawable.icons8_microsoft_excel_2019_96);
            } else if (mimeType.equals(Utils.ppt) || mimeType.equals(Utils.pptx)) {
                holder.icon.setImageResource(R.drawable.icons8_microsoft_powerpoint_2019_96);
            } else if (mimeType.equals(Utils.txt)) {
                holder.icon.setImageResource(R.drawable.icons8_txt_file_64);
            } else if (mimeType.equals("application/vnd.android.package-archive")) {
                holder.icon.setImageResource(R.drawable.icons8_apk_96);
            } else if (mimeType.equals("application/zip") || mimeType.equals("application/vnd.rar") || mimeType.equals("application/x-tar") || mimeType.equals("application/gzip")) {
                holder.icon.setImageResource(R.drawable.icons8_winrar_96);
            } else {
                holder.icon.setImageResource(R.drawable.icons8_file_96);
            }
        }


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
