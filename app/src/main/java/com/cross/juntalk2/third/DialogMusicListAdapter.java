package com.cross.juntalk2.third;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.DialogAdapterMusicuploadlistItemBinding;
import com.cross.juntalk2.databinding.DialogAdapterMusicuploadlistItemVerticalBinding;
import com.cross.juntalk2.model.MusicModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DialogMusicListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<MusicModel> musicModels = new ArrayList<>();
    private DialogMusicList.ClickItemInterface clickItemInterface;
    public DialogMusicListAdapter(Context context , DialogMusicList.ClickItemInterface clickItemInterface) {
        this.context = context;
        this. clickItemInterface =clickItemInterface;
    }

    public void setItems(List<MusicModel> musicModels) {
        if (musicModels == null) return;
        this.musicModels.clear();
        this.musicModels.addAll(musicModels);
        notifyDataSetChanged();
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        DialogAdapterMusicuploadlistItemVerticalBinding binding = DialogAdapterMusicuploadlistItemVerticalBinding.inflate(inflater, parent, false);
        binding.setDialogMusicListAdapter(this);
        return new DialogMusicListViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        MusicModel musicModel = musicModels.get(position);
        if (holder instanceof DialogMusicListViewHolder) {
            ((DialogMusicListViewHolder) holder).onBind(musicModel, holder.getAbsoluteAdapterPosition());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.dialog_adapter_musicuploadlist_item_vertical;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return musicModels == null ? 0 : musicModels.size();
    }


    public void wholeCardViewOnClickEvent(View view, MusicModel musicModel, int position){
        clickItemInterface.getItems(musicModel , position);
    }

    public class DialogMusicListViewHolder extends RecyclerView.ViewHolder {
        private DialogAdapterMusicuploadlistItemVerticalBinding binding;

        public DialogMusicListViewHolder(@NonNull @NotNull DialogAdapterMusicuploadlistItemVerticalBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }

        public void onBind(MusicModel musicModel, int position) {
            binding.setMusicModel(musicModel);
            binding.setPosition(position);
            binding.executePendingBindings();
        }
    }
}
