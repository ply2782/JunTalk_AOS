package com.cross.juntalk2.first;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.AdapterCustomDialogMenuAdapterBinding;
import com.cross.juntalk2.databinding.AdapterPopupmenuCustomItemLicenseBinding;
import com.cross.juntalk2.model.MyModel;
import com.skydoves.powermenu.MenuBaseAdapter;

public class CustomPopUpMenuAdapter  extends MenuBaseAdapter<String> {

    private AdapterPopupmenuCustomItemLicenseBinding binding;


    @Override
    public View getView(int index, View view, ViewGroup viewGroup) {
        final Context context = viewGroup.getContext();

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.adapter_popupmenu_custom_item_license, viewGroup, false);
            binding = AdapterPopupmenuCustomItemLicenseBinding.bind(view);
        }

        String item = (String) getItem(index);
        binding.setItem(item);
        binding.executePendingBindings();

        return super.getView(index, view, viewGroup);
    }

}
