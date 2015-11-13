package com.grazerss.util;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import com.google.ads.AdViewListener;
import com.google.ads.GoogleAdView;
import com.grazerss.EntryManager;
import com.grazerss.NewsRob;
import com.grazerss.PL;
import com.grazerss.R;
import com.grazerss.activities.AbstractNewsRobListActivity;

public class GoogleAdsUtil
{

  private EntryManager entryManager;

  public GoogleAdsUtil(EntryManager entryManager)
  {
    this.entryManager = entryManager;
  }

  boolean shouldShowAd(Activity owningActivity)
  {

    if (true)
      return false;

    final DisplayMetrics dm = new DisplayMetrics();
    owningActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);

    final int dpHeight = (int) (dm.heightPixels / dm.density);
    final int dpWidth = (int) (dm.widthPixels / dm.density);

    final boolean isWithinProperDimensions = !(dpHeight < 400 || dpWidth > 600);

    final boolean isNetworkConnected = entryManager.isNetworkConnected(owningActivity);

    final boolean shouldShowAd = isWithinProperDimensions && isNetworkConnected;
    if (NewsRob.isDebuggingEnabled(owningActivity))
      PL.log("GoogleAdsUtil: shouldShowAd() withinDimension=" + isWithinProperDimensions + " networkConnected=" + isNetworkConnected
          + " result=" + shouldShowAd, owningActivity);
    return shouldShowAd;
  }

  public void showAds(final Activity owningActivity)
  {

    if (true)
      return;

    PL.log("GoogleAdsUtil: showAds.", owningActivity);
    if (!entryManager.shouldAdsBeShown())
      return;

    GoogleAdView av = null; // (GoogleAdView)
                            // owningActivity.findViewById(com.grazerss.R.id.adview);

    if (av == null && shouldShowAd(owningActivity))
    {
      ViewGroup parent = (ViewGroup) owningActivity.findViewById(R.id.ad_parent);
      owningActivity.getLayoutInflater().inflate(R.layout.ad, parent);

      av = null; // (GoogleAdView)
                 // owningActivity.findViewById(com.grazerss.R.id.adview);
      av.setAutoRefreshSeconds(180);
      av.showAds(entryManager.getAdSenseSpec());
      // move ad container to the bottom
      if (owningActivity instanceof AbstractNewsRobListActivity && EntryManager.ACTION_BAR_BOTTOM.equals(entryManager.getActionBarLocation()))
      {

        ViewGroup grandParent = (ViewGroup) parent.getParent();

        grandParent.removeView(parent);
        grandParent.addView(parent, grandParent.getChildCount() - 1);
      }

    }

    final GoogleAdView adView = av;

    if (adView != null)
    {
      final ViewGroup parent = (ViewGroup) av.getParent();
      if (shouldShowAd(owningActivity))
      {
        // adView.setVisibility(View.VISIBLE);
      }
      else
      {

        parent.setVisibility(View.GONE);
        return;
      }

      if (adView.getAdViewListener() == null)
      {
        adView.setAdViewListener(new AdViewListener()
        {

          @Override
          public void onStartFetchAd()
          {
            parent.setVisibility(View.GONE);
          }

          @Override
          public void onFinishFetchAd()
          {
            if (shouldShowAd(owningActivity))
              parent.setVisibility(View.VISIBLE);
            else
              parent.setVisibility(View.GONE);
          }

          @Override
          public void onAdFetchFailure()
          {
            parent.setVisibility(View.GONE);
          }

          @Override
          public void onClickAd()
          {
          }
        });
      }

    }
  }

  public void hideAds(Activity articleActivity)
  {
    if (true)
      return;

    GoogleAdView adView = null;// (GoogleAdView)
                               // articleActivity.findViewById(R.id.adview);
    if (adView != null)
    {
      final ViewGroup parent = (ViewGroup) adView.getParent();
      parent.setVisibility(View.GONE);
      parent.removeView(adView);
      adView.setAdViewListener(null);
    }

  }

}
