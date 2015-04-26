package com.naman14.fieldmapview;

/**
 * Created by naman on 18/04/15.
 */
public class ItemData {


    public String title;
    public int imageUrl;
    public String block;
    public String village;
    public String latlong;


    public String getLatlong(){
        return latlong;
    }
    public String getTitle(){
        return title;
    }
    public int getImageUrl(){
        return imageUrl;
    }
    public String getBlock(){
        return block;
    }
    public String getVillage(){
        return village;
    }
}

