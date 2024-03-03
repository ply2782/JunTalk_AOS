package com.cross.juntalk2.fourth;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.cross.juntalk2.R;
import com.cross.juntalk2.model.LilsReplyModel;
import com.cross.juntalk2.utils.CommonString;
//import com.cross.juntalk2.utils.GlideApp;
import com.cross.juntalk2.utils.JunApplication;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.google.android.material.imageview.ShapeableImageView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LilsReplyListAdapter extends BaseAdapter {
    private Context context;
    private List<LilsReplyModel> lilsReplyModelList = new ArrayList<>();
    private String myId;
    private SimpleDateFormat originalDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat changeDateFormat = new SimpleDateFormat("a HH:mm");
    private SimpleDateFormat mainRegDateFormat = new SimpleDateFormat("yyyy-MM-dd E요일");
    private String blockContent = "";
    private Handler handler;
    private DialogLilsReplyList.ClickInterface clickInterface;

    public void setClickInterface(DialogLilsReplyList.ClickInterface clickInterface) {
        this.clickInterface = clickInterface;
    }

    public LilsReplyListAdapter(Context context) {
        this.context = context;
        myId = JunApplication.getMyModel().userId;
        handler = new Handler(Looper.getMainLooper());

    }

    public void setItems(List<LilsReplyModel> lilsReplyModelList) {
        if (lilsReplyModelList == null) return;
        this.lilsReplyModelList.clear();
        this.lilsReplyModelList.addAll(lilsReplyModelList);
        notifyDataSetChanged();
    }

    public void addItems(LilsReplyModel replyModel) {
        lilsReplyModelList.add(replyModel);
    }


    @Override
    public int getCount() {
        return lilsReplyModelList == null ? 0 : lilsReplyModelList.size();
    }

    @Override
    public Object getItem(int position) {
        return lilsReplyModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void blockDialog(int position) {
        AlertDialog.Builder cautionAlert = new AlertDialog.Builder(context);
        View cautionView = LayoutInflater.from(context).inflate(R.layout.dialog_block, null, false);
        cautionAlert.setView(cautionView);
        AppCompatImageView imageView = cautionView.findViewById(R.id.imageView);
        imageView.setVisibility(View.GONE);
        AppCompatTextView textView = cautionView.findViewById(R.id.mainTextView);
        textView.setText("");
        AppCompatTextView subTextView = cautionView.findViewById(R.id.subTextView);
        subTextView.setText("댓글을 차단하시겠습니까?");
        Button reportButton = cautionView.findViewById(R.id.reportButton);
        reportButton.setVisibility(View.GONE);
        Button okButton = cautionView.findViewById(R.id.okButton);
        okButton.setText("차단하기");
        Button cancelButton = cautionView.findViewById(R.id.cancelButton);
        androidx.appcompat.app.AlertDialog alertDialog = cautionAlert.create();
        handler.post(new Runnable() {
            @Override
            public void run() {
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation3;
                alertDialog.show();
            }
        });
        okButton.setOnClickListener(v -> clickInterface.block(lilsReplyModelList.get(position), alertDialog));

        cancelButton.setOnClickListener(v -> alertDialog.dismiss());
    }

    public void reportDialog(int position) {
        AlertDialog.Builder cautionAlert = new AlertDialog.Builder(context);
        View cautionView = LayoutInflater.from(context).inflate(R.layout.dialog_blockreport, null, false);
        cautionAlert.setView(cautionView);
        RadioGroup radioGroup = cautionView.findViewById(R.id.radioGroupLayout);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.firstReportRadioButton:
                        blockContent = "부적절한 사진 및 동영상";
                        break;
                    case R.id.secondReportRadioButton:
                        blockContent = "선정적 / 폭력적 사진 및 동영상";
                        break;
                    case R.id.thirdReportRadioButton:
                        blockContent = "불쾌감을 자극하는 잔인한 사진 및 동영상";
                        break;
                    case R.id.fourthReportRadioButton:
                        blockContent = "기타 부적절한 사진 및 동영상";
                        break;
                }
            }
        });
        Button okButton = cautionView.findViewById(R.id.reportButton);
        Button cancelButton = cautionView.findViewById(R.id.cancelButton);
        AlertDialog alertDialog = cautionAlert.create();
        handler.post(new Runnable() {
            @Override
            public void run() {
                alertDialog.getWindow().setGravity(Gravity.BOTTOM);
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation3;
                alertDialog.show();
            }
        });
        okButton.setOnClickListener(v -> {
            if (blockContent.replace(" ", "").trim().equals("")) {
                Toast.makeText(context, "항목을 선택해주세요.", Toast.LENGTH_SHORT).show();
            } else {

                try {
                    clickInterface.report(lilsReplyModelList.get(position), blockContent, alertDialog);
                } catch (Exception e) {

                }
            }

        });

        cancelButton.setOnClickListener(v -> alertDialog.dismiss());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        LilsReplyModel lilsReplyModel = (LilsReplyModel) getItem(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_lils_reply_item, parent, false);

            ShapeableImageView personImageView = convertView.findViewById(R.id.personImageView);
            AppCompatImageView imoticonImageView = convertView.findViewById(R.id.imoticonImageView);
            AppCompatTextView userIdTextView = convertView.findViewById(R.id.nickNameTextView);
            AppCompatTextView timeTextView = convertView.findViewById(R.id.timeTextView);
            AppCompatTextView replyMessage = convertView.findViewById(R.id.conversationTextView);
            AppCompatTextView regDateTextView = convertView.findViewById(R.id.regDateTextView);
            AppCompatTextView reportTextView = convertView.findViewById(R.id.reportTextView);
            AppCompatTextView blockTextView = convertView.findViewById(R.id.blockTextView);
            LinearLayout blockLinearLayout = convertView.findViewById(R.id.blockLinearLayout);

            viewHolder.reportTextView = reportTextView;
            viewHolder.blockTextView = blockTextView;
            viewHolder.blockLinearLayout = blockLinearLayout;
            viewHolder.regDateTextView = regDateTextView;
            viewHolder.imoticonImageView = imoticonImageView;
            viewHolder.personImageView = personImageView;
            viewHolder.timeTextView = timeTextView;
            viewHolder.userIdTextView = userIdTextView;
            viewHolder.replyMessage = replyMessage;


            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        viewHolder.reportTextView.setVisibility(View.VISIBLE);
        viewHolder.blockTextView.setVisibility(View.VISIBLE);
        viewHolder.imoticonImageView.setVisibility(View.VISIBLE);
        viewHolder.personImageView.setVisibility(View.VISIBLE);
        viewHolder.timeTextView.setVisibility(View.VISIBLE);
        viewHolder.userIdTextView.setVisibility(View.VISIBLE);
        viewHolder.replyMessage.setVisibility(View.VISIBLE);
        viewHolder.regDateTextView.setVisibility(View.GONE);
        viewHolder.blockLinearLayout.setVisibility(View.VISIBLE);

        if (lilsReplyModel.userId.equals(myId)) {
            viewHolder.blockLinearLayout.setVisibility(View.GONE);
        } else {
            viewHolder.blockLinearLayout.setVisibility(View.VISIBLE);
        }


        viewHolder.blockTextView.setOnClickListener(v -> blockDialog(position));

        viewHolder.reportTextView.setOnClickListener(v -> reportDialog(position));

        if (lilsReplyModel.userId.equals("null")) {
            viewHolder.imoticonImageView.setVisibility(View.GONE);
            viewHolder.personImageView.setVisibility(View.GONE);
            viewHolder.timeTextView.setVisibility(View.GONE);
            viewHolder.userIdTextView.setVisibility(View.GONE);
            viewHolder.replyMessage.setVisibility(View.GONE);
            viewHolder.regDateTextView.setVisibility(View.VISIBLE);
            try {
                Date parseDate = originalDateFormat.parse(lilsReplyModel.regDate);
                String dateFormat = mainRegDateFormat.format(parseDate);
                viewHolder.regDateTextView.setText(dateFormat);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
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


            Glide.with(viewHolder.personImageView.getContext())
                    .setDefaultRequestOptions(requestOptions)
                    .load(CommonString.CommonStringInterface.FileBaseUrl + lilsReplyModel.userMainPhoto)
                    .thumbnail(0.3f)
                    .circleCrop()
                    .optionalCircleCrop()
                    .optionalCenterCrop()
                    .into(viewHolder.personImageView);
            viewHolder.imoticonImageView.setVisibility(View.GONE);


            viewHolder.userIdTextView.setText(lilsReplyModel.userId);
            try {
                Date parseDate = originalDateFormat.parse(lilsReplyModel.regDate);
                String dateFormat = changeDateFormat.format(parseDate);
                viewHolder.timeTextView.setText(dateFormat);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            viewHolder.replyMessage.setText(lilsReplyModel.replyMessage);
            if (lilsReplyModel.imoticonUrl != null && !lilsReplyModel.imoticonUrl.equals("null")) {
                viewHolder.imoticonImageView.setVisibility(View.VISIBLE);
                Glide.with(viewHolder.imoticonImageView.getContext())
                        .setDefaultRequestOptions(requestOptions)
                        .load(CommonString.CommonStringInterface.FileBaseUrl + lilsReplyModel.imoticonUrl)
                        .thumbnail(0.3f)
                        .circleCrop()
                        .optionalCircleCrop()
                        .optionalCenterCrop()
                        .into(viewHolder.imoticonImageView);
            } else {
                viewHolder.imoticonImageView.setVisibility(View.GONE);
            }


            if (position == 0) {
                viewHolder.regDateTextView.setVisibility(View.VISIBLE);
                try {
                    Date parseDate = originalDateFormat.parse(lilsReplyModel.regDate);
                    String dateFormat = mainRegDateFormat.format(parseDate);
                    viewHolder.regDateTextView.setText(dateFormat);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else if (position > 0) {
                if (!lilsReplyModelList.get(position).regDate.substring(0, 10)
                        .equals(lilsReplyModelList.get(position - 1).regDate.substring(0, 10))) {
                    viewHolder.regDateTextView.setVisibility(View.VISIBLE);
                    try {
                        Date parseDate = originalDateFormat.parse(lilsReplyModel.regDate);
                        String dateFormat = mainRegDateFormat.format(parseDate);
                        viewHolder.regDateTextView.setText(dateFormat);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return convertView;
    }

    static class ViewHolder {
        ShapeableImageView personImageView;
        AppCompatImageView imoticonImageView;
        AppCompatTextView userIdTextView;
        AppCompatTextView timeTextView;
        AppCompatTextView replyMessage;
        AppCompatTextView regDateTextView;
        AppCompatTextView reportTextView;
        AppCompatTextView blockTextView;
        LinearLayout blockLinearLayout;

    }
}
