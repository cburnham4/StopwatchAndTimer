package letshangllc.timer;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.amazon.device.ads.Ad;
import com.amazon.device.ads.AdError;
import com.amazon.device.ads.AdListener;
import com.amazon.device.ads.AdProperties;
import com.amazon.device.ads.AdRegistration;

import java.util.*;

/**
 * Created by cvburnha on 10/26/2015.
 */
public class AdsHelper implements AdListener{
    String amazon_id;
    String admob_id;

    Activity activity;
    View view;
    private boolean amazonAdEnabled;

    private ViewGroup adViewContainer;
    private com.amazon.device.ads.AdLayout amazonAdView;
    private com.google.android.gms.ads.AdView admobAdView;

    public AdsHelper(View view, String admob_id, Activity activity){
        this.admob_id = admob_id;
        this.amazon_id = activity.getResources().getString(R.string.amazon_ad_id);
        this.activity = activity;
        this.view =view;
    }

    public void runAds(){
        this.setUpAds();
        int delay = 1000; // delay for 1 sec.
        int period = 10000; // repeat every 4 sec.
        java.util.Timer timer = new java.util.Timer();
        timer.scheduleAtFixedRate(new TimerTask()
        {
            public void run()
            {
                refreshAd();  // display the data
            }
        }, delay, period);
    }

    private void setUpAds(){
        AdRegistration.setAppKey(amazon_id);
        amazonAdView = new com.amazon.device.ads.AdLayout(activity, com.amazon.device.ads.AdSize.SIZE_320x50);
        amazonAdView.setListener(this);
        AdRegistration.enableTesting(true);
        admobAdView = new com.google.android.gms.ads.AdView(activity);
        admobAdView.setAdSize(com.google.android.gms.ads.AdSize.BANNER);
        admobAdView.setAdUnitId(admob_id);

        // Initialize view container
        adViewContainer = (ViewGroup) view.findViewById(R.id.ad_layout);
        amazonAdEnabled = true;
        adViewContainer.addView(amazonAdView);

        amazonAdView.loadAd(new com.amazon.device.ads.AdTargetingOptions());
    }


    public void refreshAd()
    {
        amazonAdView.loadAd(new com.amazon.device.ads.AdTargetingOptions());
    }

    @Override
    public void onAdLoaded(Ad ad, AdProperties adProperties) {
        if (!amazonAdEnabled)
        {
            amazonAdEnabled = true;
            adViewContainer.removeView(admobAdView);
            adViewContainer.addView(amazonAdView);
        }
    }

    @Override
    public void onAdFailedToLoad(Ad ad, AdError adError) {
        // Call AdMob SDK for backfill
        if (amazonAdEnabled)
        {
            amazonAdEnabled = false;
            adViewContainer.removeView(amazonAdView);
            adViewContainer.addView(admobAdView);
        }
//        AdRequest.Builder.addTestDevice("04CD51A7A1F806B7F55CADD6A3B84E92");
        admobAdView.loadAd((new com.google.android.gms.ads.AdRequest.Builder()).build());
    }

    @Override
    public void onAdExpanded(Ad ad) {

    }

    @Override
    public void onAdCollapsed(Ad ad) {

    }

    @Override
    public void onAdDismissed(Ad ad) {

    }

    public void onDestroy()
    {
        this.amazonAdView.destroy();
    }

    public void onPause(){
        this.amazonAdView.destroy();
    }

    public void onResume(){
        this.setUpAds();
    }
}
