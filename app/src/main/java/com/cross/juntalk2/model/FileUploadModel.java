package com.cross.juntalk2.model;

import android.graphics.Bitmap;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FileUploadModel implements Serializable {

    public enum FileType{
        Music, Video
    }

    @SerializedName("fileName")
    public String fileName;
    @SerializedName("fileType")
    public FileType fileType;
    @SerializedName("fileLength")
    public long fileLength;
    @SerializedName("fileThumbNail")
    public Bitmap fileThumbNail;
}
