package com.shakil.barivara.activities.tutorial;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
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
        activityTutorialBinding = DataBindingUtil.setContentView(this, R.layout.activity_tutorial);

        layouts = new int[]{
                R.layout.welcome_add_tenant_first,
                R.layout.welcome_assign_room_to_a_tenant,
                R.layout.welcome_calculate_electricity_rent_amount,
                R.layout.welcome_overall_dashboard};

        addBottomDots(0);

        myViewPagerAdapter = new MyViewPagerAdapter(this, layouts);
        activityTutorialBinding.viewPager.setAdapter(myViewPagerAdapter);
        activityTutorialBinding.viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        activityTutorialBinding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = getItem(+1);
                if (current < layouts.length) {
                    activityTutorialBinding.viewPager.setCurrentItem(current);
                }
                else if (current == layouts.length){
                    Toast.makeText(TutorialActivity.this, getString(R.string.finish), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(TutorialActivity.this, MainActivity.class));
                }
            }
        });
    }

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

    private int getItem(int i) {
        return activityTutorialBinding.viewPager.getCurrentItem() + i;
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
            if (position == layouts.length - 1) {
                activityTutorialBinding.btnNext.setText(getString(R.string.got_it));
            } else {
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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(TutorialActivity.this, MainActivity.class));
    }
}