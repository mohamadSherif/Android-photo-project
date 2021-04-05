package com.example.photos38;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> implements Filterable {

    ArrayList<Photo> list;
    ArrayList<Photo> listAll;
    RecyclerViewClickListener listener;

    public RecyclerViewAdapter(ArrayList<Photo> list, RecyclerViewClickListener listener) {
        this.list = list;
        this.listAll = new ArrayList<>(list);
        this.listener = listener;
    }

    public void getOrResults(String tag1, String tag2){

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_image_view, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.image.setImageBitmap(list.get(position).byteThumbImage.getThumbImage());
        holder.caption.setText(list.get(position).caption);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {

        @Override //run on background thread
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Photo> filteredList = new ArrayList<>();
            if(constraint==null || constraint.length()==0)
                filteredList.addAll(listAll);
            else{
                String filterPattern = constraint.toString().trim().toLowerCase();
                for(Photo p : listAll){
                    if(p.hasSingleTag(filterPattern))
                        filteredList.add(p);
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override //run on UI thread
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            list.addAll((ArrayList<Photo>) results.values);
            notifyDataSetChanged();
        }
    };

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView image;
        TextView caption;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            caption = itemView.findViewById(R.id.caption);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v, getAdapterPosition());
        }
    }

    interface  RecyclerViewClickListener{
        void onClick(View view, int position);
    }
}


