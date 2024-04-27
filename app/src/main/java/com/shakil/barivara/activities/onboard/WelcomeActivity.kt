package com.shakil.barivara.activities.onboard

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.shakil.barivara.R
import com.shakil.barivara.activities.auth.LoginActivity
import com.shakil.barivara.databinding.ActivityWelcomeBinding
import com.shakil.barivara.utils.Constants
import com.shakil.barivara.utils.MyViewPagerAdapter
import com.shakil.barivara.utils.PrefManager

class WelcomeActivity : AppCompatActivity() {
    private lateinit var activityWelcomeBinding: ActivityWelcomeBinding
    private var myViewPagerAdapter: MyViewPagerAdapter? = null
    private var prefManager = PrefManager(this)
    private lateinit var dots: Array<TextView?>
    private var layouts = intArrayOf(
        R.layout.welcome_about_app,
        R.layout.welcome_add_tenant_first,
        R.layout.welcome_assign_room_to_a_tenant,
        R.layout.welcome_calculate_electricity_rent_amount,
        R.layout.welcome_overall_dashboard
    )
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= 21) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        activityWelcomeBinding = DataBindingUtil.setContentView(this, R.layout.activity_welcome)
        init()
        layouts = intArrayOf(
            R.layout.welcome_about_app,
            R.layout.welcome_add_tenant_first,
            R.layout.welcome_assign_room_to_a_tenant,
            R.layout.welcome_calculate_electricity_rent_amount,
            R.layout.welcome_overall_dashboard
        )
        addBottomDots(0)
        changeStatusBarColor()
        myViewPagerAdapter = MyViewPagerAdapter(this, layouts)
        activityWelcomeBinding.viewPager.adapter = myViewPagerAdapter
        activityWelcomeBinding.viewPager.addOnPageChangeListener(viewPagerPageChangeListener)
        activityWelcomeBinding.btnNext.setOnClickListener {
            val current = getItem(+1)
            if (current < layouts.size) {
                activityWelcomeBinding.viewPager.currentItem = current
            } else {
                goLogin()
            }
        }
    }

    private fun init() {
        prefManager = PrefManager(this)
    }

    private fun goLogin() {
        prefManager[Constants.mOldUser] = true
        startActivity(Intent(this@WelcomeActivity, LoginActivity::class.java))
    }

    private fun addBottomDots(currentPage: Int) {
        dots = arrayOfNulls(layouts.size)
        val colorsActive = resources.getIntArray(R.array.array_dot_active)
        val colorsInactive = resources.getIntArray(R.array.array_dot_inactive)
        activityWelcomeBinding.layoutDots.removeAllViews()
        for (i in dots.indices) {
            dots[i] = TextView(this)
            dots[i]?.text = Html.fromHtml("&#8226;")
            dots[i]?.textSize = 35f
            dots[i]?.setTextColor(colorsInactive[currentPage])
            activityWelcomeBinding.layoutDots.addView(dots[i])
        }
        if (dots.isNotEmpty()) {
            dots[currentPage]?.setTextColor(colorsActive[currentPage])
        }
    }

    private fun getItem(i: Int): Int {
        return activityWelcomeBinding.viewPager.currentItem + i
    }

    private var viewPagerPageChangeListener: OnPageChangeListener = object : OnPageChangeListener {
        override fun onPageSelected(position: Int) {
            addBottomDots(position)
            if (position == layouts.size - 1) {
                activityWelcomeBinding.btnNext.text = getString(R.string.finish)
            } else {
                activityWelcomeBinding.btnNext.text = getString(R.string.next)
            }
        }

        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
        override fun onPageScrollStateChanged(arg0: Int) {}
    }

    private fun changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = resources.getColor(R.color.md_white_1000)
        }
    }
}