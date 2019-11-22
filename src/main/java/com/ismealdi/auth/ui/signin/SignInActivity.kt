package com.ismealdi.auth.ui.signin

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import com.ismealdi.auth.R
import com.ismealdi.auth.databinding.ViewSignInBinding
import com.ismealdi.auth.ui.otp.OtpVerificationActivity
import com.ismealdi.auth.ui.signup.SignUpActivity
import com.ismealdi.meepopup.base.AmActivity
import com.ismealdi.meepopup.base.AmApplication
import com.ismealdi.meepopup.base.userPhone
import com.ismealdi.meepopup.schema.User
import com.ismealdi.meepopup.util.common.Constants
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
                checkPhone(inputPhone.text.toString())
            }else{
                message(getString(R.string.text_error_login))
            }
        }
    }

    private fun signIn(number: String) {
        val user = User("", "", number)

        val intent = Intent(this, OtpVerificationActivity::class.java)
        intent.putExtra(Constants.INTENT.DATA.SIGN_UP.user, user)

        startActivity(intent)
    }

    private fun checkPhone(number: String) {
        dialogLoader(true)

        AmApplication.amDatabase.userPhone(number).get().addOnCompleteListener {
            dialogLoader(false)
        }.addOnSuccessListener {
            if(it.documents.isEmpty()) {
                message(getString(R.string.text_user_not_found))
                return@addOnSuccessListener
            }

            it.documents.first()?.let { documentSnapshot ->
                documentSnapshot.toObject(User::class.java)?.let { userData ->
                    if(userData.phone == number) {
                        signIn(number)
                    }
                }
            }
        }.addOnFailureListener {
            message(it.message ?: "")
        }
    }

    private fun validate(): Boolean {
        return !TextUtils.isEmpty(inputPhone.text.toString())
    }
}