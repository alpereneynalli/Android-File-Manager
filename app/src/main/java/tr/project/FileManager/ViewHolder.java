package tr.project.FileManager;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends RecyclerView.ViewHolder {
    public TextView name;
    public TextView size;
    public ImageView icon;
    public CardView itemContainer;

    public ViewHolder(View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.tv_fileName);
        size = itemView.findViewById(R.id.tv_fileSize);
        icon = itemView.findViewById(R.id.img_fileType);
        itemContainer = itemView.findViewById(R.id.itemContainer);
    }
}