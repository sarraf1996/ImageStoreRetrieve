package com.example.imagestoreretrieve;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<UploadedImages> uploadedImagesList;

    public RecyclerAdapter(Context mContext2, ArrayList<UploadedImages> uploadedImagesList2) {
        this.mContext = mContext2;
        this.uploadedImagesList = uploadedImagesList2;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_image_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.retrievedImageNameView.setText(uploadedImagesList.get(position).getImageName());
        Glide.with(mContext).load(uploadedImagesList.get(position).getImageUrl()).into(holder.retrievedImageView);
    }

    @Override
    public int getItemCount() {
        return this.uploadedImagesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView retrievedImageNameView;
        ImageView retrievedImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            retrievedImageView = (ImageView) itemView.findViewById(R.id.retrievedImageView);
            retrievedImageNameView = (TextView) itemView.findViewById(R.id.retrievedImageNameView);
        }
    }
}
