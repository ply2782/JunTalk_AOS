package com.cross.juntalk2.fourth;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.DialogCustomadapterItemYouBinding;
import com.cross.juntalk2.model.BulletinCommentModel;
import com.cross.juntalk2.utils.JunApplication;
import com.skydoves.powermenu.MenuBaseAdapter;

public class DialogCustomAdapter extends MenuBaseAdapter<BulletinCommentModel> {
    private DialogCustomadapterItemYouBinding binding_you;
    private Context context;
    private int user_Index;

    public DialogCustomAdapter(Context context) {
        this.context = context;
        user_Index = JunApplication.getMyModel().user_Index;
    }


    @Override
    public View getView(int index, View view, ViewGroup viewGroup) {
        final Context context = viewGroup.getContext();

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.dialog_customadapter_item_you, viewGroup, false);
            binding_you = DataBindingUtil.bind(view);
        }
        if (getItem(index) instanceof BulletinCommentModel) {
            binding_you.setBulletinCommentModel((BulletinCommentModel) getItem(index));
        }
        return super.getView(index, view, viewGroup);
    }


    @Override
    public Object getItem(int index) {
        return super.getItem(index);
    }


    @Override
    public long getItemId(int index) {
        return index;
    }


}