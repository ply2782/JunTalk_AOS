package com.cross.juntalk2.fifth;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.AdapterClubfileItemBinding;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;

import java.util.ArrayList;
import java.util.List;

public class ClubFileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<Bitmap> bitmapList = new ArrayList<>();


    private CreateClubActivity.RemoveClickInterface removeClickInterface;

    public void setRemoveClickInterface(CreateClubActivity.RemoveClickInterface removeClickInterface) {
        this.removeClickInterface = removeClickInterface;
    }

    public ClubFileAdapter(Context context) {
        this.context = context;

    }

    private RequestManager initGlide() {
        // 로드된 이미지를 받을 Target을 생성한다.

        Shimmer shimmer = new Shimmer.ColorHighlightBuilder()// The attributes for a ShimmerDrawable is set by this builder
                .setDuration(1000) // how long the shimmering animation takes to do one full sweep
                .setBaseAlpha(0.7f) //the alpha of the underlying children
                .setBaseColor(Color.GRAY)
                .setHighlightAlpha(0.7f) // the shimmer alpha amount
                .setHighlightColor(Color.WHITE)
                .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
                .setAutoStart(true)
                .build();
        ShimmerDrawable shimmerDrawable = new ShimmerDrawable();
        shimmerDrawable.setShimmer(shimmer);
        RequestOptions requestOptions = new RequestOptions()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .disallowHardwareConfig()
                .error(R.drawable.btn_x)
                .placeholder(shimmerDrawable);
        return Glide.with(context)
                .setDefaultRequestOptions(requestOptions);
    }



    public void setItems(List<Bitmap> bitmapList) {
        if (bitmapList == null) return;
        this.bitmapList.clear();
        this.bitmapList.addAll(bitmapList);
        notifyDataSetChanged();
    }

    public void removeClickEvent(Bitmap bitmap) {
        bitmapList.remove(bitmap);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.adapter_clubfile_item;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        AdapterClubfileItemBinding binding = AdapterClubfileItemBinding.inflate(inflater, parent, false);
        return new ClubFileAdapterViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Bitmap bitmap = bitmapList.get(position);
        if (holder instanceof ClubFileAdapterViewHolder) {
            ((ClubFileAdapterViewHolder) holder).onBind(bitmap, position);
        }
    }

    @Override
    public int getItemCount() {
        return bitmapList == null ? 0 : bitmapList.size();
    }

    public class ClubFileAdapterViewHolder extends RecyclerView.ViewHolder {

        private AdapterClubfileItemBinding binding;

        public ClubFileAdapterViewHolder(@NonNull AdapterClubfileItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void onBind(Bitmap bitmap, int position) {
            binding.imageView.setImageBitmap(bitmap);

            binding.removeImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(getItemCount()>0){
                        removeClickInterface.removeClick(bitmap, position);
                    }
                }
            });
            binding.executePendingBindings();

        }


    }
}
