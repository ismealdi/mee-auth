package com.ismealdi.auth.signin

import android.content.Intent
import android.os.Bundle
import com.ismealdi.auth.R
import com.ismealdi.auth.databinding.ViewSignInBinding
import com.ismealdi.auth.signup.SignUpActivity
import com.ismealdi.meepopup.base.AmActivity
import kotlinx.android.synthetic.main.view_sign_in.*

class SignInActivity : AmActivity<ViewSignInBinding>(R.layout.view_sign_in) {

    override fun initView(savedInstanceState: Bundle?) {

    }


    override fun listener() {
        super.listener()

        buttonSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)

            startActivity(intent)
        }
    }
}