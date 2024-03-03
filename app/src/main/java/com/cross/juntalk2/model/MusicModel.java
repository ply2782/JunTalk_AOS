package com.cross.juntalk2.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class MusicModel  implements Serializable {



    @SerializedName("music_Index")
    public int music_Index;
    @SerializedName("music_singerName")
    public String music_singerName;
    @SerializedName("musicName")
    public String musicName;
    @SerializedName("musicTitle")
    public String musicTitle;
    @SerializedName("musicImage")
    public String musicImage;
    @SerializedName("musicContent")
    public String musicContent;
    @SerializedName("musicLength")
    public long musicLength;
    @SerializedName("type")
    public String type;
    @SerializedName("folderName")
    public String folderName;


}
