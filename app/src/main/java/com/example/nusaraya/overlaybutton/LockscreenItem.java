package com.example.nusaraya.overlaybutton;

import org.json.JSONException;

/**
 * Created by nusaraya on 6/15/2016.
 */

public class LockscreenItem {
    protected String link;
    protected String kategori;
    protected String banner;
    protected Integer point;

    public String getLink(){
        return link;
    }

    public void setLink(String link) throws JSONException {
        this.link = link;
    }

    public String getKategori(){
        return kategori;
    }

    public void setKategori(String kategori) throws JSONException {
        this.kategori = kategori;
    }

    public String getBanner(){
        return banner;
    }

    public void setBanner(String banner) throws JSONException {
        this.banner = banner;
    }

    public Integer getPoint(){
        return point;
    }

    public void setPoint(Integer point) throws JSONException {
        this.point = point;
    }
}
