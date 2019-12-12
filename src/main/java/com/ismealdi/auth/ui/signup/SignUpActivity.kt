package com.ismealdi.auth.ui.signup

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import com.ismealdi.auth.R
import com.ismealdi.auth.ui.otp.OtpVerificationActivity
import com.ismealdi.meepopup.base.AmActivity
import com.ismealdi.meepopup.base.AmApplication
import com.ismealdi.meepopup.schema.User
import com.ismealdi.meepopup.util.common.Constants
import com.ismealdi.meepopup.util.common.Constants.INTENT.DATA.SIGN_UP.changePhone
import com.ismealdi.meepopup.util.common.Constants.INTENT.REQUEST.SIGN_UP_PHONE_VERIFICATION
import kotlinx.android.synthetic.main.view_sign_up.*

class SignUpActivity : AmActivity(R.layout.view_sign_up) {

    private var isChangePhone = false
    private var currentPhone = ""

    override fun initView(savedInstanceState: Bundle?) {
        pageBack(true)
        pageTitle(getString(R.string.title_sign_up))
    }

    override fun listener() {
        super.listener()

        buttonSignUp.setOnClickListener {
            if(validate()) {
                if(isChangePhone) {
                    if(currentPhone == inputPhone.text.toString()) {
                        message("Nomor telepon tidak boleh sama!")
                        return@setOnClickListener
                    }

                    goToOtp()
                }else{
                    signUp()
                }
            }else{
                message("Tolong di isikan seluruh data!")
            }
        }
    }

    private fun validate() : Boolean{
        if(TextUtils.isEmpty(inputEmail.text.toString()) ||
            TextUtils.isEmpty(inputName.text.toString()) ||
            TextUtils.isEmpty(inputPassword.text.toString()) ||
            TextUtils.isEmpty(inputPhone.text.toString()) || !checkAgreement.isChecked) {
            return false
        }

        return true
    }

    private fun signUp() {
        val email = inputEmail.text.toString()
        val password = inputPassword.text.toString()

        dialogLoader(true)

        AmApplication.amAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->

            if (task.isSuccessful) {
                dialogLoader(false)
                message("Authentication success.")
            } else {
                createUser(email, password)
            }
        }
    }

    private fun createUser(email: String, password: String) {
        AmApplication.amAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            dialogLoader(false)

            if (task.isSuccessful) {
                message("Success")
                goToOtp()
            }else{
                message("Gagal: ${task.exception?.localizedMessage}")
            }
        }
    }

    private fun goToOtp() {
        isChangePhone = false
        disabledForm(true)

        val email = inputEmail.text.toString()
        val phone = inputPhone.text.toString()
        val name = inputName.text.toString()
        val user = User(name, email, phone)

        val intent = Intent(this, OtpVerificationActivity::class.java)
        intent.putExtra(Constants.INTENT.DATA.SIGN_UP.user, user)
        startActivityForResult(intent, SIGN_UP_PHONE_VERIFICATION)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK && data != null) {
            when(requestCode) {
                SIGN_UP_PHONE_VERIFICATION -> {
                    isChangePhone = data.getBooleanExtra(changePhone, false)
                    if(isChangePhone) {
                        disabledForm(false)
                    }
                }
            }
        }
    }

    private fun disabledForm(state: Boolean) {
        inputEmail.isEnabled = state
        inputName.isEnabled = state
        inputPassword.isEnabled = state

        if(state) {
            currentPhone = inputPhone.text.toString()
            inputPhone.requestFocus()
        }
    }

}