package com.cross.juntalk2.fourth;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.DialogCustomadapterItemMeBinding;
import com.cross.juntalk2.databinding.DialogCustomadapterItemTimeBinding;
import com.cross.juntalk2.databinding.DialogCustomadapterItemYouBinding;
import com.cross.juntalk2.diffutil.BulletinCommentDillUtil;
import com.cross.juntalk2.model.BulletinCommentModel;
import com.cross.juntalk2.utils.JunApplication;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DialogCustomCommentListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<BulletinCommentModel> bulletinCommentModels = new ArrayList<>();
    private List<BulletinCommentModel> bulletinCommentModels_copy = new ArrayList<>();
    private int user_Index;
    private SimpleDateFormat todayStringToDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
    private SimpleDateFormat todayDateToString = new SimpleDateFormat("aa HH:mm", Locale.KOREA);
    private SimpleDateFormat todayDate = new SimpleDateFormat("yyyy-MM-dd E요일", Locale.KOREA);
    private Handler handler;
    private String blockContent = "";
    private DialogCustomCommentList.CommentListClickInterface commentListClickInterface;

    public void setCommentListClickInterface(DialogCustomCommentList.CommentListClickInterface commentListClickInterface) {
        this.commentListClickInterface = commentListClickInterface;
    }

    public DialogCustomCommentListAdapter(Context context) {
        this.context = context;
        user_Index = JunApplication.getMyModel().user_Index;
        handler = new Handler(Looper.getMainLooper());
    }

    public void setItems(List<BulletinCommentModel> bulletinCommentModels) {
        if (bulletinCommentModels == null) return;
        this.bulletinCommentModels.addAll(bulletinCommentModels_copy);
        bulletinCommentModels_copy.addAll(bulletinCommentModels);
        BulletinCommentDillUtil bulletinCommentDillUtil = new BulletinCommentDillUtil(this.bulletinCommentModels, bulletinCommentModels_copy);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(bulletinCommentDillUtil);
        result.dispatchUpdatesTo(this);
        this.bulletinCommentModels.clear();
        this.bulletinCommentModels.addAll(bulletinCommentModels_copy);
        bulletinCommentModels_copy.clear();

    }

    @Override
    public int getItemViewType(int position) {
        if (bulletinCommentModels.get(position).viewType == 1) {
            return R.layout.dialog_customadapter_item_time;
        } else if (bulletinCommentModels.get(position).user_Index == user_Index) {
            return R.layout.dialog_customadapter_item_me;
        } else {
            return R.layout.dialog_customadapter_item_you;
        }
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
        AlertDialog alertDialog = cautionAlert.create();
        handler.post(() -> {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation3;
            alertDialog.show();
        });
        okButton.setOnClickListener(v -> {
            try {
                commentListClickInterface.block(bulletinCommentModels.get(position), "null", alertDialog);

            } catch (Exception e) {

            }
        });

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
                    commentListClickInterface.report(bulletinCommentModels.get(position), blockContent, alertDialog);
                } catch (Exception e) {

                }
            }

        });

        cancelButton.setOnClickListener(v -> alertDialog.dismiss());
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        switch (viewType) {
            case R.layout.dialog_customadapter_item_me:
                DialogCustomadapterItemMeBinding meBinding = DialogCustomadapterItemMeBinding.inflate(inflater, parent, false);
                return new DialogCustomAdapter_Me(meBinding);
            case R.layout.dialog_customadapter_item_you:
                DialogCustomadapterItemYouBinding youBinding = DialogCustomadapterItemYouBinding.inflate(inflater, parent, false);
                return new DialogCustomAdapter_You(youBinding);
            case R.layout.dialog_customadapter_item_time:
                DialogCustomadapterItemTimeBinding timeBinding = DialogCustomadapterItemTimeBinding.inflate(inflater, parent, false);
                return new DialogCustomAdapter_Time(timeBinding);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        BulletinCommentModel bulletinCommentModel = bulletinCommentModels.get(holder.getBindingAdapterPosition());

        if (holder instanceof DialogCustomAdapter_Me) {
            ((DialogCustomAdapter_Me) holder).onBind(bulletinCommentModel);

            ((DialogCustomAdapter_Me) holder).meBinding.regDateTextView.setVisibility(View.GONE);
            if (position == 0) {
                ((DialogCustomAdapter_Me) holder).meBinding.regDateTextView.setVisibility(View.VISIBLE);
            } else if (position > 0) {
                if (!bulletinCommentModels.get(holder.getBindingAdapterPosition() - 1).userCommentRegDate.substring(0, 10)
                        .equals(bulletinCommentModels.get(holder.getBindingAdapterPosition()).userCommentRegDate.substring(0, 10))) {
                    ((DialogCustomAdapter_Me) holder).meBinding.regDateTextView.setVisibility(View.VISIBLE);
                } else {
                    ((DialogCustomAdapter_Me) holder).meBinding.regDateTextView.setVisibility(View.GONE);
                }
            }


        } else if (holder instanceof DialogCustomAdapter_You) {

            ((DialogCustomAdapter_You) holder).onBind(bulletinCommentModel);
            ((DialogCustomAdapter_You) holder).youBinding.regDateTextView.setVisibility(View.GONE);
            if (position == 0) {
                ((DialogCustomAdapter_You) holder).youBinding.regDateTextView.setVisibility(View.VISIBLE);
            } else if (position > 0) {
                if (!bulletinCommentModels.get(holder.getBindingAdapterPosition() - 1).userCommentRegDate.substring(0, 10)
                        .equals(bulletinCommentModels.get(holder.getBindingAdapterPosition()).userCommentRegDate.substring(0, 10))) {
                    ((DialogCustomAdapter_You) holder).youBinding.regDateTextView.setVisibility(View.VISIBLE);
                } else {
                    ((DialogCustomAdapter_You) holder).youBinding.regDateTextView.setVisibility(View.GONE);
                }
            }

            ((DialogCustomAdapter_You) holder).youBinding.blockTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    blockDialog(holder.getBindingAdapterPosition());
                }
            });

            ((DialogCustomAdapter_You) holder).youBinding.reportTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reportDialog(holder.getBindingAdapterPosition());
                }
            });

        } else if (holder instanceof DialogCustomAdapter_Time) {
            ((DialogCustomAdapter_Time) holder).onBind(bulletinCommentModel);
        }
    }


    @Override
    public int getItemCount() {
        return bulletinCommentModels == null ? 0 : bulletinCommentModels.size();
    }

    public class DialogCustomAdapter_Me extends RecyclerView.ViewHolder {
        private DialogCustomadapterItemMeBinding meBinding;

        public DialogCustomAdapter_Me(@NonNull @NotNull DialogCustomadapterItemMeBinding meBinding) {
            super(meBinding.getRoot());
            this.meBinding = meBinding;
        }

        public void onBind(BulletinCommentModel bulletinCommentModel) {
            try {
                meBinding.setBulletinCommentModel(bulletinCommentModel);
                meBinding.setToday(todayDate.format(todayStringToDate.parse(bulletinCommentModel.userCommentRegDate)));
                meBinding.executePendingBindings();
            } catch (ParseException e) {
            } catch (Exception e) {

            }


        }
    }

    public class DialogCustomAdapter_You extends RecyclerView.ViewHolder {
        private DialogCustomadapterItemYouBinding youBinding;

        public DialogCustomAdapter_You(@NonNull @NotNull DialogCustomadapterItemYouBinding youBinding) {
            super(youBinding.getRoot());
            this.youBinding = youBinding;
        }

        public void onBind(BulletinCommentModel bulletinCommentModel) {

            try {
                youBinding.setBulletinCommentModel(bulletinCommentModel);
                youBinding.setToday(todayDate.format(todayStringToDate.parse(bulletinCommentModel.userCommentRegDate)));
                youBinding.executePendingBindings();
            } catch (ParseException e) {
            } catch (Exception e) {

            }
        }
    }

    public class DialogCustomAdapter_Time extends RecyclerView.ViewHolder {
        private DialogCustomadapterItemTimeBinding timeBinding;

        public DialogCustomAdapter_Time(@NonNull @NotNull DialogCustomadapterItemTimeBinding timeBinding) {
            super(timeBinding.getRoot());
            this.timeBinding = timeBinding;
        }

        public void onBind(BulletinCommentModel bulletinCommentModel) {
            try {
                bulletinCommentModel.userCommentRegDate = todayDateToString.format(todayStringToDate.parse(bulletinCommentModel.userCommentRegDate));
                timeBinding.setBulletinCommentModel(bulletinCommentModel);
                timeBinding.executePendingBindings();
            } catch (ParseException e) {
            } catch (Exception e) {

            }

        }
    }
}
