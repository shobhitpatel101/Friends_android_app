package com.example.javaapp;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {
    Context context;
    ArrayList<String>PostDiscriptions;
    ArrayList<String>PostDateTimes;
    ArrayList<String>PostUploaderEmail;
    ArrayList<String>PostUploaderName;


    class FeedViewHolder extends RecyclerView.ViewHolder{
        TextView Discription, Name, Time;
        ImageView upload, profile;
        public FeedViewHolder(@NonNull View itemView){
            super(itemView);
            Discription = itemView.findViewById(R.id.Descriptopn);
            profile = itemView.findViewById(R.id.profil_image_feed);
            Name = itemView.findViewById(R.id.uploader_name);
            Time = itemView.findViewById(R.id.upload_time);
            upload = itemView.findViewById(R.id.uploaded_story);
        }
    }

    public FeedAdapter(Context context, ArrayList<String> postDiscriptions, ArrayList<String> postDateTimes, ArrayList<String> postUploaderEmail, ArrayList<String> postUploaderName) {
        this.context = context;
        this.PostDiscriptions = postDiscriptions;
        this.PostDateTimes = postDateTimes;
        this.PostUploaderEmail = postUploaderEmail;
        this.PostUploaderName = postUploaderName;
    }

    @NonNull
    @Override
    public FeedAdapter.FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_recycler_view, parent, false);
        return new FeedAdapter.FeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FeedViewHolder holder, int position) {
        holder.Discription.setText(PostDiscriptions.get(position));
        //holder.Name.setText(PostUploaderEmail.get(position));
        holder.Time.setText(PostDateTimes.get(position));
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("profileImages");
        System.out.println();
        storageReference.child(PostUploaderEmail.get(position)+".jpeg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri).placeholder(R.mipmap.ic_launcher_round).into(holder.profile);
            }
        });
        StorageReference storageReference2 = FirebaseStorage.getInstance().getReference("Feeds");
        storageReference2.child(PostUploaderEmail.get(position)).child((PostDateTimes.get(position).hashCode()+".jpeg")).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                System.out.println(uri.toString());
                Glide.with(context).load(uri).into(holder.upload);
            }
        });

    }

    @Override
    public int getItemCount() {
        return PostDiscriptions.size();
    }

}
