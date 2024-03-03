package com.cross.juntalk2.fourth;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.AdapterCategoryviewpager2ItemBinding;

import org.jetbrains.annotations.NotNull;


public class CategoryViewPager2Adapter extends ListAdapter<String,CategoryViewPager2Adapter.CategoryViewPager2ViewHolder> {

    private Context context;

    protected CategoryViewPager2Adapter(@NonNull @NotNull DiffUtil.ItemCallback<String> diffCallback , Context context) {
        super(diffCallback);
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.adapter_categoryviewpager2_item;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @NotNull
    @Override
    public CategoryViewPager2ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        AdapterCategoryviewpager2ItemBinding binding =AdapterCategoryviewpager2ItemBinding.inflate(inflater,parent,false);
        return new CategoryViewPager2ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CategoryViewPager2ViewHolder holder, int position) {
        holder.onBind(getItem(position));
    }

    public class CategoryViewPager2ViewHolder extends RecyclerView.ViewHolder {
        private AdapterCategoryviewpager2ItemBinding binding;
        public CategoryViewPager2ViewHolder(@NonNull @NotNull AdapterCategoryviewpager2ItemBinding binding) {
            super(binding.getRoot());
            this.binding =binding;
        }
        public void onBind(String category){
            binding.setCategoryString(category);
            binding.executePendingBindings();
        }
    }
}
