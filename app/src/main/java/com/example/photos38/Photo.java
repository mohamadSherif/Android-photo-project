package com.example.photos38;

import android.graphics.Bitmap;
import android.media.Image;
import android.widget.ImageView;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;

public class Photo implements Serializable{
    private static final long serialVersionUID = 6529685098267757690L;
    public String caption="N/A";
    public SerialBitmap byteThumbImage;
    public String location = "";
    public ArrayList<String> person = new ArrayList<>();

    public Photo(SerialBitmap thumbImage){
        this.byteThumbImage = thumbImage;
    }

    public boolean has(String personName){
        for(String p : person){
            if(p.equalsIgnoreCase(personName))
                return true;
        }
        return false;
    }

    public boolean hasSingleTag(String tag){
        if(location.toLowerCase().contains(tag))
            return true;
        for(String p : person){
            if(p.toLowerCase().contains(tag))
                return true;
        }
        return false;
    }
}