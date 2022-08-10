package tr.project.FileManager;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class GalleryViewHolder extends RecyclerView.ViewHolder{
    public TextView name;
    public ImageView thumbnail;
    public CardView itemContainer;

    public GalleryViewHolder(View itemView){
        super(itemView);

        name = itemView.findViewById(R.id.galleryItemName);
        thumbnail = itemView.findViewById(R.id.galleryItemImg);
        itemContainer = itemView.findViewById(R.id.galleryItemsContainer);
    }
}