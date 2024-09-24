package com.shakil.barivara.presentation.tutorial

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.widget.TextView
import android.widget.Toast
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.shakil.barivara.BaseActivity
import com.shakil.barivara.R
import com.shakil.barivara.databinding.ActivityTutorialBinding
import com.shakil.barivara.presentation.onboard.HomeActivity
import com.shakil.barivara.utils.MyViewPagerAdapter

class TutorialActivity : BaseActivity<ActivityTutorialBinding>() {
    private lateinit var activityTutorialBinding: ActivityTutorialBinding
    private var myViewPagerAdapter: MyViewPagerAdapter? = null
    private lateinit var dots: Array<TextView?>
    private val layouts = intArrayOf(
        R.layout.welcome_add_tenant_first,
        R.layout.welcome_assign_room_to_a_tenant,
        R.layout.welcome_calculate_electricity_rent_amount,
        R.layout.welcome_overall_dashboard
    )
    override val layoutResourceId: Int
        get() = R.layout.activity_tutorial

    override fun setVariables(dataBinding: ActivityTutorialBinding) {
        activityTutorialBinding = dataBinding
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addBottomDots(0)
        myViewPagerAdapter = MyViewPagerAdapter(this, layouts)
        activityTutorialBinding.viewPager.adapter = myViewPagerAdapter
        activityTutorialBinding.viewPager.addOnPageChangeListener(viewPagerPageChangeListener)
        activityTutorialBinding.btnNext.setOnClickListener {
            val current = getItem(+1)
            if (current < layouts.size) {
                activityTutorialBinding.viewPager.currentItem = current
            } else if (current == layouts.size) {
                Toast.makeText(
                    this@TutorialActivity,
                    getString(R.string.finish),
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(Intent(this@TutorialActivity, HomeActivity::class.java))
            }
        }
    }

    private fun addBottomDots(currentPage: Int) {
        dots = arrayOfNulls(layouts.size)
        val colorsActive = resources.getIntArray(R.array.array_dot_active)
        val colorsInactive = resources.getIntArray(R.array.array_dot_inactive)
        activityTutorialBinding.layoutDots.removeAllViews()
        for (i in dots.indices) {
            dots[i] = TextView(this)
            dots[i]?.text = Html.fromHtml("&#8226;")
            dots[i]?.textSize = 35f
            dots[i]?.setTextColor(colorsInactive[currentPage])
            activityTutorialBinding.layoutDots.addView(dots[i])
        }
        if (dots.isNotEmpty()) {
            dots[currentPage]?.setTextColor(colorsActive[currentPage])
        }
    }

    private fun getItem(i: Int): Int {
        return activityTutorialBinding.viewPager.currentItem + i
    }

    private var viewPagerPageChangeListener: OnPageChangeListener = object : OnPageChangeListener {
        override fun onPageSelected(position: Int) {
            addBottomDots(position)
            if (position == layouts.size - 1) {
                activityTutorialBinding.btnNext.text = getString(R.string.got_it)
            } else {
                activityTutorialBinding.btnNext.text = getString(R.string.next)
            }
        }

        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
        override fun onPageScrollStateChanged(arg0: Int) {}
    }
}