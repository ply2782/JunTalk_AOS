package com.cross.juntalk2.first;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.AdapterCommonnoticeItemBinding;
import com.cross.juntalk2.model.CommonNoticeModel;
import com.cross.juntalk2.retrofit.RetrofitClient;
import com.cross.juntalk2.retrofit.RetrofitService;
import com.cross.juntalk2.utils.CommonString;
import com.cross.juntalk2.utils.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoticeAdapter extends ListAdapter<CommonNoticeModel, NoticeAdapter.NoticeViewHolder> {


    private NoticeActivity.ItemClickInterface itemClickInterface;
    private Context context;
    protected NoticeAdapter(@NonNull @NotNull DiffUtil.ItemCallback<CommonNoticeModel> diffCallback , Context context) {
        super(diffCallback);
        this.context =context;
    }

    public void setItemClickInterface(NoticeActivity.ItemClickInterface itemClickInterface) {
        this.itemClickInterface = itemClickInterface;
    }

    @NonNull
    @NotNull
    @Override
    public NoticeViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        AdapterCommonnoticeItemBinding binding = AdapterCommonnoticeItemBinding.inflate(inflater, parent, false);
        return new NoticeViewHolder(binding);
    }


    @Override
    public int getItemViewType(int position) {
        return R.layout.adapter_commonnotice_item;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull NoticeViewHolder holder, int position) {
        holder.onBind(getItem(position), position, itemClickInterface);




    }

    public class NoticeViewHolder extends RecyclerView.ViewHolder {
        private AdapterCommonnoticeItemBinding binding;

        public NoticeViewHolder(@NonNull @NotNull AdapterCommonnoticeItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void onBind(CommonNoticeModel commonNoticeModel, int position, NoticeActivity.ItemClickInterface itemClickInterface) {
            binding.setCommonNoticeModel(commonNoticeModel);
            binding.executePendingBindings();
            binding.checkbox.setChecked(getCurrentList().get(position).isChecked);
            binding.checkbox.setOnClickListener(v -> {
                if(binding.checkbox.isChecked()){
                    getCurrentList().get(position).isChecked=true;
                    itemClickInterface.addItems(String.valueOf(commonNoticeModel.notice_Index) , commonNoticeModel);
                }else{
                    getCurrentList().get(position).isChecked=false;
                    itemClickInterface.removeItems(String.valueOf(commonNoticeModel.notice_Index) , commonNoticeModel);
                }
            });
        }

    }



}
