package com.example.photos38;

import android.content.Context;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;

public class Album implements Serializable{
    private static final long serialVersionUID = 6529685098267757690L;

    public String name;
    public ArrayList<Photo> photos;

    public Album(String name){
        this.name = name;
        photos = new ArrayList<>();
    }

    public String toString() {
        return name;
    }

    public static ArrayList<Album> getAlbums(Context context){ //if file exists, return the arraylist. If not, return empty arraylist
        ArrayList<Album> albums;
        try { //adds the albums to the listview if the file exists
            FileInputStream fis = context.openFileInput(Home.FILE_NAME);
            ObjectInputStream is = new ObjectInputStream(fis);
            albums = (ArrayList<Album>) is.readObject();
            is.close();
            fis.close();
            return albums;
        } catch (Exception e){
            return new ArrayList<>();
        }
    }
}
