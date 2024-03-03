package com.cross.juntalk2.first;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.AdapterCustomDialogMenuAdapterBinding;
import com.cross.juntalk2.model.FriendModel;
import com.cross.juntalk2.model.MyModel;
import com.skydoves.powermenu.MenuBaseAdapter;

public class CustomDialogMenuAdapter extends MenuBaseAdapter<MyModel> {

    private AdapterCustomDialogMenuAdapterBinding binding;



    @Override
    public View getView(int index, View view, ViewGroup viewGroup) {
        final Context context = viewGroup.getContext();

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.adapter_custom_dialog_menu_adapter, viewGroup, false);
            binding = AdapterCustomDialogMenuAdapterBinding.bind(view);
        }

        if (getItem(index) instanceof FriendModel) {
            FriendModel item = (FriendModel) getItem(index);
            binding.setUserInfoModel(item);
            Log.e("abc","time : "+ item.user_lastLogin);
            binding.executePendingBindings();
        } else if (getItem(index) instanceof MyModel) {
            MyModel item = (MyModel) getItem(index);
            binding.setMyInfoModel(item);
            Log.e("abc","time : "+ item.user_lastLogin);
            binding.executePendingBindings();
        }


        return super.getView(index, view, viewGroup);
    }

}