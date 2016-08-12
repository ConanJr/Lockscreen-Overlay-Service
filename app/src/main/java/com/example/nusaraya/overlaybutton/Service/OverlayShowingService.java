package com.example.nusaraya.overlaybutton.Service;

import android.animation.ObjectAnimator;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextClock;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.example.nusaraya.overlaybutton.LockscreenItem;
import com.example.nusaraya.overlaybutton.R;
import com.example.nusaraya.overlaybutton.customViewGroup;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import static android.content.ContentValues.TAG;

/**
 * Created by nusaraya on 6/15/2016.
 */

public class OverlayShowingService extends Service implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {
    private final String KEY_TANGGAL = "Tanggal";
    private final String KEY_JSON = "Json";
    private final String KEY_PROMO_NUMBER = "Jumlah";

    private boolean internet_state;
    private SeekBar seekBar;
    private SliderLayout slider;
    private TextView banner, point, textDate, textSlideShadow;
    private ImageView icon_unlock, icon_search;
    private TextClock textClock;
    private ShimmerTextView textSlide;
    private int seekbarLevel;
    private WindowManager wm;
    private String[] nama_bulan = {"January", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
    private String tanggal;
    private TextSliderView textSliderView;
    private ArrayList<LockscreenItem> items = new ArrayList<>();
    private JSONObject jItem = null;
    private customViewGroup blockingView = null;
    private SharedPreferences pref = null;
    private SharedPreferences.Editor editor = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        disableStatusBar();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Calendar c = Calendar.getInstance();
        String today = String.valueOf(c.get(Calendar.DATE)) + " " + nama_bulan[c.get(Calendar.MONTH)] + " " + c.get(Calendar.YEAR);
        String time = String.valueOf(c.get(Calendar.HOUR)) + ":" + String.valueOf(c.get(Calendar.MINUTE));
        tanggal = String.valueOf(c.get(Calendar.DATE));
        internet_state = checkInternet();
        slider = new SliderLayout(this);
        slider.setBackgroundColor(Color.BLACK);

        banner = new TextView(this);
        banner.setTextSize(24);
        banner.setTypeface(Typeface.DEFAULT_BOLD);
        banner.setTextColor(Color.WHITE);
        banner.setShadowLayer(8, 0, 0, Color.BLACK);

        textDate = new TextView(this);
        textDate.setTextSize(24);
        textDate.setTypeface(Typeface.DEFAULT);
        textDate.setTextColor(Color.WHITE);
        textDate.setShadowLayer(8, 0, 0, Color.BLACK);
        textDate.setText(today);

        textClock = new TextClock(this);
        textClock.setTextSize(24);
        textClock.setTypeface(Typeface.DEFAULT);
        textClock.setTextColor(Color.WHITE);
        textClock.setShadowLayer(8, 0, 0, Color.BLACK);
        textClock.setText(time);

        point = new TextView(this);
        point.setTextSize(15);
        point.setTypeface(Typeface.DEFAULT_BOLD);
        point.setTextColor(Color.WHITE);
        point.setShadowLayer(8, 0, 0, Color.BLACK);

        textSlide = new ShimmerTextView(this);
        textSlide.setTextSize(15);
        textSlide.setText(R.string.unlock_label);
        textSlide.setTypeface(Typeface.DEFAULT_BOLD);
        textSlide.setTextColor(Color.parseColor("#04AEDA"));
        textSlide.setReflectionColor(Color.BLUE);

        textSlideShadow = new TextView(this);
        textSlideShadow.setTextSize(15);
        textSlideShadow.setText(R.string.unlock_label);
        textSlideShadow.setTypeface(Typeface.DEFAULT_BOLD);
        textSlideShadow.setTextColor(Color.WHITE);
        textSlideShadow.setShadowLayer(8, 0, 0, Color.BLACK);

        Drawable lockDrawable = getResources().getDrawable(R.drawable.lock);
        Bitmap lockBitmap = ((BitmapDrawable) lockDrawable).getBitmap();
        final Drawable thumbLock = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(lockBitmap, 80, 80, true));
        Drawable unlockDrawable = getResources().getDrawable(R.drawable.unlock);
        Bitmap unlockBitmap = ((BitmapDrawable) unlockDrawable).getBitmap();
        final Drawable thumbUnlock = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(unlockBitmap, 100, 100, true));
        Drawable searchDrawable = getResources().getDrawable(R.drawable.search);
        Bitmap searchBitmap = ((BitmapDrawable) searchDrawable).getBitmap();
        final Drawable thumbSearch = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(searchBitmap, 100, 100, true));

        icon_unlock = new ImageView(this);
        icon_unlock.setMaxHeight(80);
        icon_unlock.setMaxWidth(80);
        icon_unlock.setScaleType(ImageView.ScaleType.FIT_CENTER);
        icon_unlock.setImageBitmap(unlockBitmap);

        icon_search = new ImageView(this);
        icon_search.setMaxHeight(80);
        icon_search.setMaxWidth(80);
        icon_search.setScaleType(ImageView.ScaleType.FIT_CENTER);
        icon_search.setImageBitmap(searchBitmap);

        seekBar = new SeekBar(this);
        seekBar.setThumb(thumbLock);
        seekBar.setMax(100);
        seekBar.setProgressDrawable(getResources().getDrawable(R.drawable.seekbar_progress));
        seekBar.setProgress(50);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekbarLevel = progress;
                if (seekbarLevel <= 10) {
                    seekBar.setThumb(thumbUnlock);
                    icon_unlock.setVisibility(View.GONE);
                    icon_search.setVisibility(View.GONE);
                } else if (seekbarLevel >= 90) {
                    seekBar.setThumb(thumbSearch);
                    icon_unlock.setVisibility(View.GONE);
                    icon_search.setVisibility(View.GONE);
                } else {
                    seekBar.setThumb(thumbLock);
                    icon_unlock.setVisibility(View.VISIBLE);
                    icon_search.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekbarLevel = 50;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekbarLevel >= 90){
                    if(internet_state) {
                        openPromo();
                    }
                    unlockScreen();
                    stopSelf();
                }else if (seekbarLevel <= 10) {
                    unlockScreen();
                    stopSelf();
                } else {
                    ObjectAnimator animator = ObjectAnimator.ofInt(seekBar, "progress", 50);
                    animator.setDuration(500);
                    animator.setInterpolator(new DecelerateInterpolator());
                    animator.start();
                }
            }
        });

        WindowManager.LayoutParams paramsSlider = new WindowManager.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT,
                LayoutParams.TYPE_PRIORITY_PHONE | LayoutParams.TYPE_SYSTEM_ALERT,
                LayoutParams.FLAG_LAYOUT_IN_SCREEN | LayoutParams.FLAG_LAYOUT_INSET_DECOR | LayoutParams.FLAG_FULLSCREEN,
                PixelFormat.TRANSLUCENT);
        paramsSlider.gravity = Gravity.START | Gravity.BOTTOM;
        paramsSlider.x = 0;
        paramsSlider.y = 0;
        paramsSlider.width = dm.widthPixels;
        paramsSlider.height = dm.heightPixels;
        wm.addView(slider, paramsSlider);

        WindowManager.LayoutParams paramsBanner = new WindowManager.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                LayoutParams.TYPE_SYSTEM_ERROR,
                LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        paramsBanner.gravity = Gravity.START | Gravity.TOP;
        paramsBanner.x = 30;
        paramsBanner.y = 30;
        wm.addView(banner, paramsBanner);

        WindowManager.LayoutParams paramsDate = new WindowManager.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                LayoutParams.TYPE_SYSTEM_ERROR,
                LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        paramsDate.gravity = Gravity.END | Gravity.TOP;
        paramsDate.x = 30;
        paramsDate.y = 30;
        wm.addView(textDate, paramsDate);

        WindowManager.LayoutParams paramsTime = new WindowManager.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                LayoutParams.TYPE_SYSTEM_ERROR,
                LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        paramsTime.gravity = Gravity.END | Gravity.TOP;
        paramsTime.x = 30;
        paramsTime.y = 100;
        wm.addView(textClock, paramsTime);

        WindowManager.LayoutParams paramsPoint = new WindowManager.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                LayoutParams.TYPE_SYSTEM_ERROR,
                LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        paramsPoint.gravity = Gravity.END | Gravity.BOTTOM;
        paramsPoint.x = 10;
        paramsPoint.y = 120;
        wm.addView(point, paramsPoint);

        WindowManager.LayoutParams paramsTextSlide = new WindowManager.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                LayoutParams.TYPE_SYSTEM_ERROR,
                LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        paramsTextSlide.gravity = Gravity.CENTER | Gravity.BOTTOM;
        paramsTextSlide.x = 0;
        paramsTextSlide.y = 120;
        wm.addView(textSlideShadow, paramsTextSlide);
        wm.addView(textSlide, paramsTextSlide);

        WindowManager.LayoutParams paramsIconUnlock = new WindowManager.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                LayoutParams.TYPE_SYSTEM_ERROR,
                LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        paramsIconUnlock.gravity = Gravity.START | Gravity.BOTTOM;
        paramsIconUnlock.x = 10;
        paramsIconUnlock.y = 15;
        paramsIconUnlock.height = 80;
        paramsIconUnlock.width = 80;
        wm.addView(icon_unlock, paramsIconUnlock);

        WindowManager.LayoutParams paramsIconSearch = new WindowManager.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                LayoutParams.TYPE_SYSTEM_ERROR,
                LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        paramsIconSearch.gravity = Gravity.END | Gravity.BOTTOM;
        paramsIconSearch.x = 10;
        paramsIconSearch.y = 15;
        paramsIconSearch.height = 80;
        paramsIconSearch.width = 80;
        wm.addView(icon_search, paramsIconSearch);

        WindowManager.LayoutParams paramsSeekBar = new WindowManager.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                LayoutParams.TYPE_SYSTEM_ERROR,
                LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        paramsSeekBar.gravity = Gravity.START | Gravity.BOTTOM;
        paramsSeekBar.x = 0;
        paramsSeekBar.y = 10;
        paramsSeekBar.width = dm.widthPixels;
        paramsSeekBar.height = 100;
        wm.addView(seekBar, paramsSeekBar);

        LockscreenItem item;

        if(internet_state) {
            pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE); // 0 - for private mode
            if ((pref.contains(KEY_TANGGAL)) && (tanggal.equalsIgnoreCase(pref.getString(KEY_TANGGAL, "")))) {
                String jString = pref.getString(KEY_JSON, "");
                JSONArray jResponse;
                try {
                    int max = pref.getInt(KEY_PROMO_NUMBER, 0);
                    jResponse = new JSONArray(jString);
                    for (int i = 0; i < max; i++) {
                        item = new LockscreenItem();
                        jItem = jResponse.getJSONObject(i);
                        item.setLink(jItem.getString("url"));
                        item.setKategori(jItem.getString("category"));
                        item.setBanner(jItem.getString("image"));
                        item.setPoint(jItem.getInt("point"));
                        items.add(item);
                    }

                    renderSlider();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                sendingRequestList();
            }
        } else {
            textSliderView = new TextSliderView(OverlayShowingService.this);
            // initialize a SliderLayout
            textSliderView
                    .description("No Internet Connection")
                    .image(R.drawable.nointernet)
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(OverlayShowingService.this);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle().putString("extra","No Internet Connection");
            textSliderView.getBundle().putString("point","0");
            slider.addSlider(textSliderView);

            slider.setPresetTransformer(SliderLayout.Transformer.Accordion);
            slider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
            slider.setCustomAnimation(new DescriptionAnimation());
            slider.addOnPageChangeListener(this);
            slider.stopAutoCycle();
        }

        Shimmer shimmer = new Shimmer();
        shimmer.start(textSlide);
    }

    private void unlockScreen(){
        if (blockingView!=null) {
            WindowManager manager = ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE));
            manager.removeView(blockingView);
        }
        wm.removeView(seekBar);
        wm.removeView(slider);
        wm.removeView(banner);
        wm.removeView(textClock);
        wm.removeView(textDate);
        wm.removeView(textSlide);
        wm.removeView(textSlideShadow);
        wm.removeView(point);
        wm.removeView(icon_search);
        wm.removeView(icon_unlock);
        seekBar = null;
        slider = null;
        banner = null;
        textClock = null;
        textDate = null;
        textSlide = null;
        textSlideShadow = null;
        point = null;
        icon_search = null;
        icon_unlock = null;
    }

    private void openPromo(){
        String url = slider.getCurrentSlider().getBundle().getString("url");
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    private void disableStatusBar() {
        WindowManager manager = ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE));

        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
        localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        localLayoutParams.gravity = Gravity.TOP;
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |

                // this is to enable the notification to receive touch events
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |

                // Draws over status bar
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        localLayoutParams.height = (int) (40 * getResources().getDisplayMetrics().scaledDensity);
        localLayoutParams.format = PixelFormat.TRANSPARENT;

        blockingView = new customViewGroup(this);
        manager.addView(blockingView, localLayoutParams);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void sendingRequestList(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        final StringRequest request = new StringRequest(Request.Method.GET,
                "http://128.199.109.128/gcm/lockscreen/getdata.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        LockscreenItem item;
                        try {
                            JSONArray jResponse = new JSONArray(response);
                            for (int i = 0; i < jResponse.length(); i++){
                                item = new LockscreenItem();
                                jItem = jResponse.getJSONObject(i);
                                item.setLink(jItem.getString("url"));
                                item.setKategori(jItem.getString("category"));
                                item.setBanner(jItem.getString("image"));
                                item.setPoint(jItem.getInt("point"));
                                items.add(item);
                            }

                            editor = pref.edit();
                            editor.putString(KEY_TANGGAL, tanggal);
                            editor.putInt(KEY_PROMO_NUMBER, jResponse.length());
                            editor.putString(KEY_JSON, response);
                            editor.commit();
                            renderSlider();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error conneciton cause : " + error.getMessage());
                        error.printStackTrace();
                    }
                });
        requestQueue.add(request);
    }

    private boolean checkInternet(){
        ConnectivityManager ConnectionManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=ConnectionManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private void renderSlider(){
        for (int j=0; j<items.size(); j++){
            textSliderView = new TextSliderView(OverlayShowingService.this);
            // initialize a SliderLayout
            textSliderView
                    .description("Promo " + (j+1))
                    .image(items.get(j).getBanner())
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(OverlayShowingService.this);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle().putString("extra","Promo " + (j+1));
            textSliderView.getBundle().putString("point","" + items.get(j).getPoint());
            textSliderView.getBundle().putString("kategori","" + items.get(j).getKategori());
            textSliderView.getBundle().putString("url","" + items.get(j).getLink());
            slider.addSlider(textSliderView);
        }

        slider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        slider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        slider.setCustomAnimation(new DescriptionAnimation());
        slider.addOnPageChangeListener(this);
        slider.stopAutoCycle();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        banner.setText(slider.getBundle().getString("extra"));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        banner.setText(" ");
        point.setText("Point : " + slider.getCurrentSlider().getBundle().getString("point"));
    }

    @Override
    public void onPageSelected(int position) {}

    @Override
    public void onPageScrollStateChanged(int state) {}


}
