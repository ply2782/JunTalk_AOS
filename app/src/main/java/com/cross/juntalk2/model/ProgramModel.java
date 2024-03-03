package com.cross.juntalk2.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;

public class ProgramModel implements Parcelable
{
    private int id = -1;
    private String title = "";
    private String time = "";
    private String day = "";
    private String organization = "";
    private String participants = "";
    private String description = "";
    private String descriptionFull = "";
    private String location = "";
    private String locationCaption = "";
    private int locationCode = 0;
    private String locationMap = "";
    private String imageUrl = "";
    private String color = "";
    private int colorId = -1;
    private int columnId = -1;
    private String startTime = "";
    private String endTime = "";
    private Date convertedTimeStart = null;
    private Date convertedTimeEnd = null;
    private String theme = "";

    private ArrayList<Integer> tagsId = new ArrayList<Integer>();

    public ProgramModel()
    {
    }

    private ProgramModel(Parcel in)
    {
        id = in.readInt();
        title = in.readString();
        time = in.readString();
        day = in.readString();
        organization = in.readString();
        participants = in.readString();
        description = in.readString();
        descriptionFull = in.readString();
        location = in.readString();
        locationCaption = in.readString();
        locationCode = in.readInt();
        locationMap = in.readString();
        imageUrl = in.readString();
        color = in.readString();
        colorId = in.readInt();
        columnId = in.readInt();
        startTime = in.readString();
        endTime = in.readString();
        convertedTimeStart = new Date(in.readLong());
        convertedTimeEnd = new Date(in.readLong());
        theme = in.readString();
//        in.readTypedList(tags, TagModel.CREATOR);
        in.readList(tagsId, Integer.class.getClassLoader());
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    // Other getters and setters

    @Override
    public int describeContents()
    {
        return 0;
    }

    // this is used to regenerate your object
    public static final Parcelable.Creator<ProgramModel> CREATOR = new Parcelable.Creator<ProgramModel>()
    {
        public ProgramModel createFromParcel(Parcel in)
        {
            return new ProgramModel(in);
        }

        public ProgramModel[] newArray(int size)
        {
            return new ProgramModel[size];
        }
    };

    @Override
    public void writeToParcel(Parcel out, int flags)
    {
        out.writeInt(id);
        out.writeString(title);
        out.writeString(time);
        out.writeString(day);
        out.writeString(organization);
        out.writeString(participants);
        out.writeString(description);
        out.writeString(descriptionFull);
        out.writeString(location);
        out.writeString(locationCaption);
        out.writeInt(locationCode);
        out.writeString(locationMap);
        out.writeString(imageUrl);
        out.writeString(color);
        out.writeInt(colorId);
        out.writeInt(columnId);
        out.writeString(startTime);
        out.writeString(endTime);
        out.writeLong(convertedTimeStart.getTime());
        out.writeLong(convertedTimeEnd.getTime());
        out.writeString(theme);
//        out.writeTypedList(tags);
        out.writeList(tagsId);
    }
}
