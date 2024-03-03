package com.cross.juntalk2.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.DialogLoadingBinding;

import java.util.Objects;

public class LoadingDialog extends Dialog {
    private DialogLoadingBinding loadingBinding;
    private Context context;

    public LoadingDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = LayoutInflater.from(context);
        loadingBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_loading, null, false);
        setContentView(loadingBinding.getRoot());
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCancelable(false);
    }
}
