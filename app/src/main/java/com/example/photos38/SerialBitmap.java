package com.example.photos38;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;

public class SerialBitmap implements Serializable {
    private static final long serialVersionUID = 6529685098267757690L;
    byte[] thumbImageByteArray;

    public SerialBitmap(Bitmap thumbImage) {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        thumbImage.compress(Bitmap.CompressFormat.PNG, 10, byteStream);
        thumbImageByteArray = byteStream.toByteArray();
    }

   public Bitmap getThumbImage(){
       ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
       return BitmapFactory.decodeByteArray(thumbImageByteArray, 0, thumbImageByteArray.length);
   }
}
