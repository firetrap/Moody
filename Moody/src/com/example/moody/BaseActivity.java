package com.example.moody;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.view.Menu;

import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;


public class BaseActivity extends com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity {
	
	private int mTitleRes;
	protected ListFragment mFrag;
	
	public BaseActivity(int titleRes){
		mTitleRes = titleRes;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setTitle(mTitleRes);
		
		setBehindContentView(R.layout.menu_frame);
		FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
		mFrag = new RandomList();
		ft.replace(R.id.menu_frame, mFrag);
		ft.commit();
		
		com.jeremyfeinstein.slidingmenu.lib.SlidingMenu sm = getSlidingMenu();
		
//		sm.setMode(SlidingMenu.LEFT);
		sm.setShadowWidth(10);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindOffset(60);
		sm.setFadeDegree(0.35f);
		sm.setTouchModeAbove(com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.TOUCHMODE_FULLSCREEN);
		
		// estas duas linhas abaixo sao o icon como botao
//		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//		getSupportActionBar().setIcon(R.drawable.ic_launcher);

	}
	
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item){
//		switch(item.getItemId()){
//		case android.R.id.home:
//			toggle();
//			return true;
//		}
//		return onOptionsItemSelected(item);
//	}
	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getSupportMenuInflater().inflate(R.menu.activity_main, menu);
//		return true;
//	}
	
	public class BasePagerAdapter extends FragmentPagerAdapter{
		private List<Fragment> mFragments = new ArrayList<Fragment>();
		private ViewPager mPager;
		
		public BasePagerAdapter(FragmentManager fm, ViewPager vp){
			super(fm);
			mPager = vp;
			mPager.setAdapter(this);
			for (int i = 0; i < 3; i++){
				addTab(new RandomList());
			}
		}
		
		public void addTab(Fragment frag){
			mFragments.add(frag);
		}
		
		@Override
		public Fragment getItem(int position){
			return mFragments.get(position);
		}
		
		@Override
		public int getCount(){
			return mFragments.size();
		}
	}

}
