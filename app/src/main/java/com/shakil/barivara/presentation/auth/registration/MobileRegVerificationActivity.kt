package com.shakil.barivara.presentation.auth.registration

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.shakil.barivara.BaseActivity
import com.shakil.barivara.R
import com.shakil.barivara.presentation.onboard.MainActivity
import com.shakil.barivara.databinding.ActivityMobileRegVerificationBinding
import com.shakil.barivara.utils.Constants
import com.shakil.barivara.utils.PrefManager
import com.shakil.barivara.utils.Tools
import com.shakil.barivara.utils.UX
import es.dmoral.toasty.Toasty
import java.util.concurrent.TimeUnit

class MobileRegVerificationActivity : BaseActivity<ActivityMobileRegVerificationBinding>() {
    private lateinit var activityBinding: ActivityMobileRegVerificationBinding
    private var mobileNumber: String? = ""
    private var mVerificationId = ""
    private var firebaseAuth = FirebaseAuth.getInstance()
    private var tools = Tools(this)
    private var ux: UX? = null
    override val layoutResourceId: Int
        get() = R.layout.activity_mobile_reg_verification

    override fun setVariables(dataBinding: ActivityMobileRegVerificationBinding) {
        activityBinding = dataBinding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_mobile_reg_verification)
        initUI()
        getIntentData()
        bindUIWithComponents()
    }

    private fun initUI() {
        ux = UX(this)
    }

    private fun bindUIWithComponents() {
        activityBinding.sentCodeHintText.text =
            getString(R.string.sent_you_code_on_your_number) + "(" + mobileNumber + ")"
        activityBinding.verify.setOnClickListener {
            if (!TextUtils.isEmpty(activityBinding.verificationCode.text.toString())) {
                if (activityBinding.verificationCode.text.toString().length < 6) {
                    Toasty.error(
                        this@MobileRegVerificationActivity,
                        getString(R.string.not_valid_otp_or_code),
                        Toast.LENGTH_LONG,
                        true
                    ).show()
                } else {
                    //verifying the code entered manually
                    verifyVerificationCode(activityBinding.verificationCode.text.toString())
                }
            } else {
                activityBinding.verificationCode.requestFocus()
                activityBinding.verificationCode.error =
                    getString(R.string.otp_or_code_can_not_be_empty)
            }
        }
    }

    private fun getIntentData() {
        if (intent.getStringExtra("mobile") != null) {
            mobileNumber = intent.getStringExtra("mobile")
            if (tools.hasConnection()) {
                Toasty.info(
                    this, """
     ${getString(R.string.please_wait)}
     ${getString(R.string.we_are_verifying_you)}
     """.trimIndent(), Toast.LENGTH_LONG, true
                ).show()
                sendVerificationCode(mobileNumber)
            } else {
                Snackbar.make(
                    findViewById(R.id.parent),
                    getString(R.string.no_internet_title),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun sendVerificationCode(mobile: String?) {
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber("+88$mobile") // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(object : OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                    val code = phoneAuthCredential.smsCode
                    if (code != null) {
                        Log.i(Constants.TAG + ":VerificationCompleted", "Code::$code")
                        activityBinding.verificationCode.setText(code)
                        verifyVerificationCode(code)
                    }
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Log.i(Constants.TAG + ":onVerificationFailed", "Error::" + e.message)
                    Toasty.error(
                        this@MobileRegVerificationActivity, """
     ${getString(R.string.code_verification_failed)}
     ${getString(R.string.try_again)}
     """.trimIndent(), Toast.LENGTH_LONG, true
                    ).show()
                }

                override fun onCodeSent(s: String, forceResendingToken: ForceResendingToken) {
                    Log.i(Constants.TAG + ":onCodeSent", "Verification ID::$s")
                    Toasty.info(
                        this@MobileRegVerificationActivity,
                        getString(R.string.code_sent_please_check),
                        Toast.LENGTH_LONG,
                        true
                    ).show()
                    super.onCodeSent(s, forceResendingToken)
                    //storing the verification id that is sent to the user
                    mVerificationId = s
                }
            }) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(
            options
        )
    }

    private fun verifyVerificationCode(code: String) {
        if (tools.hasConnection()) {
            Log.i(Constants.TAG + ":verifyVerificationCode", "Code:$code")
            try {
                val credential = PhoneAuthProvider.getCredential(mVerificationId, code)
                loginWithMobile(credential)
            } catch (e: Exception) {
                Log.i(
                    Constants.TAG + ":verifyVerificationCode",
                    e.message ?: "verifyVerificationCode() error"
                )
            }
        } else {
            val snackbar = Snackbar.make(
                findViewById(R.id.parent),
                getString(R.string.no_internet_title),
                Snackbar.LENGTH_LONG
            )
            snackbar.show()
        }
    }

    private fun loginWithMobile(credential: PhoneAuthCredential) {
        ux?.getLoadingView()
        firebaseAuth?.signInWithCredential(credential)
            ?.addOnCompleteListener(this@MobileRegVerificationActivity) { task ->
                if (task.isSuccessful) {
                    Log.i(Constants.TAG + ":loginWithMobile", "Success")
                    tools.setLoginPrefs(task, PrefManager(this@MobileRegVerificationActivity))
                    ux?.removeLoadingView()
                    Toasty.success(
                        this@MobileRegVerificationActivity,
                        getString(R.string.login_succcessful),
                        Toast.LENGTH_LONG,
                        true
                    ).show()
                    val intent =
                        Intent(this@MobileRegVerificationActivity, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Log.i(Constants.TAG + ":loginWithMobile", "Failed")
                    ux?.removeLoadingView()
                    var message = getString(R.string.login_unsucccessful)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        message = """
                        
                        ${getString(R.string.invalid_code_entered)}
                        """.trimIndent()
                    }
                    Toasty.error(
                        this@MobileRegVerificationActivity,
                        message,
                        Toast.LENGTH_LONG,
                        true
                    ).show()
                }
            }
    }
}