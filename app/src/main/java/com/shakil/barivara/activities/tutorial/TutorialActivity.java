package com.shakil.barivara.activities.tutorial;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.ViewPager;

import com.shakil.barivara.R;
import com.shakil.barivara.activities.onboard.MainActivity;
import com.shakil.barivara.databinding.ActivityTutorialBinding;
import com.shakil.barivara.utils.MyViewPagerAdapter;

public class TutorialActivity extends AppCompatActivity {
    private ActivityTutorialBinding activityTutorialBinding;
    private MyViewPagerAdapter myViewPagerAdapter;
    private TextView[] dots;
    private int[] layouts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        activityTutorialBinding = DataBindingUtil.setContentView(this, R.layout.activity_tutorial);

        //layouts of all welcome sliders
        layouts = new int[]{
                R.layout.welcome_add_tenant_first,
                R.layout.welcome_assign_room_to_a_tenant,
                R.layout.welcome_calculate_electricity_rent_amount,
                R.layout.welcome_overall_dashboard};

        addBottomDots(0);
        changeStatusBarColor();

        myViewPagerAdapter = new MyViewPagerAdapter(this, layouts);
        activityTutorialBinding.viewPager.setAdapter(myViewPagerAdapter);
        activityTutorialBinding.viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        activityTutorialBinding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking for last page
                // if last page home screen will be launched
                int current = getItem(+1);
                if (current < layouts.length) {
                    // move to next screen
                    activityTutorialBinding.viewPager.setCurrentItem(current);
                }
                else{
                    Toast.makeText(TutorialActivity.this, getString(R.string.end), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //region bottom dots
    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        activityTutorialBinding.layoutDots.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            activityTutorialBinding.layoutDots.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[currentPage].setTextColor(colorsActive[currentPage]);
        }
    }
    //endregion

    private int getItem(int i) {
        return activityTutorialBinding.viewPager.getCurrentItem() + i;
    }

    //region viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.length - 1) {
                // last page. make button text to GOT IT
                activityTutorialBinding.btnNext.setText(getString(R.string.got_it));
            } else {
                // still pages are left
                activityTutorialBinding.btnNext.setText(getString(R.string.next));
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };
    //endregion

    //region Making notification bar transparent
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.md_white_1000));
        }
    }
    //endregion

    //region option menu, back press and broadcast register
    @Override
    public void onBackPressed() {
        startActivity(new Intent(TutorialActivity.this, MainActivity.class));
    }
    //endregion
}