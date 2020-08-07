package com.arcsoft.arcfacedemo.widget;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arcsoft.arcfacedemo.R;

import java.util.List;

public class CriminalNameAdapter extends RecyclerView.Adapter<CriminalNameAdapter.ViewHolder>{

    private List<String> namwList;

    public CriminalNameAdapter(List<String> namwList) {
        this.namwList = namwList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_criminal_name,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.CriminalName.setText(namwList.get(i));
    }


    @Override
    public int getItemCount() {
        return namwList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView CriminalName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            CriminalName = itemView.findViewById(R.id.criminal_name);
        }
    }
}
