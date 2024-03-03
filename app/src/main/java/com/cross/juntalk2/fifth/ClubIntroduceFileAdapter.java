package com.cross.juntalk2.fifth;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.utils.widget.ImageFilterButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cross.juntalk2.R;
import com.cross.juntalk2.fourth.ImageOrVideoAdapter;
import com.cross.juntalk2.fourth.ImageVideoView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ClubIntroduceFileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<String> urlList;
    public ClubIntroduceFileAdapterViewHolder clubIntroduceFileAdapterViewHolder;

    public ClubIntroduceFileAdapter(Context context) {
        this.context = context;

    }

    public void setItems(List<String> urlList) {
        this.urlList = urlList;
    }


    @Override
    public void onAttachedToRecyclerView(@NonNull @NotNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recyclerView.addRecyclerListener(new RecyclerView.RecyclerListener() {
            @Override
            public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
                Log.e("abc", "onViewRecycled");
                if(holder instanceof ClubIntroduceFileAdapterViewHolder){
                    ((ClubIntroduceFileAdapterViewHolder)holder).imageVideoView.releasePlayer();
                }
            }
        });
        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
                Log.e("abc", "attach");
                ClubIntroduceFileAdapterViewHolder holder = (ClubIntroduceFileAdapterViewHolder) view.getTag();
                int position = holder.getAbsoluteAdapterPosition();
                Log.e("abc", "position : " + position);
//                ImageVideoView imageVideoView = new ImageVideoView(context);
//                imageVideoView.setUrl(urlList.get(position));
//                imageVideoView.playVideo();
//                if(imageVideoView.getParent() ==null){
//                    holder.frameLayout.addView(imageVideoView);
//                }
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
                Log.e("abc", "detach");
                ClubIntroduceFileAdapterViewHolder holder = (ClubIntroduceFileAdapterViewHolder) view.getTag();
//                ImageVideoView imageVideoView = (ImageVideoView) holder.frameLayout.getChildAt(0);
//                imageVideoView.releasePlayer();
//                holder.frameLayout.removeAllViews();

            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull @NotNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int firstVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                    ClubIntroduceFileAdapter.ClubIntroduceFileAdapterViewHolder clubIntroduceFileAdapterViewHolder = (ClubIntroduceFileAdapter.ClubIntroduceFileAdapterViewHolder) recyclerView.findViewHolderForAdapterPosition(firstVisibleItem);
                    if (clubIntroduceFileAdapterViewHolder != null) {
                        if (!clubIntroduceFileAdapterViewHolder.imageVideoView.isPlaying()) {
                            clubIntroduceFileAdapterViewHolder.imageVideoView.playVideo();
                            clubIntroduceFileAdapterViewHolder.playButton.setVisibility(View.GONE);
                        }

                    }

                } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    int firstVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                    ClubIntroduceFileAdapter.ClubIntroduceFileAdapterViewHolder clubIntroduceFileAdapterViewHolder = (ClubIntroduceFileAdapter.ClubIntroduceFileAdapterViewHolder) recyclerView.findViewHolderForAdapterPosition(firstVisibleItem);
                    if (clubIntroduceFileAdapterViewHolder != null) {
                        if (clubIntroduceFileAdapterViewHolder.imageVideoView.isPlaying()) {
                            clubIntroduceFileAdapterViewHolder.imageVideoView.pauseVideo();
                            clubIntroduceFileAdapterViewHolder.playButton.setVisibility(View.VISIBLE);
                        }

                    }

                } else if (newState == RecyclerView.SCROLL_STATE_SETTLING) {

                }
            }
        });
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof ClubIntroduceFileAdapterViewHolder) {
            if (((ClubIntroduceFileAdapterViewHolder) holder).imageVideoView != null) {
                if (((ClubIntroduceFileAdapterViewHolder) holder).imageVideoView.isPlaying()) {
                    ((ClubIntroduceFileAdapterViewHolder) holder).imageVideoView.pauseVideo();
                    ((ClubIntroduceFileAdapterViewHolder) holder).playButton.setVisibility(View.VISIBLE);
                }
            }
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(viewType, parent, false);
        clubIntroduceFileAdapterViewHolder = new ClubIntroduceFileAdapterViewHolder(view);
        return clubIntroduceFileAdapterViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String url = urlList.get(position);
        if (holder instanceof ClubIntroduceFileAdapterViewHolder) {
            ((ClubIntroduceFileAdapterViewHolder) holder).imageVideoView.setUrl(url);
            ((ClubIntroduceFileAdapterViewHolder) holder).playButton.setVisibility(View.GONE);
            if (url.contains(".mp4")) {
                if (((ClubIntroduceFileAdapterViewHolder) holder).imageVideoView.isPlaying()) {
                    ((ClubIntroduceFileAdapterViewHolder) holder).playButton.setVisibility(View.GONE);
                } else {
                    ((ClubIntroduceFileAdapterViewHolder) holder).playButton.setVisibility(View.VISIBLE);
                }

            } else {
                ((ClubIntroduceFileAdapterViewHolder) holder).playButton.setVisibility(View.GONE);
            }
            ((ClubIntroduceFileAdapterViewHolder) holder).playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ClubIntroduceFileAdapterViewHolder) holder).imageVideoView.playVideo();
                    ((ClubIntroduceFileAdapterViewHolder) holder).playButton.setVisibility(View.GONE);
                }
            });
            ((ClubIntroduceFileAdapterViewHolder) holder).imageVideoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((ClubIntroduceFileAdapterViewHolder) holder).imageVideoView.isPlaying()) {
                        ((ClubIntroduceFileAdapterViewHolder) holder).imageVideoView.pauseVideo();
                        ((ClubIntroduceFileAdapterViewHolder) holder).playButton.setVisibility(View.VISIBLE);
                    }
                }
            });
            ((ClubIntroduceFileAdapterViewHolder) holder).countPageTextView.setText("( " + (position + 1) + " / " + urlList.size() + " )");
        }
    }

    @Override
    public int getItemCount() {
        return urlList == null ? 0 : urlList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.adapter_clubintroduce_file_item;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ClubIntroduceFileAdapterViewHolder extends RecyclerView.ViewHolder {
        public View parent;
        //        public FrameLayout frameLayout;
        public ImageVideoView imageVideoView;
        private AppCompatTextView countPageTextView;
        public ImageFilterButton playButton;

        public ClubIntroduceFileAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView;
            parent.setTag(this);
//            frameLayout = itemView.findViewById(R.id.frameLayout);
            playButton = itemView.findViewById(R.id.playButton);
            imageVideoView = itemView.findViewById(R.id.imageVideoView);
            countPageTextView = itemView.findViewById(R.id.countPageTextView);
        }
    }
}
