package letshangllc.timer;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {
    private static final int TAB_STOPWATCH = 0;
    private static final int TAB_TIMER = 1;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private AdsHelper adsHelper;
    private java.util.Timer adRefreshTimer;
    private String stopwatchAdUnitId;
    private String timerAdUnitId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        applyWindowInsets();

        stopwatchAdUnitId = getString(R.string.admob_stopwatch_id);
        timerAdUnitId = getString(R.string.admob_timer_id);

        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        // Default tab is Stopwatch → stopwatch_banner_ad
        adsHelper = new AdsHelper(getWindow().getDecorView(), stopwatchAdUnitId, this);
        adsHelper.setUpAds();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (adsHelper == null) {
                    return;
                }
                int position = tab.getPosition();
                if (position == TAB_TIMER) {
                    adsHelper.setAdUnitId(timerAdUnitId);
                } else {
                    adsHelper.setAdUnitId(stopwatchAdUnitId);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        int delay = 1000;
        int period = getResources().getInteger(R.integer.ad_refresh_rate);
        adRefreshTimer = new java.util.Timer();
        adRefreshTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (adsHelper != null) {
                    adsHelper.refreshAd();
                }
            }
        }, delay, period);
    }

    private void applyWindowInsets() {
        final View root = findViewById(R.id.root_layout);
        final View adLayout = findViewById(R.id.ad_layout);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(insets.left, insets.top, insets.right, 0);
            if (adLayout != null) {
                ViewGroup.MarginLayoutParams lp =
                        (ViewGroup.MarginLayoutParams) adLayout.getLayoutParams();
                lp.bottomMargin = insets.bottom;
                adLayout.setLayoutParams(lp);
            }
            return WindowInsetsCompat.CONSUMED;
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new StopwatchFragment(), "Stopwatch");
        adapter.addFragment(new Timer(), "Timer");
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onPause() {
        if (adsHelper != null) {
            adsHelper.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adsHelper != null) {
            adsHelper.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        if (adRefreshTimer != null) {
            adRefreshTimer.cancel();
            adRefreshTimer = null;
        }
        if (adsHelper != null) {
            adsHelper.onDestroy();
        }
        super.onDestroy();
    }

    static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
