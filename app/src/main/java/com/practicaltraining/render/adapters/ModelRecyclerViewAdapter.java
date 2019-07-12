package com.practicaltraining.render.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.practicaltraining.render.MainActivity;
import com.practicaltraining.render.R;
import com.practicaltraining.render.callbacks.OnSettingItemClickListener;
import com.practicaltraining.render.objects.ModelItem;
import com.practicaltraining.render.objects.SettingItem;

import java.util.ArrayList;
import java.util.List;



public class ModelRecyclerViewAdapter extends RecyclerView.Adapter<ModelRecyclerViewAdapter.VH> {
    private OnSettingItemClickListener onSettingItemClickListener;
    private Context context;
    private List<Boolean> isClicked;
    private Bitmap bitmap;

    public List<Boolean> getIsClicked() {
        return isClicked;
    }

    public void setOnSettingItemClickListener(OnSettingItemClickListener onSettingItemClickListener) {
        this.onSettingItemClickListener = onSettingItemClickListener;
    }

    public static class VH extends RecyclerView.ViewHolder{
        TextView name;
        ImageView icon;
        ConstraintLayout background;
        TextView mesh;

        public VH(View v) {
            //this is a view holder
            super(v);
            this.name=itemView.findViewById(R.id.names);
            this.icon=itemView.findViewById(R.id.imgs);
            this.background=itemView.findViewById(R.id.recyclerItem);
            this.mesh = itemView.findViewById(R.id.mesh_name);
        }
    }
    private List<ModelItem> mData;//Adapter接收list作为参数
    public ModelRecyclerViewAdapter(List<ModelItem>lis,Context c){
        this.mData =lis;
        isClicked=new ArrayList<>();
        for(int i=0;i<lis.size();i++){
            isClicked.add(false);
        }
        context = c;
    }
    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        //创建组件
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.resourcesrecyclerview_item, parent, false);
        return new VH(v);
    }


    @Override
    public void onBindViewHolder(@NonNull VH holder, final int position) {
        //传递数据
        holder.name.setText(mData.get(position).getName());
        holder.mesh.setText(mData.get(position).getMeshName());
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize= 4;
        bitmap =BitmapFactory.decodeResource(context.getResources(),mData.get(position).getId(), opts);
        holder.icon.setImageBitmap(bitmap);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSettingItemClickListener.onSettingItemClick(position);
                //choose
                //holder.background.setBackgroundColor(Color.parseColor("#FFDED3"));
                for(int i=0;i<isClicked.size();i++){
                    isClicked.set(i,false);
                }
                isClicked.set(position,true);
                notifyDataSetChanged();
            }
        });
        //holder.background.setBackgroundResource(0);//remove background color
        if(isClicked.get(position)){
            holder.background.setBackgroundColor(Color.parseColor("#FFDED3"));
        }
        else{
            holder.background.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }



    public ModelItem getModelItem(int position){
        return mData.get(position)!=null?mData.get(position):null;
    }
}
