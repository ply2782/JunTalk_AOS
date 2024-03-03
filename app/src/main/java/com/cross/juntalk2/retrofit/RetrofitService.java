package com.cross.juntalk2.retrofit;


import com.cross.juntalk2.model.BlockBulletinModel;
import com.cross.juntalk2.model.BulletinBoardModel;
import com.cross.juntalk2.model.BulletinCommentModel;
import com.cross.juntalk2.model.ChattingModel;
import com.cross.juntalk2.model.ClubModel;
import com.cross.juntalk2.model.CommentModel;
import com.cross.juntalk2.model.CommonNoticeModel;
import com.cross.juntalk2.model.FriendModel;
import com.cross.juntalk2.model.KakaoMapClassUtis;
import com.cross.juntalk2.model.LilsReplyModel;
import com.cross.juntalk2.model.MusicModel;
import com.cross.juntalk2.model.MyModel;
import com.cross.juntalk2.model.PayInfoModel;
import com.cross.juntalk2.model.RoomModel;
import com.cross.juntalk2.model.UnReadCountModel;
import com.cross.juntalk2.model.VideoListModel;
import com.cross.juntalk2.model.VideoModel;
import com.cross.juntalk2.utils.CommonString;
import com.google.android.gms.common.internal.service.Common;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface RetrofitService {

    /**
     * 구독 정보 가져오기 api
     */
    @FormUrlEncoded
    @POST("/{controller}/userPayInfo")
    Call<List<PayInfoModel>> userPayInfo(@Path("controller") CommonString commonString,
                                         @Field("user_Index") int user_Index,
                                         @Field("userId") String userId);

    /**
     * 세션 및 쿠키 만료 api
     */
    @FormUrlEncoded
    @POST("/{controller}/sessionEnd")
    Call<Void> sessionEnd(@Path("controller") CommonString controller,
                          @Field("userKakaoOwnNumber") long userKakaoOwnNumber);

    /**
     * 쥰스 댓글 리스트 차단 및 신고 api
     */
    @FormUrlEncoded
    @POST("/{controller}/blockLilsReplyList")
    Call<Boolean> blockLilsReplyList(@Path("controller") CommonString controller,
                                     @Field("userId") String userId,
                                     @Field("lils_Uuid") String lils_Uuid,
                                     @Field("fromUser") String fromUser,
                                     @Field("fromUser_Index") int fromUser_Index);

    @FormUrlEncoded
    @POST("/{controller}/reportLilsReplyList")
    Call<Boolean> reportLilsReplyList(@Path("controller") CommonString controller,
                                      @Field("userId") String userId,
                                      @Field("blockReportContent") String blockReportContent,
                                      @Field("lils_Uuid") String lils_Uuid,
                                      @Field("fromUser") String fromUser,
                                      @Field("fromUser_Index") int fromUser_Index);


    /**
     * 음악 게시판 댓글 리스트 신고 및 차단 api
     */
    @FormUrlEncoded
    @POST("/{controller}/insertBlockComment")
    Call<Boolean> insertBlockComment(@Path("controller") CommonString controller,
                                     @FieldMap Map<String, Object> blockCommentMap);


    /**
     * 게시물 게시판 댓글 리스트 신고 및 차단 api
     */
    @FormUrlEncoded
    @POST("/{controller}/insertBlockBulletinBoard_CommentList")
    Call<Boolean> insertBulletinBoard_CommentList(@Path("controller") CommonString controller,
                                                  @FieldMap Map<String, Object> blockCommentMap);

    /**
     * 건의사항 답글 달기 api
     */
    @FormUrlEncoded
    @POST("/{controller}/updateAdminAnswer")
    Call<Boolean> updateAdminAnswer(@Path("controller") CommonString commonString,
                                    @Field("user_Index") int user_Index,
                                    @Field("requestQuestion_Index") int requestQuestion_Index,
                                    @Field("adminAnswer") String adminAnswer);


    /**
     * 클럽채팅 새롭게 생긴 flag api
     */
    @FormUrlEncoded
    @POST("/{controller}/isNewClubChatting")
    Call<Boolean> isNewClubChatting(@Path("controller") CommonString commonString,
                                    @Field("userId") String userId);


    /**
     * 클럽채팅 새롭게 생긴 flag 보고 난 후  api
     */
    @FormUrlEncoded
    @POST("/{controller}/updateIsNewClubChatting")
    Call<Void> updateIsNewClubChatting(@Path("controller") CommonString commonString,
                                       @Field("userId") String userId,
                                       @Field("result") boolean result);

    /**
     * 건의 내용 불러오기 api
     */
    @POST("/{controller}/requestQuestionList")
    Call<List<Map<String, Object>>> requestQuestionList(@Path("controller") CommonString controller,
                                                        @Query("pageNum") int pageNum);

    /**
     * 건의 내용 작성 api
     */
    @FormUrlEncoded
    @POST("/{controller}/sendRequestQuestionList")
    Call<Boolean> sendRequestQuestionList(@Path("controller") CommonString controller,
                                          @Field("user_Index") int user_Index,
                                          @Field("userId") String userId,
                                          @Field("requestContenbulletinBoardt") String requestContent);

    /**
     * 채팅 조인 방 만들기 api
     */
    @FormUrlEncoded
    @POST("/{controller}/createJoinChattingRoom")
    Call<Boolean> createJoinChattingRoom(@Path("controller") CommonString controller,
                                         @Field("fireBaseToken") String fireBaseToken,
                                         @Field("club_Uuid") String room_Uuid,
                                         @Field("room_Title") String room_Title,
                                         @Field("roomHashTag") String roomHashTag,
                                         @Field("joinRoomContent") String joinRoomContent,
                                         @Field("userMainPhoto") String userMainPhoto,
                                         @Field("userId") String userId);


    /**
     * 해당 유저 차단 api
     */
    @FormUrlEncoded
    @POST("/{controller}/blockUser")
    Call<Boolean> blockUser(@Path("controller") CommonString controller,
                            @Field("fromUser_Index") int fromUser_Index,
                            @Field("fromUser") String fromUser,
                            @Field("user_Index") int user_Index,
                            @Field("userId") String userId);

    /**
     * 해당 유저 차단 api
     */
    @FormUrlEncoded
    @POST("/{controller}/blockReportUser")
    Call<Boolean> blockReportUser(@Path("controller") CommonString controller,
                                  @Field("fromUser_Index") int fromUser_Index,
                                  @Field("fromUser") String fromUser,
                                  @Field("user_Index") int user_Index,
                                  @Field("userId") String userId,
                                  @Field("blockReportContent") String blockReportContent);

    /**
     * 해당 쥰스 영상 신고 api
     */
    @FormUrlEncoded
    @POST("/{controller}/blockReportJunes")
    Call<Boolean> blockReportJunes(@Path("controller") CommonString controller,
                                   @Field("fromUser_Index") int fromUser_Index,
                                   @Field("lils_Uuid") String lils_Uuid,
                                   @Field("user_Index") int user_Index,
                                   @Field("lils_Index") int lils_Index,
                                   @Field("userId") String userId,
                                   @Field("blockReportContent") String blockReportContent);

    /**
     * 결제관련 정보 저장 api
     */
    @FormUrlEncoded
    @POST("/{controller}/savePay")
    Call<Void> savePay(@Path("controller") CommonString controller,
                       @FieldMap Map<String, Object> saveMap);


    /**
     * 매니저 모드 여부 api
     */
    @POST("/{controller}/managerModeIsActivated")
    Call<Boolean> managerModeIsActivated(@Path("controller") CommonString controller);

    /**
     * 매니저 모드 로그인 api
     */
    @FormUrlEncoded
    @POST("/{controller}/managerLogin")
    Call<MyModel> myModel(@Path("controller") CommonString controller,
                          @Field("userId") String userId,
                          @Field("password") String password);

    /**
     * 카카오 검색 api
     */
    @GET("/v2/local/search/keyword.json")
    Call<KakaoMapClassUtis.ResultSearchKeyword> getSearchKeyword(@Header("Authorization") String key,     // 카카오 API 인증키 [필수]
                                                                 @Query("query") String query,
                                                                 @Query("page") int page);

    /**
     * 해당 날짜 클럽 리스트 불러오기 api
     */
    @FormUrlEncoded
    @POST("/{controller}/loadCurrentDateClubList")
    Call<Map<String, Object>> clubListModel(@Path("controller") CommonString controller,
                                            @Field("currentDate") String currentDate,
                                            @Field("user_Index") int user_Index);


    /**
     * 클럽 수락 및 거절  api
     */
    @FormUrlEncoded
    @POST("/{controller}/updateRequestResult")
    Call<Boolean> updateRequestResult(@Path("controller") CommonString controller,
                                      @FieldMap Map<String, Object> item);

    /**
     * 클럽 리스트 block api
     */
    @FormUrlEncoded
    @POST("/{controller}/blockClubList")
    Call<Boolean> blockClubList(@Path("controller") CommonString controller,
                                @Field("club_Uuid") String club_Uuid,
                                @Field("userId") String userId,
                                @Field("user_Index") int user_Index,
                                @Field("blockReportContent") String blockReportContent);


    /**
     * 클럽 내용 수정 api
     */
    @Multipart
    @POST("/{controller}/updateClub")
    Call<Boolean> updateClubList(@Path("controller") CommonString controller,
                                 @PartMap Map<String, RequestBody> insertClubInfoMap,
                                 @Part List<MultipartBody.Part> imageFileList,
                                 @Part List<MultipartBody.Part> videoFileList);

    /**
     * 클럽 리스트 삭제 api
     */
    @POST("/{controller}/deleteClubList")
    Call<Boolean> deleteClubList(@Path("controller") CommonString controller,
                                 @Body ClubModel clubModel);

    /**
     * 클럽 생성 api
     */
    @Multipart
    @POST("/{controller}/createClub")
    Call<Boolean> createClub(@Path("controller") CommonString controller,
                             @PartMap Map<String, RequestBody> insertClubInfoMap,
                             @Part List<MultipartBody.Part> imageFileList,
                             @Part List<MultipartBody.Part> videoFileList);

    /**
     * 오픈 채팅방 개설하기 api
     */
    @Multipart
    @POST("/{controller}/createOpenChatting")
    Call<Boolean> createOpenChatting(@Path("controller") CommonString controller,
                                     @PartMap Map<String, RequestBody> insertClubInfoMap,
                                     @Part List<MultipartBody.Part> imageFileList,
                                     @Part List<MultipartBody.Part> videoFileList);

    /**
     * 오픈 채팅방 리스트 불러오기 api
     */
    @FormUrlEncoded
    @POST("/{controller}/loadOpenChatting")
    Call<List<Map<String, Object>>> loadOpenChatting(@Path("controller") CommonString controller, @Field("page") int page, @Field("userId") String userId);

    /**
     * 클럽 리스트 불러오기 api
     */
    @FormUrlEncoded
    @POST("/{controller}/clubList")
    Call<Map<String, Object>> clubList(@Path("controller") CommonString controller, @Field("user_Index") int user_Index);

    /**
     * 클럽 참여하기 api
     */
    @FormUrlEncoded
    @POST("/{controller}/joinClub")
    Call<Boolean> joinClub(@Path("controller") CommonString controller, @FieldMap Map<String, Object> clubInfoMap);

    /**
     * 클럽 참여한 사람 불러오기  api
     */
    @FormUrlEncoded
    @POST("/{controller}/clubJoinList")
    Call<List<Map<String, Object>>> joinPeopleList(@Path("controller") CommonString controller,
                                                   @Field("club_Uuid") String club_Uuid,
                                                   @Field("user_Index") int user_Index);

    /**
     * 릴스 목록 불러오기
     */
    @FormUrlEncoded
    @POST("/{controller}/LilsVideoList")
    Call<List<VideoListModel>> lilsVideoList(@Path("controller") CommonString controller,
                                             @Field("page") int page,
                                             @Field("userId") String userId,
                                             @Field("user_Index") int user_Index);

    /**
     * 나의 릴스 목록 불러오기
     */
    @FormUrlEncoded
    @POST("/{controller}/myLilsVideoList")
    Call<List<VideoListModel>> myLilsVideoList(@Path("controller") CommonString controller,
                                               @Field("page") int page,
                                               @Field("userId") String userId,
                                               @Field("user_Index") int user_Index);


    /**
     * 릴스 좋아요 api
     */
    @FormUrlEncoded
    @POST("/{controller}/LilsClickLike")
    Call<Boolean> lilsLike(@Path("controller") CommonString controller,
                           @FieldMap Map<String, Object> infoMap);


    /**
     * 릴스 답글 api
     */
    @FormUrlEncoded
    @POST("/{controller}/LilsReply")
    Call<List<LilsReplyModel>> lilsReply(@Path("controller") CommonString controller,
                                         @FieldMap Map<String, Object> infoMap);


    @FormUrlEncoded
    @POST("/{controller}/enterLilsReply")
    Call<List<LilsReplyModel>> enterLilsReply(@Path("controller") CommonString controller,
                                              @FieldMap Map<String, Object> infoMap);


    /**
     * 오픈 채팅 입장 api
     */
    @FormUrlEncoded
    @POST("/{controller}/myOpenChattingList")
    Call<Void> insertOpenChattingList(@Path("controller") CommonString controller,
                                      @FieldMap Map<String, Object> map);

    /**
     * 릴스 목록 삭제하기
     */
    @POST("/{controller}/removeLilsVideoList")
    Call<Boolean> removeLilsVideoList(@Path("controller") CommonString controller, @Body VideoListModel videoListModel);

    /**
     * 릴스 만들기
     */
    @Multipart
    @POST("/{controller}/createLilsVideoList")
    Call<Boolean> createLilsVideoList(@Path("controller") CommonString controller,
                                      @Part MultipartBody.Part videoFile,
                                      @Part("lils_Uuid") RequestBody lils_Uuid,
                                      @Part("userId") RequestBody userId,
                                      @Part("userMainPhoto") RequestBody userMainPhoto,
                                      @Part("user_Index") RequestBody user_Index,
                                      @Part("contents") RequestBody contents,
                                      @Part("hashTagList") RequestBody hashTagList);

    @POST("/{controller}/blockLilsVideoList")
    Call<Boolean> blockLilsVideoList(@Path("controller") CommonString controller,
                                     @Body VideoListModel videoListModel);


    /**
     * 메시지 fcm 알람 조절 api
     */
    @POST("/{controller}/chattingAlarmUpdate")
    Call<Boolean> chattingAlarmUpdate(@Path("controller") CommonString controller,
                                      @Query("room_Uuid") String room_Uuid,
                                      @Query("userId") String userId,
                                      @Query("currentState") boolean currentState);

    /**
     * 계정 삭제 api
     */
    @POST("/{controller}/deleteAccount")
    Call<Boolean> deleteAccount(@Path("controller") CommonString controller, @Query("userKakaoOwnNumber") long userKakaoOwnNumber);

    /**
     * 현재 방에 있는 사람 리스트 불러오기 api
     */
    @POST("/{controller}/currentRoomJoinPeopleList")
    @FormUrlEncoded
    Call<List<FriendModel>> currentRoomJoinPeopleList(@Path("controller") CommonString controller,
                                                      @Field("room_Uuid") String room_Uuid,
                                                      @Field("userId") String userId);


    /**
     * 게시판 신고 기능 api
     */
    @POST("/{controller}/blockBulletinBoard")
    Call<Boolean> blockBulletinBoard(@Path("controller") CommonString controller,
                                     @QueryMap Map<String, Object> blockInfo);

    /**
     * 음악 신고 기능 api
     */
    @POST("/{controller}/blockMusic")
    Call<Boolean> blockMusic(@Path("controller") CommonString controller,
                             @QueryMap Map<String, Object> blockInfo);

    /**
     * 동영 신고 기능 api
     */
    @POST("/{controller}/blockVideo")
    Call<Boolean> blockVideo(@Path("controller") CommonString controller,
                             @QueryMap Map<String, Object> blockInfo);


    /**
     * 채팅 요청시 보내는 fcm  api
     */
    @POST("/{controller}/friendRequest")
    Call<String> send_NotificationFcm(@Path("controller") CommonString controller, @Body RoomModel roomModel, @Query(value = "type") String type);

    /**
     * 가입 완료시 친구리스트 자동 갱신 위한 fcm api
     */
    @FormUrlEncoded
    @POST("/{controller}/noticeJoin")
    Call<String> send_noticeJoin(@Path("controller") CommonString controller,
                                 @FieldMap Map<String, Object> fireBaseTokenMap);


    /**
     * firebase token 업데이트 api
     */
    @FormUrlEncoded
    @POST("/{controller}/updateToken")
    Call<Boolean> updateToken(@Path("controller") CommonString controller,
                              @FieldMap Map<String, Object> userInfoMap);


    /**
     * 아이디 존재 여부 체크 api
     */
    @GET("/{controller}/isExistOfId")
    Observable<MyModel> isExistOfId(@Path("controller") String controller, @Query("userKakaoOwnNumber") long userKakaoOwnNumber);

    /**
     * 인증번호 요청 api
     */
    @FormUrlEncoded
    @POST("/{controller}/checkPhoneNumber")
    Observable<String> getRandomCheckNumber(@Path("controller") String controller, @Field("myPhoneNumber") String myPhoneNumber);

    /**
     * 아이디 중복 api
     */
    @FormUrlEncoded
    @POST("/{controller}/overLapId")
    Observable<Boolean> overLapId(@Path("controller") String controller, @Field("userId") String userId);

    /**
     * 회원가입 api
     */
    @POST("/{controller}/join")
    Observable<Boolean> join(@Path("controller") CommonString controller, @Body MyModel myModel);



    @Multipart
    @POST("/{controller}/saveMainUserPhoto")
    Call<Void> saveMainUserPhoto(@Path("controller") CommonString controller,
                                 @Part MultipartBody.Part userMainPhoto);

    /**
     * 메인 친구 리스트 api
     */
    @GET("/{controller}/mainList")
    Observable<Map<String, Object>> mainList(@Path("controller") String controller, @Query("userKakaoOwnNumber") long userKakaoOwnNumber, @Query("today") String today, @Query("category") String category);

    /**
     * 채팅 룸 만들기 api
     */
    @FormUrlEncoded
    @POST("/{controller}/createRoomList")
    Call<Map<String, String>> createChattingRoomList(@Path("controller") CommonString controller, @FieldMap Map<String, Object> roomInfo);


    /**
     * 채팅 리스트 페이징 api
     */
    @FormUrlEncoded
    @POST("/{controller}/roomList")
    Call<List<Map<String, Object>>> roomListPaging(@Path("controller") CommonString controller, @Field("page") int page, @Field("userId") String userId);

    /**
     * 파일 저장 api
     */
    @Multipart
    @POST("/{controller}/saveFileImage")
    Observable<String> saveFiles(@Path("controller") CommonString controller,
                                 @Part List<MultipartBody.Part> imageFileList,
                                 @Part List<MultipartBody.Part> videoFileList);


    /**
     * 프로필 사진 및 데이터 저장 api
     */
    @Multipart
    @POST("/{controller}/saveMyData")
    Call<MyModel> saveMyDataFile(@Path("controller") CommonString controller,
                                 @Part MultipartBody.Part userMainPhoto,
                                 @Part("userId") RequestBody userId,
                                 @Part("user_Index") RequestBody user_Index,
                                 @Part("user_Introduce") RequestBody user_Introduce);

    /**
     * 로그인 시간 갱신 api
     */
    @POST("/{controller}/updateLastLogin")
    Call<Boolean> updateLastLogin(@Path("controller") CommonString controller,
                                  @Query("userKakaoOwnNumber") long userKakaoOwnNumber,
                                  @Query("currentVersion") String currentAppVersion);


    /**
     * 음악 리스트 불러오기 api
     */
    @POST("/{controller}/music/{folderName}")
    Call<List<MusicModel>> musicList(@Path("controller") CommonString controller,
                                     @Path("folderName") String folderName,
                                     @Query("pageNum") int pageNum);


    /**
     * 동영상 리스트 불러오기 api
     */
    @POST("/{controller}/video/{folderName}")
    Call<List<VideoModel>> videoList(@Path("controller") CommonString controller,
                                     @Path("folderName") String folderName,
                                     @Query("pageNum") int pageNum);


    /**
     * 댓글 불러오기 api
     */
    @FormUrlEncoded
    @POST("/{controller}/comment")
    Call<List<CommentModel>> comment(@Path("controller") CommonString controller,
                                     @Field("music_Index") int position,
                                     @Field("fromUser_Index") int fromUser_Index);

    /**
     * 댓글 저장 api
     */
    @POST("/{controller}/insertComment")
    Call<Boolean> insertComment(@Path("controller") CommonString controller,
                                @Body List<CommentModel> commentModels);

    /**
     * 게시판 목록 불러오기 api
     */
    @GET("/{controller}/bulletinBoard")
    Observable<List<BulletinBoardModel>> getBulletinBoard(@Path("controller") CommonString controller,
                                                          @Query("myId") String myId,
                                                          @Query("user_Index") int user_Index,
                                                          @Query("category") String category,
                                                          @Query("page") int page);


    /**
     * 상대방의 게시판 목록 불러오기 api
     */
    @FormUrlEncoded
    @POST("/{controller}/personal_BulletinBoard")
    Observable<List<BulletinBoardModel>> personal_BulletinBoard(@Path("controller") CommonString controller,
                                                                @Field("userId") String userId,
                                                                @Query("myIndex") int user_Index,
                                                                @Field("myId") String myId,
                                                                @Field("category") String category,
                                                                @Field("page") int page);


    /**
     * 게시판 작성 api
     */
    @Multipart
    @POST("/{controller}/insertBulletinBoard")
    Observable<Boolean> insertBulletinBoard(@Path("controller") CommonString controller,
                                            @PartMap Map<String, RequestBody> insertBulletinBoardMap,
                                            @Part List<MultipartBody.Part> videoFiles,
                                            @Part List<MultipartBody.Part> imageFiles);


    /**
     * 게시판 삭제 api
     */
    @POST("/{controller}/deleteBulletinBoard")
    Call<Boolean> deleteBulletinBoard(@Path("controller") CommonString controller,
                                      @Body BulletinBoardModel bulletinBoardModel);

    /**
     * 게시판 편집 api
     */
    @Multipart
    @POST("/{controller}/editBulletinBoard")
    Observable<Boolean> editBulletinBoard(@Path("controller") CommonString controller,
                                          @PartMap Map<String, RequestBody> insertBulletinBoardMap,
                                          @Part List<MultipartBody.Part> videoFiles,
                                          @Part List<MultipartBody.Part> imageFiles);


    /**
     * 게시판 좋아요 클릭 수 api
     */
    @GET("/{controller}/clickLike")
    Call<Boolean> clickLike(@Path("controller") CommonString controller,
                            @Query("bulletin_Uuid") String bulletin_Uuid,
                            @Query("userId") String userId,
                            @Query("fromUser") String fromUser);


    /**
     * 게시판 댓글 불러오기 api
     */
    @GET("/{controller}/bulletinCommentList")
    Call<List<BulletinCommentModel>> bulletinCommentList(@Path("controller") CommonString controller,
                                                         @Query("bulletin_Uuid") String bulletin_Uuid,
                                                         @Query("fromUser_Index") int fromUser_Index);

    /**
     * 게시판 댓글 작성 api
     */
    @POST("/{controller}/insertBulletinBoardComment")
    Call<Boolean> insertBulletinBoardComment(@Path("controller") CommonString controller, @Body BulletinCommentModel bulletinCommentModel);


    /**
     * 음악 업로드 api
     */
    @Multipart
    @POST("/{controller}/musicUpload")
    Observable<Boolean> musicUpload(@Path("controller") CommonString controller,
                                    @PartMap Map<String, Object> userInfoMap,
                                    @Part List<MultipartBody.Part> musicFileList,
                                    @Part List<MultipartBody.Part> imageFileList);

    /**
     * 비디오 영상 업로드 api
     */
    @Multipart
    @POST("/{controller}/videoUpload")
    Observable<Boolean> videoUpload(@Path("controller") CommonString controller,
                                    @PartMap Map<String, Object> userInfoMap,
                                    @Part List<MultipartBody.Part> videoFileList,
                                    @Part List<MultipartBody.Part> imageFileList);


    /**
     * 공지사항 목록 불러오기 api
     */
    @GET("/{controller}/loadNotice")
    Observable<List<CommonNoticeModel>> loadNotice(@Path("controller") CommonString controller);

    /**
     * 마퀴 되는 대표 공지사항 불러오기 api
     */
    @GET("/{controller}/loadMainNoticeContent")
    Call<String> loadMainNoticeContent(@Path("controller") CommonString controller);


    /**
     * 음악 mp3 다운로드 api
     */
    @GET("/{controller}/musicDownLoad2")
    Observable<ResponseBody> musicDownLoad(@Path("controller") CommonString controller, @Query("fileName") String musicName);

    /**
     * 친구 초대 api
     */
    @FormUrlEncoded
    @POST("/{controller}/addFriendInRoom")
    Call<Boolean> addFriendInRoom(@Path("controller") CommonString controller,
                                  @Field("roomType") String roomType,
                                  @Field("myMainPhoto") String myMainPhoto,
                                  @Field("userId") String userId,
                                  @Field("fireBaseToken") String fireBaseToken,
                                  @Field("userState") ChattingModel.UserState userState,
                                  @Field("room_Index") int room_Index,
                                  @Field("friend_Index") int friend_Index,
                                  @Field("friendId") String friendId,
                                  @Field("userKakaoOwnNumber") long userKakaoOwnNumber,
                                  @Field("userMainPhoto") String userMainPhoto,
                                  @Field(value = "room_Uuid") String room_Uuid,
                                  @Field(value = "notice_RegDate") String notice_RegDate,
                                  @Field(value = "fromUser") String fromUser,
                                  @Field(value = "toUser") String toUser


    );


    @FormUrlEncoded
    @POST("/{controller}/unReadCount")
    Call<Integer> unReadCount(@Path("controller") CommonString controller, @Field("userKakaoOwnNumber") long userKakaoOwnNumber);

    @FormUrlEncoded
    @POST("/{controller}/blockUserList")
    Call<List<FriendModel>> blockUserList(@Path("controller") CommonString controller,
                                          @Field("user_Index") int user_Index);


    @FormUrlEncoded
    @POST("/{controller}/loadChattingConversation")
    Call<List<ChattingModel>> loadChattingConversation(@Path("controller") CommonString controller,
                                                       @Field("room_Uuid") String room_Uuid,
                                                       @Field("userId") String userId);


    /**
     * 공지사항 지우기 api
     */
    @FormUrlEncoded
    @POST("/{controller}/removeNotice")
    Call<Boolean> removeNotice(@Path("controller") CommonString controller,
                               @Field(value = "noticeNumber") List<Integer> noticeNumber);

    @POST("/{controller}/getCurrentVersion")
    Observable<Map<String, Object>> getCurrentVersion(@Path("controller") CommonString controller);
}
