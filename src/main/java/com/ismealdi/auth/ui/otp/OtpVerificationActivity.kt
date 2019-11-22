package com.ismealdi.auth.ui.otp

import android.app.Activity
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.ismealdi.auth.R
import com.ismealdi.auth.databinding.ViewOtpVerificationBinding
import com.ismealdi.meepopup.base.AmActivity
import com.ismealdi.meepopup.base.AmApplication
import com.ismealdi.meepopup.schema.User
import com.ismealdi.meepopup.util.common.Constants
import com.ismealdi.meepopup.util.common.Logs
import com.ismealdi.meepopup.util.common.getRandomNumberString
import kotlinx.android.synthetic.main.view_otp_verification.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timerTask


class OtpVerificationActivity : AmActivity<ViewOtpVerificationBinding>(R.layout.view_otp_verification) {

    private var resendTimer = Timer()
    private var verificationId = ""
    private var verificationToken: PhoneAuthProvider.ForceResendingToken? = null
    private var user: User? = null
    private var resend = 0

    override fun initView(savedInstanceState: Bundle?) {
        pageBack(true)
        pageTitle(getString(R.string.title_otp))

        handleIntent()
        setTimer()
    }

    override fun handleIntent() {
        super.handleIntent()

        intent?.let { data ->
            data.getSerializableExtra(Constants.INTENT.DATA.SIGN_UP.user)?.let { userData ->
                user = userData as User
                AmApplication.amPhone.verifyPhoneNumber("+62${user?.phone}", 60, TimeUnit.SECONDS, this, callbacks)

                setTimer()
            }
        }
    }

    override fun listener() {
        super.listener()

        binding.inputPhone.addTextChangedListener {
            if(it?.length == 6) {
                dialogLoader(true)
                val credential = PhoneAuthProvider.getCredential(verificationId, it.toString())
                signInWithPhoneAuthCredential(credential)
            }
        }

        binding.buttonResend.setOnClickListener {
            if(binding.buttonResend.text == getString(R.string.text_ganti_nomor)) {
                intent.putExtra(Constants.INTENT.DATA.SIGN_UP.changePhone, true)
                setResult(Activity.RESULT_OK, intent)
                finish()

                return@setOnClickListener
            }

            verificationToken?.let { token ->
                resendVerificationCode("+62${user?.phone}", token)
                inputPhone.setText("")

                if(resend == 1) {
                    binding.buttonResend.text = getString(R.string.text_ganti_nomor)
                    resend = 0
                }else{
                    resendTimer.cancel()
                    resendTimer = Timer()

                    setTimer()
                    resend++
                }
            }
        }
    }

    override fun onBackPressed() {
        resendTimer.cancel()
        finish()
    }

    private fun setTimer() {
        var count = 60

        binding.buttonResend.isEnabled = false

        resendTimer.scheduleAtFixedRate(timerTask {
            runOnUiThread {
                count--

                binding.buttonResend.text = getString(R.string.text_kirim_ulang_count, count.toString())

                if(count == 0) {
                    binding.buttonResend.isEnabled = true
                    binding.buttonResend.text = getString(R.string.text_kirim_ulang)
                    resendTimer.cancel()
                }
            }

        }, 0, 1000)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        AmApplication.amAuth.currentUser?.linkWithCredential(credential)?.addOnCompleteListener(this) { task ->
            dialogLoader(false)

            if (task.isSuccessful) {
                finishAffinity()
            }else{
                message("failed")
            }
        }
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Logs.i("onVerificationCompleted:$credential")
            inputPhone.setText(credential.smsCode ?: getRandomNumberString())
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            dialogLoader(false)

            Logs.e("onVerificationFailed $e")

            if (e is FirebaseAuthInvalidCredentialsException) {
                Logs.e("Invalid request")
            } else if (e is FirebaseTooManyRequestsException) {
                Logs.e("The SMS quota for the project has been exceeded")
            }
        }

        override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
            dialogLoader(false)

            verificationId = id
            verificationToken = token

            Logs.e( "onCodeSent:$verificationId")
            message("Success, $token and $verificationId")
        }
    }

    private fun resendVerificationCode(phoneNumber: String, token: PhoneAuthProvider.ForceResendingToken?) {
        dialogLoader(true)
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, this, callbacks, token)
    }

}