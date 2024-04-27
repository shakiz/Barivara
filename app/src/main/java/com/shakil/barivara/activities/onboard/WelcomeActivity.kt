package com.shakil.barivara.activities.onboard;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.ViewPager;

import com.shakil.barivara.R;
import com.shakil.barivara.activities.auth.LoginActivity;
import com.shakil.barivara.databinding.ActivityWelcomeBinding;
import com.shakil.barivara.utils.MyViewPagerAdapter;
import com.shakil.barivara.utils.PrefManager;

import static com.shakil.barivara.utils.Constants.mOldUser;

public class WelcomeActivity extends AppCompatActivity {
    private ActivityWelcomeBinding activityWelcomeBinding;
    private MyViewPagerAdapter myViewPagerAdapter;
    private PrefManager prefManager;
    private TextView[] dots;
    private int[] layouts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        activityWelcomeBinding = DataBindingUtil.setContentView(this, R.layout.activity_welcome);

        init();

        layouts = new int[]{
                R.layout.welcome_about_app,
                R.layout.welcome_add_tenant_first,
                R.layout.welcome_assign_room_to_a_tenant,
                R.layout.welcome_calculate_electricity_rent_amount,
                R.layout.welcome_overall_dashboard};

        addBottomDots(0);
        changeStatusBarColor();

        myViewPagerAdapter = new MyViewPagerAdapter(this, layouts);
        activityWelcomeBinding.viewPager.setAdapter(myViewPagerAdapter);
        activityWelcomeBinding.viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        activityWelcomeBinding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = getItem(+1);
                if (current < layouts.length) {
                    activityWelcomeBinding.viewPager.setCurrentItem(current);
                } else {
                    goLogin();
                }
            }
        });
    }

    private void init() {
        prefManager = new PrefManager(this);
    }

    private void goLogin() {
        prefManager.set(mOldUser, true);
        startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        activityWelcomeBinding.layoutDots.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            activityWelcomeBinding.layoutDots.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[currentPage].setTextColor(colorsActive[currentPage]);
        }
    }

    private int getItem(int i) {
        return activityWelcomeBinding.viewPager.getCurrentItem() + i;
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
            if (position == layouts.length - 1) {
                activityWelcomeBinding.btnNext.setText(getString(R.string.finish));
            } else {
                activityWelcomeBinding.btnNext.setText(getString(R.string.next));
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.md_white_1000));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}