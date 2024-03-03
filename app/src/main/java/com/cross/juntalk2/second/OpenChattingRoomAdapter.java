package com.cross.juntalk2.second;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.AdapterChattingEmptyBinding;
import com.cross.juntalk2.databinding.AdapterChattingEnterExitBinding;
import com.cross.juntalk2.databinding.AdapterChattingMeBinding;
import com.cross.juntalk2.databinding.AdapterChattingYouBinding;
import com.cross.juntalk2.databinding.AdapterOpenChattingMeBinding;
import com.cross.juntalk2.databinding.AdapterOpenChattingYouBinding;
import com.cross.juntalk2.databinding.ChattingShowDateBinding;
import com.cross.juntalk2.model.ChattingModel;
import com.cross.juntalk2.model.MyModel;
import com.cross.juntalk2.utils.JunApplication;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OpenChattingRoomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private MyModel myModel;
    private List<ChattingModel> chattingModelList = new ArrayList<>();
    private SimpleDateFormat todayDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat todayDateFormatToDate = new SimpleDateFormat("yyyy-MM-dd");
    private List<Map<String, Object>> room_JoinPeopleListMap = new ArrayList<>();
    private Activity activity;
    private SimpleDateFormat stringToDate = new SimpleDateFormat("yyyy-MM-dd E요일", Locale.KOREA);
    private SimpleDateFormat todayStringToDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);

    public OpenChattingRoomAdapter(Context context, Activity activity) {
        this.context = context;
        myModel = JunApplication.getMyModel();
        this.activity = activity;
    }


    public void settingConversation(List<ChattingModel> chattingModels) {
        chattingModelList.clear();
        chattingModelList.addAll(chattingModels);
    }

    public void setItems(ChattingModel chattingModel) {
        if (chattingModel == null) return;
        if (chattingModel.userMessageType.equals(ChattingModel.MessageType.ENTER)) {
            room_JoinPeopleListMap.clear();
            if (chattingModel.room_JoinPeopleImage != null) {
                room_JoinPeopleListMap.addAll(chattingModel.room_JoinPeopleImage);
            }
        }
        this.chattingModelList.add(chattingModel);
        notifyDataSetChanged();

        /*chattingModelList_copy.clear();
        chattingModelList_copy.addAll(chattingModelList);
        chattingModelList.add(chattingModel);
        ChattingDiffUtil chattingListModel = new ChattingDiffUtil(chattingModelList_copy, chattingModelList);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(chattingListModel);
        result.dispatchUpdatesTo(this);*/


    }

    @Override
    public int getItemViewType(int position) {
        if (chattingModelList.get(position).userMessageType.equals(ChattingModel.MessageType.ENTER) &&
                chattingModelList.get(position).userState.equals(ChattingModel.UserState.IN)) {
            return R.layout.adapter_chatting_enter_exit;
        } else if (chattingModelList.get(position).userMessageType.equals(ChattingModel.MessageType.EXIT) &&
                chattingModelList.get(position).userState.equals(ChattingModel.UserState.OUT)) {
            return R.layout.adapter_chatting_enter_exit;
        } else if (chattingModelList.get(position).userMessageType.equals(ChattingModel.MessageType.CONVERSATION)) {
            if (chattingModelList.get(position).userId.equals(myModel.userId)) {
                return R.layout.adapter_open_chatting_you;
            } else {
                return R.layout.adapter_open_chatting_me;
            }
        }


        return -1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return chattingModelList == null ? 0 : chattingModelList.size();
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        switch (viewType) {

            case R.layout.adapter_open_chatting_me:
                AdapterOpenChattingMeBinding meItemBinding = AdapterOpenChattingMeBinding.inflate(inflater, parent, false);
                meItemBinding.setChattingRoomAdapter(this);
                return new ChattingMeViewHolder(meItemBinding);
            case R.layout.adapter_open_chatting_you:
                AdapterOpenChattingYouBinding youItemBinding = AdapterOpenChattingYouBinding.inflate(inflater, parent, false);
                youItemBinding.setChattingRoomAdapter(this);
                return new ChattingYouViewHolder(youItemBinding);
            case R.layout.adapter_chatting_enter_exit:
                AdapterChattingEnterExitBinding enterExitBinding = AdapterChattingEnterExitBinding.inflate(inflater, parent, false);
                return new ChattingEnterExitViewHolder(enterExitBinding);
            case R.layout.chatting_show_date:
                ChattingShowDateBinding chattingShowDateBinding = ChattingShowDateBinding.inflate(inflater, parent, false);
                return new ChattingShowTodayViewHolder(chattingShowDateBinding);
            case R.layout.adapter_chatting_empty:
                AdapterChattingEmptyBinding adapterChattingEmptyBinding = AdapterChattingEmptyBinding.inflate(inflater, parent, false);
                return new ChattingEmptyViewHolder(adapterChattingEmptyBinding);
        }
        return null;
    }


    public void imageViewOnClick(View view, String imageUrl) {
        Log.e("abc", "intent");
        Intent intent = new Intent(context, PictureActivity.class);
        intent.putExtra("imageUrl", imageUrl);
        context.startActivity(intent);
    }


    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ChattingMeViewHolder) {

            ((ChattingMeViewHolder) holder).onBind(chattingModelList.get(position));

            //todo : 시간 프로필 그리고 닉네임 분기처리
            //todo : 초기화 세팅
            ((ChattingMeViewHolder) holder).meItemBinding.unReadCountTextView.setVisibility(View.VISIBLE);
            ((ChattingMeViewHolder) holder).meItemBinding.timeTextView.setVisibility(View.VISIBLE);
            ((ChattingMeViewHolder) holder).meItemBinding.personImageView.setVisibility(View.VISIBLE);
            ((ChattingMeViewHolder) holder).meItemBinding.nickNameTextView.setVisibility(View.VISIBLE);
            ((ChattingMeViewHolder) holder).meItemBinding.regDateTextView.setVisibility(View.GONE);

            if (position == 0) {
                ((ChattingMeViewHolder) holder).meItemBinding.regDateTextView.setVisibility(View.VISIBLE);
            }

            if (position < chattingModelList.size() - 1) {
                //todo : 우선 포지션 값은 0보다 크기
                if (position > 0) {
                    //todo : 현재 포지션과 그 전 포지션 비교해서 메시지 타입이 Conversation 인지 아닌지 판단
                    if (chattingModelList.get(position - 1).userMessageType.equals(ChattingModel.MessageType.CONVERSATION)) {
                        //todo : conversation 이면서 지금 포지션과 전 포지션 아이디가 상대방일 경우
                        if (chattingModelList.get(position - 1).userId.equals(chattingModelList.get(position).userId)) {
                            //todo : conversation 이면서 지금 포지션과 전 포지션 시간이 같을 경우
                            if (chattingModelList.get(position - 1).userConversationTime.equals(chattingModelList.get(position).userConversationTime)) {

                                //todo : conversation 이면서 지금 포지션과 전 포지션 시간이 같으면서 그 앞에 포지션도 시간이 같은지 판단
                                if (chattingModelList.get(position + 1).userConversationTime.equals(chattingModelList.get(position).userConversationTime)) {

                                    //todo : 유저 아이디가 현재포지션과 다음 포지션 비교
                                    if (chattingModelList.get(position + 1).userId.equals(chattingModelList.get(position).userId)) {
                                        if (chattingModelList.get(position + 1).userMessageType.equals(chattingModelList.get(position).userMessageType)) {
                                            ((ChattingMeViewHolder) holder).meItemBinding.timeTextView.setVisibility(View.INVISIBLE);
                                            ((ChattingMeViewHolder) holder).meItemBinding.unReadCountTextView.setVisibility(View.INVISIBLE);
                                        } else {
                                            ((ChattingMeViewHolder) holder).meItemBinding.timeTextView.setVisibility(View.VISIBLE);
                                            ((ChattingMeViewHolder) holder).meItemBinding.unReadCountTextView.setVisibility(View.VISIBLE);
                                        }

                                        ((ChattingMeViewHolder) holder).meItemBinding.personImageView.setVisibility(View.INVISIBLE);
                                        ((ChattingMeViewHolder) holder).meItemBinding.nickNameTextView.setVisibility(View.GONE);
                                    } else {
                                        ((ChattingMeViewHolder) holder).meItemBinding.unReadCountTextView.setVisibility(View.VISIBLE);
                                        ((ChattingMeViewHolder) holder).meItemBinding.timeTextView.setVisibility(View.VISIBLE);
                                        ((ChattingMeViewHolder) holder).meItemBinding.personImageView.setVisibility(View.INVISIBLE);
                                        ((ChattingMeViewHolder) holder).meItemBinding.nickNameTextView.setVisibility(View.GONE);
                                    }


                                } else {
                                    ((ChattingMeViewHolder) holder).meItemBinding.unReadCountTextView.setVisibility(View.VISIBLE);
                                    ((ChattingMeViewHolder) holder).meItemBinding.timeTextView.setVisibility(View.VISIBLE);
                                    ((ChattingMeViewHolder) holder).meItemBinding.personImageView.setVisibility(View.INVISIBLE);
                                    ((ChattingMeViewHolder) holder).meItemBinding.nickNameTextView.setVisibility(View.GONE);
                                }


                            } else {


                                //todo : 전 포지션이랑 시간이 다를 경우
                                //todo : conversation 이면서 지금 포지션과 전 포지션 시간이 같으면서 그 앞에 포지션도 시간이 같은지 판단
                                if (chattingModelList.get(position + 1).userConversationTime.equals(chattingModelList.get(position).userConversationTime)) {
                                    if (chattingModelList.get(position + 1).userId.equals(chattingModelList.get(position).userId)) {
                                        ((ChattingMeViewHolder) holder).meItemBinding.timeTextView.setVisibility(View.INVISIBLE);
                                        ((ChattingMeViewHolder) holder).meItemBinding.unReadCountTextView.setVisibility(View.INVISIBLE);
                                    } else {
                                        ((ChattingMeViewHolder) holder).meItemBinding.timeTextView.setVisibility(View.VISIBLE);
                                        ((ChattingMeViewHolder) holder).meItemBinding.unReadCountTextView.setVisibility(View.VISIBLE);
                                    }

                                } else {
                                    ((ChattingMeViewHolder) holder).meItemBinding.timeTextView.setVisibility(View.VISIBLE);
                                    ((ChattingMeViewHolder) holder).meItemBinding.unReadCountTextView.setVisibility(View.VISIBLE);
                                }
                            }


                        } else {


                            //todo : conversation 이 아니면서 지금 포지션과 다음 포지션 아이디가 다를 경우
                            //todo : 유저 아이디가 현재포지션과 다음 포지션 비교
                            if (chattingModelList.get(position + 1).userId.equals(chattingModelList.get(position).userId)) {

                                if (chattingModelList.get(position + 1).userConversationTime.equals(chattingModelList.get(position).userConversationTime)) {
                                    ((ChattingMeViewHolder) holder).meItemBinding.unReadCountTextView.setVisibility(View.INVISIBLE);
                                    ((ChattingMeViewHolder) holder).meItemBinding.timeTextView.setVisibility(View.INVISIBLE);

                                } else {
                                    ((ChattingMeViewHolder) holder).meItemBinding.unReadCountTextView.setVisibility(View.VISIBLE);
                                    ((ChattingMeViewHolder) holder).meItemBinding.timeTextView.setVisibility(View.VISIBLE);
                                }

                            } else {
                                ((ChattingMeViewHolder) holder).meItemBinding.unReadCountTextView.setVisibility(View.VISIBLE);
                                ((ChattingMeViewHolder) holder).meItemBinding.timeTextView.setVisibility(View.VISIBLE);
                            }


                        }


                    } else {


                        //todo : 현재 포지션과 그 전 포지션 비교해서 메시지 타입이 Conversation 인지 아닌지 판단
                        //todo : conversation 이 아니면서 지금 포지션과 전 포지션 시간이 같으면서 그 앞에 포지션도 시간이 같은지 판단
                        //todo : 시간만 없애게 보여주
                        if (chattingModelList.get(position + 1).userId.equals(chattingModelList.get(position).userId)) {
                            if (chattingModelList.get(position + 1).userConversationTime.equals(chattingModelList.get(position).userConversationTime)) {

                                if (chattingModelList.get(position + 1).userId.equals(chattingModelList.get(position).userId)) {
                                    ((ChattingMeViewHolder) holder).meItemBinding.unReadCountTextView.setVisibility(View.INVISIBLE);
                                    ((ChattingMeViewHolder) holder).meItemBinding.timeTextView.setVisibility(View.INVISIBLE);
                                } else {
                                    ((ChattingMeViewHolder) holder).meItemBinding.unReadCountTextView.setVisibility(View.VISIBLE);
                                    ((ChattingMeViewHolder) holder).meItemBinding.timeTextView.setVisibility(View.VISIBLE);
                                }

                            } else {
                                ((ChattingMeViewHolder) holder).meItemBinding.unReadCountTextView.setVisibility(View.VISIBLE);
                                ((ChattingMeViewHolder) holder).meItemBinding.timeTextView.setVisibility(View.VISIBLE);
                            }
                        } else {
                            ((ChattingMeViewHolder) holder).meItemBinding.unReadCountTextView.setVisibility(View.VISIBLE);
                            ((ChattingMeViewHolder) holder).meItemBinding.timeTextView.setVisibility(View.VISIBLE);
                        }
                    }


                    if (position > 0) {
                        if (!chattingModelList.get(position).currentActualTime.substring(0, 10)
                                .equals(chattingModelList.get(position - 1).currentActualTime.substring(0, 10))) {
                            ((ChattingMeViewHolder) holder).meItemBinding.regDateTextView.setVisibility(View.VISIBLE);
                        } else {
                            ((ChattingMeViewHolder) holder).meItemBinding.regDateTextView.setVisibility(View.GONE);
                        }
                    }


                } else {

                    if (position == 0) {
                        if(chattingModelList.get(position).userId
                                .equals(chattingModelList.get(position+1).userId)){
                            ((ChattingMeViewHolder) holder).meItemBinding.unReadCountTextView.setVisibility(View.INVISIBLE);
                            ((ChattingMeViewHolder) holder).meItemBinding.timeTextView.setVisibility(View.INVISIBLE);
                        }else{
                            ((ChattingMeViewHolder) holder).meItemBinding.unReadCountTextView.setVisibility(View.VISIBLE);
                            ((ChattingMeViewHolder) holder).meItemBinding.timeTextView.setVisibility(View.VISIBLE);
                        }

                    }
                }


                //todo : 현재 포지션 일떄
            } else if (position == chattingModelList.size() - 1) {
                //todo : 우선 포지션 값은 0보다 크기
                if (position > 0) {
                    //todo : 현재 포지션과 그 전 포지션 비교해서 메시지 타입이 Conversation 인지 아닌지 판단
                    if (chattingModelList.get(position - 1).userMessageType.equals(ChattingModel.MessageType.CONVERSATION)) {
                        //todo : conversation 이면서 지금 포지션과 전 포지션 아이디가 내 아이딜 경우
                        if (chattingModelList.get(position - 1).userId.equals(chattingModelList.get(position).userId)) {
                            //todo : conversation 이면서 지금 포지션과 전 포지션 시간이 같을 경우
                            if (chattingModelList.get(position - 1).userConversationTime.equals(chattingModelList.get(position).userConversationTime)) {
                                ((ChattingMeViewHolder) holder).meItemBinding.timeTextView.setVisibility(View.VISIBLE);
                                ((ChattingMeViewHolder) holder).meItemBinding.personImageView.setVisibility(View.INVISIBLE);
                                ((ChattingMeViewHolder) holder).meItemBinding.nickNameTextView.setVisibility(View.GONE);
                            } else {
                                ((ChattingMeViewHolder) holder).meItemBinding.timeTextView.setVisibility(View.VISIBLE);
                                ((ChattingMeViewHolder) holder).meItemBinding.personImageView.setVisibility(View.VISIBLE);
                                ((ChattingMeViewHolder) holder).meItemBinding.nickNameTextView.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }

                if (position > 0) {
                    if (!chattingModelList.get(position).currentActualTime.substring(0, 10)
                            .equals(chattingModelList.get(position - 1).currentActualTime.substring(0, 10))) {
                        ((ChattingMeViewHolder) holder).meItemBinding.regDateTextView.setVisibility(View.VISIBLE);
                    } else {
                        ((ChattingMeViewHolder) holder).meItemBinding.regDateTextView.setVisibility(View.GONE);
                    }
                }

            }


        } else if (holder instanceof ChattingYouViewHolder) {


            ((ChattingYouViewHolder) holder).onBind(myModel, chattingModelList.get(position));
            //todo : 시간 프로필 그리고 닉네임 분기처리
            //todo : 초기화 세팅

            ((ChattingYouViewHolder) holder).youItemBinding.timeTextView.setVisibility(View.VISIBLE);
            /*((ChattingYouViewHolder) holder).youItemBinding.personImageView.setVisibility(View.VISIBLE);*/
            /*((ChattingYouViewHolder) holder).youItemBinding.nickNameTextView.setVisibility(View.VISIBLE);*/
            ((ChattingYouViewHolder) holder).youItemBinding.unReadCountTextView.setVisibility(View.VISIBLE);
            ((ChattingYouViewHolder) holder).youItemBinding.regDateTextView.setVisibility(View.GONE);

            if (position == 0) {
                ((ChattingYouViewHolder) holder).youItemBinding.regDateTextView.setVisibility(View.VISIBLE);
            }

            if (position < chattingModelList.size() - 1) {
                //todo : 우선 포지션 값은 0보다 크기
                if (position > 0) {
                    //todo : 현재 포지션과 그 전 포지션 비교해서 메시지 타입이 Conversation 인지 아닌지 판단
                    if (chattingModelList.get(position - 1).userMessageType.equals(ChattingModel.MessageType.CONVERSATION)) {
                        //todo : conversation 이면서 지금 포지션과 전 포지션 아이디가 내 아이딜 경우
                        if (chattingModelList.get(position - 1).userId.equals(myModel.userId)) {

                            //todo : conversation 이면서 지금 포지션과 전 포지션 시간이 같을 경우
                            if (chattingModelList.get(position - 1).userConversationTime.equals(chattingModelList.get(position).userConversationTime)) {

                                //todo : conversation 이면서 지금 포지션과 전 포지션 시간이 같으면서 그 앞에 포지션도 시간이 같은지 판단
                                if (chattingModelList.get(position + 1).userConversationTime.equals(chattingModelList.get(position).userConversationTime)) {


                                    //todo : 지금 포지션과 그 다음 포지션 에서 채팅하는 사람이 나 일 경우와 아닌경우 분기처리
                                    if (chattingModelList.get(position + 1).userId.equals(myModel.userId)) {

                                        /*((ChattingYouViewHolder) holder).youItemBinding.personImageView.setVisibility(View.INVISIBLE);*/
                                        ((ChattingYouViewHolder) holder).youItemBinding.nickNameTextView.setVisibility(View.GONE);
                                        ((ChattingYouViewHolder) holder).youItemBinding.timeTextView.setVisibility(View.INVISIBLE);
                                        ((ChattingYouViewHolder) holder).youItemBinding.unReadCountTextView.setVisibility(View.INVISIBLE);

                                    } else {

                                        ((ChattingYouViewHolder) holder).youItemBinding.unReadCountTextView.setVisibility(View.VISIBLE);
                                        ((ChattingYouViewHolder) holder).youItemBinding.timeTextView.setVisibility(View.VISIBLE);
                                        /*((ChattingYouViewHolder) holder).youItemBinding.personImageView.setVisibility(View.INVISIBLE);*/
                                        ((ChattingYouViewHolder) holder).youItemBinding.nickNameTextView.setVisibility(View.GONE);
                                    }


                                } else {


                                    ((ChattingYouViewHolder) holder).youItemBinding.unReadCountTextView.setVisibility(View.VISIBLE);
                                    ((ChattingYouViewHolder) holder).youItemBinding.timeTextView.setVisibility(View.VISIBLE);
                                    /*((ChattingYouViewHolder) holder).youItemBinding.personImageView.setVisibility(View.INVISIBLE);*/
                                    ((ChattingYouViewHolder) holder).youItemBinding.nickNameTextView.setVisibility(View.GONE);
                                }


                            } else {
                                //todo : 전 포지션이랑 현재포지션이랑 다른 시간대일 경우
                                //todo : conversation 이면서 지금 포지션과 전 포지션 시간이 같으면서 그 앞에 포지션도 시간이 같은지 판단
                                if (chattingModelList.get(position + 1).userConversationTime.equals(chattingModelList.get(position).userConversationTime)) {

                                    if (chattingModelList.get(position + 1).userId.equals(chattingModelList.get(position).userId)) {
                                        ((ChattingYouViewHolder) holder).youItemBinding.timeTextView.setVisibility(View.INVISIBLE);
                                        ((ChattingYouViewHolder) holder).youItemBinding.unReadCountTextView.setVisibility(View.INVISIBLE);
                                    } else {
                                        ((ChattingYouViewHolder) holder).youItemBinding.timeTextView.setVisibility(View.VISIBLE);
                                        ((ChattingYouViewHolder) holder).youItemBinding.unReadCountTextView.setVisibility(View.VISIBLE);
                                    }


                                } else {
                                    ((ChattingYouViewHolder) holder).youItemBinding.timeTextView.setVisibility(View.VISIBLE);
                                    ((ChattingYouViewHolder) holder).youItemBinding.unReadCountTextView.setVisibility(View.VISIBLE);
                                }
                            }

                        } else {

                            //todo : conversation 이 아니면서 지금 포지션과 다음 포지션 아이디가 다를 경우
                            //todo : 유저 아이디가 현재포지션과 다음 포지션 비교
                            if (chattingModelList.get(position + 1).userId.equals(myModel.userId)) {
                                if (chattingModelList.get(position + 1).userConversationTime.equals(chattingModelList.get(position).userConversationTime)) {
                                    ((ChattingYouViewHolder) holder).youItemBinding.timeTextView.setVisibility(View.INVISIBLE);
                                    ((ChattingYouViewHolder) holder).youItemBinding.unReadCountTextView.setVisibility(View.INVISIBLE);
                                } else {
                                    ((ChattingYouViewHolder) holder).youItemBinding.timeTextView.setVisibility(View.VISIBLE);
                                    ((ChattingYouViewHolder) holder).youItemBinding.unReadCountTextView.setVisibility(View.VISIBLE);
                                }
                            } else {
                                ((ChattingYouViewHolder) holder).youItemBinding.timeTextView.setVisibility(View.VISIBLE);
                                ((ChattingYouViewHolder) holder).youItemBinding.unReadCountTextView.setVisibility(View.VISIBLE);
                            }

                        }


                    } else {

                        //todo : 대화가 아닐경우 그 다음포지션 시간대로 분기처리
                        //todo : conversation 이 아니면서 지금 포지션과 다음 포지션 아이디가 다를 경우
                        //todo : 유저 아이디가 현재포지션과 다음 포지션 비교
                        if (chattingModelList.get(position + 1).userId.equals(myModel.userId)) {
                            if (chattingModelList.get(position + 1).userConversationTime.equals(chattingModelList.get(position).userConversationTime)) {
                                ((ChattingYouViewHolder) holder).youItemBinding.timeTextView.setVisibility(View.INVISIBLE);
                                ((ChattingYouViewHolder) holder).youItemBinding.unReadCountTextView.setVisibility(View.INVISIBLE);
                            } else {
                                ((ChattingYouViewHolder) holder).youItemBinding.unReadCountTextView.setVisibility(View.VISIBLE);
                                ((ChattingYouViewHolder) holder).youItemBinding.timeTextView.setVisibility(View.VISIBLE);
                            }
                        } else {
                            ((ChattingYouViewHolder) holder).youItemBinding.unReadCountTextView.setVisibility(View.VISIBLE);
                            ((ChattingYouViewHolder) holder).youItemBinding.timeTextView.setVisibility(View.VISIBLE);
                        }
                    }


                    if (position > 0) {
                        if (!chattingModelList.get(position).currentActualTime.substring(0, 10)
                                .equals(chattingModelList.get(position - 1).currentActualTime.substring(0, 10))) {
                            ((ChattingYouViewHolder) holder).youItemBinding.regDateTextView.setVisibility(View.VISIBLE);
                        } else {
                            ((ChattingYouViewHolder) holder).youItemBinding.regDateTextView.setVisibility(View.GONE);
                        }
                    }


                } else {

                    if (position == 0) {
                        if(chattingModelList.get(position).userId
                                .equals(chattingModelList.get(position+1).userId)){
                            ((ChattingYouViewHolder) holder).youItemBinding.unReadCountTextView.setVisibility(View.INVISIBLE);
                            ((ChattingYouViewHolder) holder).youItemBinding.timeTextView.setVisibility(View.INVISIBLE);
                        }else{
                            ((ChattingYouViewHolder) holder).youItemBinding.unReadCountTextView.setVisibility(View.VISIBLE);
                            ((ChattingYouViewHolder) holder).youItemBinding.timeTextView.setVisibility(View.VISIBLE);
                        }

                    }
                }


                //todo : 현재 포지션 일떄
            } else if (position == chattingModelList.size() - 1) {
                //todo : 우선 포지션 값은 0보다 크기
                if (position > 0) {
                    //todo : 현재 포지션과 그 전 포지션 비교해서 메시지 타입이 Conversation 인지 아닌지 판단
                    if (chattingModelList.get(position - 1).userMessageType.equals(ChattingModel.MessageType.CONVERSATION)) {
                        //todo : conversation 이면서 지금 포지션과 전 포지션 아이디가 내 아이딜 경우
                        if (chattingModelList.get(position - 1).userId.equals(myModel.userId)) {
                            //todo : conversation 이면서 지금 포지션과 전 포지션 시간이 같을 경우
                            if (chattingModelList.get(position - 1).userConversationTime.equals(chattingModelList.get(position).userConversationTime)) {
                                ((ChattingYouViewHolder) holder).youItemBinding.unReadCountTextView.setVisibility(View.VISIBLE);
                                ((ChattingYouViewHolder) holder).youItemBinding.timeTextView.setVisibility(View.VISIBLE);
                                /*((ChattingYouViewHolder) holder).youItemBinding.personImageView.setVisibility(View.INVISIBLE);*/
                                ((ChattingYouViewHolder) holder).youItemBinding.nickNameTextView.setVisibility(View.GONE);
                            } else {
                                ((ChattingYouViewHolder) holder).youItemBinding.unReadCountTextView.setVisibility(View.VISIBLE);
                                ((ChattingYouViewHolder) holder).youItemBinding.timeTextView.setVisibility(View.VISIBLE);
                                /*((ChattingYouViewHolder) holder).youItemBinding.personImageView.setVisibility(View.VISIBLE);*/
                                /*((ChattingYouViewHolder) holder).youItemBinding.nickNameTextView.setVisibility(View.VISIBLE);*/
                            }
                        }
                    }
                    if (!chattingModelList.get(position).currentActualTime.substring(0, 10)
                            .equals(chattingModelList.get(position - 1).currentActualTime.substring(0, 10))) {
                        ((ChattingYouViewHolder) holder).youItemBinding.regDateTextView.setVisibility(View.VISIBLE);
                    } else {
                        ((ChattingYouViewHolder) holder).youItemBinding.regDateTextView.setVisibility(View.GONE);
                    }
                }
            }


        } else if (holder instanceof ChattingEnterExitViewHolder) {
            ((ChattingEnterExitViewHolder) holder).onBind(chattingModelList.get(position));
            if (position == 0) {
                ((ChattingEnterExitViewHolder) holder).enterExitBinding.regDateTextView.setVisibility(View.VISIBLE);
            } else {
                if (!chattingModelList.get(position).currentActualTime.substring(0, 10)
                        .equals(chattingModelList.get(position - 1).currentActualTime.substring(0, 10))) {
                    ((ChattingEnterExitViewHolder) holder).enterExitBinding.regDateTextView.setVisibility(View.VISIBLE);
                } else {
                    ((ChattingEnterExitViewHolder) holder).enterExitBinding.regDateTextView.setVisibility(View.GONE);
                }

            }

        } else if (holder instanceof ChattingEmptyViewHolder) {
            ((ChattingEmptyViewHolder) holder).onBind();

        } else {
            ((ChattingShowTodayViewHolder) holder).onBind(todayDateFormat.format(new Date().getTime()));

        }
    }


    public class ChattingMeViewHolder extends RecyclerView.ViewHolder {

        private AdapterOpenChattingMeBinding meItemBinding;

        public ChattingMeViewHolder(@NonNull @NotNull AdapterOpenChattingMeBinding meItemBinding) {
            super(meItemBinding.getRoot());
            this.meItemBinding = meItemBinding;
        }

        public void onBind(ChattingModel chattingModel) {

            try {

                if (chattingModel.chatting_ImageFile.equals("null")) {
                    if (!chattingModel.chatting_VideoFile.equals("null")) {
                        chattingModel.chatting_ImageFile = chattingModel.chatting_VideoFile;
                    }
                }

                meItemBinding.setChattingModel(chattingModel);
                meItemBinding.setFriendImageUrl(chattingModel.userImage);
                meItemBinding.setToday(stringToDate.format(todayStringToDate.parse(chattingModel.currentActualTime)));
                meItemBinding.executePendingBindings();


                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) meItemBinding.timeTextView.getLayoutParams();
                lp.addRule(RelativeLayout.ALIGN_BOTTOM, meItemBinding.conversationCardView.getId());
                lp.addRule(RelativeLayout.RIGHT_OF, meItemBinding.conversationCardView.getId());
                meItemBinding.timeTextView.setLayoutParams(lp);

                if (chattingModel.userConversation == null || chattingModel.userConversation.replace(" ", "").trim().equals("")) {
                    RelativeLayout.LayoutParams lp2 = (RelativeLayout.LayoutParams) meItemBinding.timeTextView.getLayoutParams();
                    lp2.addRule(RelativeLayout.ALIGN_BOTTOM, meItemBinding.imoticonImageView.getId());
                    lp2.addRule(RelativeLayout.RIGHT_OF, meItemBinding.imoticonImageView.getId());
                    meItemBinding.timeTextView.setLayoutParams(lp2);
                }

            } catch (ParseException e) {
            } catch (Exception e) {

            }

        }
    }


    public class ChattingYouViewHolder extends RecyclerView.ViewHolder {
        private AdapterOpenChattingYouBinding youItemBinding;

        public ChattingYouViewHolder(@NonNull @NotNull AdapterOpenChattingYouBinding youItemBinding) {
            super(youItemBinding.getRoot());
            this.youItemBinding = youItemBinding;
        }

        public void onBind(MyModel myModel, ChattingModel chattingModel) {

            try {

                if (chattingModel.chatting_ImageFile.equals("null")) {
                    if (!chattingModel.chatting_VideoFile.equals("null")) {
                        chattingModel.chatting_ImageFile = chattingModel.chatting_VideoFile;
                    }
                }

                youItemBinding.setChattingModel(chattingModel);
                youItemBinding.setMyModel(myModel);
                youItemBinding.setToday(stringToDate.format(todayStringToDate.parse(chattingModel.currentActualTime)));
                youItemBinding.executePendingBindings();

                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) youItemBinding.timeTextView.getLayoutParams();
                lp.addRule(RelativeLayout.ALIGN_BOTTOM, youItemBinding.conversationCardView.getId());
                lp.addRule(RelativeLayout.LEFT_OF, youItemBinding.conversationCardView.getId());
                youItemBinding.timeTextView.setLayoutParams(lp);
                if (chattingModel.userConversation == null || chattingModel.userConversation.replace(" ", "").trim().equals("")) {

                    RelativeLayout.LayoutParams lp2 = (RelativeLayout.LayoutParams) youItemBinding.timeTextView.getLayoutParams();
                    lp2.addRule(RelativeLayout.ALIGN_BOTTOM, youItemBinding.imoticonImageView.getId());
                    lp2.addRule(RelativeLayout.LEFT_OF, youItemBinding.imoticonImageView.getId());
                    youItemBinding.timeTextView.setLayoutParams(lp2);
                }


            } catch (ParseException e) {
            } catch (Exception e) {

            }


        }
    }


    public class ChattingEnterExitViewHolder extends RecyclerView.ViewHolder {
        private AdapterChattingEnterExitBinding enterExitBinding;

        public ChattingEnterExitViewHolder(@NonNull @NotNull AdapterChattingEnterExitBinding enterExitBinding) {
            super(enterExitBinding.getRoot());
            this.enterExitBinding = enterExitBinding;
        }


        public void onBind(ChattingModel chattingModel) {
            try {
                enterExitBinding.setChattingModel(chattingModel);
                enterExitBinding.setToday(stringToDate.format(new Date().getTime()));
                enterExitBinding.executePendingBindings();
            } catch (Exception e) {

            }

        }
    }

    public class ChattingShowTodayViewHolder extends RecyclerView.ViewHolder {
        private ChattingShowDateBinding binding;

        public ChattingShowTodayViewHolder(@NonNull @NotNull ChattingShowDateBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void onBind(String today) {
            binding.setToday(today);
            binding.executePendingBindings();
        }
    }

    public class ChattingEmptyViewHolder extends RecyclerView.ViewHolder {
        private AdapterChattingEmptyBinding binding;

        public ChattingEmptyViewHolder(@NonNull AdapterChattingEmptyBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void onBind() {
            binding.executePendingBindings();
        }
    }


}
