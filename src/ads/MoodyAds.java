package ads;

import model.ModConstants;
import android.app.Activity;
import android.content.Context;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.revmob.RevMob;

/**
 * @author fbarrei1
 * 
 *         Admob class
 * 
 */
public class MoodyAds {
	Context context;

	public MoodyAds(Context context) {
		this.context = context;
	}

	/**
	 * 
	 * This method will create a admob adView which you can add to your layout
	 * 
	 * @return AdView
	 */
	public AdView createAdmobSmartBanner() {
		// Create adView.
		AdView adMobView = new AdView(context);

		adMobView.setAdUnitId(ModConstants.MY_ADMOB_UNIT_ID);
		adMobView.setAdSize(AdSize.SMART_BANNER);

		// Init generic builder
		// AdRequest adRequest = new AdRequest.Builder().build();

		// Test Mode
		AdRequest adRequest = new AdRequest.Builder().addTestDevice(ModConstants.ADS_TEST_DEVICE_ID).build();

		// Load ads to the adView
		adMobView.loadAd(adRequest);

		return adMobView;
	}

	/**
	 * 
	 * This method will create and return interstitialAd view you can use it and
	 * add Listeners etc etc etc
	 * 
	 * @return InterstitialAd
	 */
	public InterstitialAd createAdmobInterstitial() {
		// Create the interstitial.
		InterstitialAd interstitial = new InterstitialAd(context);

		interstitial.setAdUnitId(ModConstants.MY_ADMOB_UNIT_ID);

		// Create ad request.
		// AdRequest adRequest = new AdRequest.Builder().build();

		// Test Mode
		AdRequest adRequest = new AdRequest.Builder().addTestDevice(ModConstants.ADS_TEST_DEVICE_ID).build();

		// Begin loading your interstitial.
		interstitial.loadAd(adRequest);

		return interstitial;
	}

	/**
	 * 
	 * This method will create and display the Revmob insterstatial simple as
	 * that, just invoke it
	 * 
	 * @return InterstitialAd
	 */
	public void createRevmobInterstitial() {
		RevMob revmob = RevMob.start((Activity) context);
		revmob.showFullscreen((Activity) context);
	}

}
