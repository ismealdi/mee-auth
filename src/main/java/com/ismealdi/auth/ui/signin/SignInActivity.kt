package com.ismealdi.auth.ui.signin

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import com.ismealdi.auth.R
import com.ismealdi.auth.databinding.ViewSignInBinding
import com.ismealdi.auth.ui.signup.SignUpActivity
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

        buttonSignIn.setOnClickListener {
            if(validate()) {
                signIn(inputPhone.text.toString())
            }
        }
    }

    private fun signIn(number: String) {
        val phone = "+62$number"


    }

    private fun validate(): Boolean {
        return TextUtils.isEmpty(inputPhone.text.toString())
    }
}