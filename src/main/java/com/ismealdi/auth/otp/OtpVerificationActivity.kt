package com.ismealdi.auth.otp

import android.os.Bundle
import com.ismealdi.auth.R
import com.ismealdi.auth.databinding.ViewOtpVerificationBinding
import com.ismealdi.meepopup.base.AmActivity
import java.util.*
import kotlin.concurrent.timerTask

class OtpVerificationActivity : AmActivity<ViewOtpVerificationBinding>(R.layout.view_otp_verification) {

    private var resendTimer = Timer()

    override fun initView(savedInstanceState: Bundle?) {
        pageBack(true)
        pageTitle(getString(R.string.title_otp))

        setTimer()
    }

    private fun setTimer() {
        var count = 60

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

    override fun onBackPressed() {
        resendTimer.cancel()
        finish()
    }

}