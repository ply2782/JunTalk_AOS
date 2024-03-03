package com.cross.juntalk2.third;

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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.AdapterCommentMeBinding;
import com.cross.juntalk2.databinding.AdapterCommentTimeBinding;
import com.cross.juntalk2.databinding.AdapterCommentYouBinding;
import com.cross.juntalk2.diffutil.CommentDiffUtil;
import com.cross.juntalk2.fourth.DialogCustomCommentListAdapter;
import com.cross.juntalk2.model.CommentModel;
import com.cross.juntalk2.utils.JunApplication;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ResponseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
    private Context context;
    private List<CommentModel> commentModelList = new ArrayList<>();
    private List<CommentModel> commentModelList_copy = new ArrayList<>();
    private SimpleDateFormat todayDate = new SimpleDateFormat("yyyy-MM-dd E요일");
    private String myId;
    private SimpleDateFormat todayStringToDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
    private String blockContent = "";
    private Handler handler;
    private MusicActivity.ClickInterface clickInterface;

    public ResponseAdapter(Context context, List<CommentModel> commentModelList) {
        this.context = context;
        this.commentModelList.addAll(commentModelList);
        myId = JunApplication.getMyModel().userId;
        handler = new Handler(Looper.getMainLooper());
    }

    public void setClickInterface(MusicActivity.ClickInterface clickInterface) {
        this.clickInterface = clickInterface;
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
        okButton.setOnClickListener(v -> clickInterface.block(commentModelList.get(position), alertDialog));

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
                    clickInterface.report(commentModelList.get(position), blockContent, alertDialog);
                } catch (Exception e) {

                }
            }

        });

        cancelButton.setOnClickListener(v -> alertDialog.dismiss());
    }


    public void setItems(CommentModel commentModels) {
        if (commentModels == null) return;
        commentModelList_copy.clear();
        commentModelList_copy.addAll(commentModelList);
        commentModelList.add(commentModels);
        CommentDiffUtil chattingListModel = new CommentDiffUtil(commentModelList_copy, commentModelList);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(chattingListModel);
        result.dispatchUpdatesTo(this);
    }


    @Override
    public int getItemViewType(int position) {
        if (commentModelList.get(position).dateDiff == 1) {
            return R.layout.adapter_comment_time;
        } else if (commentModelList.get(position).userId.equals(myId)) {
            return R.layout.adapter_comment_me;

        } else {
            return R.layout.adapter_comment_you;

        }


    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case R.layout.adapter_comment_you:
                AdapterCommentYouBinding adapterCommentYouBinding = AdapterCommentYouBinding.inflate(inflater, parent, false);
                return new ResponseViewHolder_You(adapterCommentYouBinding);
            case R.layout.adapter_comment_me:
                AdapterCommentMeBinding adapterCommentMeBinding = AdapterCommentMeBinding.inflate(inflater, parent, false);
                return new ResponseViewHolder_Me(adapterCommentMeBinding);
            case R.layout.adapter_comment_time:
                AdapterCommentTimeBinding adapterCommentTimeBinding = AdapterCommentTimeBinding.inflate(inflater, parent, false);
                return new ResponseViewHolder_Time(adapterCommentTimeBinding);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        CommentModel commentModel = commentModelList.get(position);
        if (holder instanceof ResponseViewHolder_Me) {
            ((ResponseViewHolder_Me) holder).onBind(commentModel);

            ((ResponseViewHolder_Me) holder).binding.regDateTextView.setVisibility(View.GONE);
            if (position == 0) {
                ((ResponseViewHolder_Me) holder).binding.regDateTextView.setVisibility(View.VISIBLE);
            } else if (position > 0) {
                if (!commentModelList.get(position - 1).userCommentRegDate.substring(0, 10)
                        .equals(commentModelList.get(position).userCommentRegDate.substring(0, 10))) {
                    ((ResponseViewHolder_Me) holder).binding.regDateTextView.setVisibility(View.VISIBLE);
                } else {
                    ((ResponseViewHolder_Me) holder).binding.regDateTextView.setVisibility(View.GONE);
                }
            }

        } else if (holder instanceof ResponseViewHolder_You) {
            ((ResponseViewHolder_You) holder).onBind(commentModel);
            ((ResponseViewHolder_You) holder).binding.regDateTextView.setVisibility(View.GONE);
            if (position == 0) {
                ((ResponseViewHolder_You) holder).binding.regDateTextView.setVisibility(View.VISIBLE);
            } else if (position > 0) {
                if (!commentModelList.get(position - 1).userCommentRegDate.substring(0, 10)
                        .equals(commentModelList.get(position).userCommentRegDate.substring(0, 10))) {
                    ((ResponseViewHolder_You) holder).binding.regDateTextView.setVisibility(View.VISIBLE);
                } else {
                    ((ResponseViewHolder_You) holder).binding.regDateTextView.setVisibility(View.GONE);
                }
            }

            ((ResponseViewHolder_You) holder).binding.reportTextView.setOnClickListener(v -> reportDialog(holder.getAbsoluteAdapterPosition()));

            ((ResponseViewHolder_You) holder).binding.blockTextView.setOnClickListener(v -> blockDialog(holder.getAbsoluteAdapterPosition()));

        } else if (holder instanceof ResponseViewHolder_Time) {
            ((ResponseViewHolder_Time) holder).onBind(todayDate.format(new Date().getTime()));
        }
    }

    @Override
    public int getItemCount() {
        return commentModelList == null ? 0 : commentModelList.size();
    }

    public class ResponseViewHolder_Me extends RecyclerView.ViewHolder {
        private AdapterCommentMeBinding binding;

        public ResponseViewHolder_Me(@NonNull @NotNull AdapterCommentMeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void onBind(CommentModel commentModel) {
            try {
                binding.setCommentModel(commentModel);
                binding.setToday(todayDate.format(todayStringToDate.parse(commentModel.userCommentRegDate)));
                binding.executePendingBindings();
                if (commentModel.userImoticonUrl != null && commentModel.userImoticonUrl.equals("null")) {
                    binding.imoticonImageView.setVisibility(View.GONE);
                }

            } catch (ParseException e) {

            } catch (Exception e) {

            }
        }
    }

    public class ResponseViewHolder_You extends RecyclerView.ViewHolder {
        private AdapterCommentYouBinding binding;

        public ResponseViewHolder_You(@NonNull @NotNull AdapterCommentYouBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void onBind(CommentModel commentModel) {
            try {
                binding.setCommentModel(commentModel);
                binding.setToday(todayDate.format(todayStringToDate.parse(commentModel.userCommentRegDate)));
                binding.executePendingBindings();
                if (commentModel.userImoticonUrl != null && commentModel.userImoticonUrl.equals("null")) {
                    binding.imoticonImageView.setVisibility(View.GONE);
                }

            } catch (ParseException e) {

            } catch (Exception e) {

            }


        }
    }

    public class ResponseViewHolder_Time extends RecyclerView.ViewHolder {
        private AdapterCommentTimeBinding adapterCommentTimeBinding;

        public ResponseViewHolder_Time(@NonNull @NotNull AdapterCommentTimeBinding adapterCommentTimeBinding) {
            super(adapterCommentTimeBinding.getRoot());
            this.adapterCommentTimeBinding = adapterCommentTimeBinding;
        }

        public void onBind(String regDate) {
            adapterCommentTimeBinding.setRegDate(regDate);
            adapterCommentTimeBinding.executePendingBindings();
        }
    }
}
