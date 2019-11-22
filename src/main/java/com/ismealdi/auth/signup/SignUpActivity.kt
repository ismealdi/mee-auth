package com.ismealdi.auth.signup

import android.content.Intent
import android.os.Bundle
import com.ismealdi.auth.R
import com.ismealdi.auth.databinding.ViewSignUpBinding
import com.ismealdi.auth.otp.OtpVerificationActivity
import com.ismealdi.meepopup.base.AmActivity
import kotlinx.android.synthetic.main.view_sign_up.*

class SignUpActivity : AmActivity<ViewSignUpBinding>(R.layout.view_sign_up) {

    override fun initView(savedInstanceState: Bundle?) {
        pageBack(true)
        pageTitle(getString(R.string.title_sign_up))
    }

    override fun listener() {
        super.listener()

        buttonSignUp.setOnClickListener {
            val intent = Intent(this, OtpVerificationActivity::class.java)
            startActivity(intent)
        }
    }

}