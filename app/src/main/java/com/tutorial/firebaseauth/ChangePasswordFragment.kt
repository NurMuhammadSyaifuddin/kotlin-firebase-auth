package com.tutorial.firebaseauth

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import kotlinx.android.synthetic.main.fragment_change_password.*

/**
 * create an instance of this fragment.
 */
class ChangePasswordFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        layoutPassword.visibility = View.VISIBLE
        layoutNewPassword.visibility = View.GONE

        btnAuth.setOnClickListener {
            val password = et_password.text.toString().trim()

            if (password.isEmpty()){
                et_password.error = "Password Harus diisi"
                et_password.requestFocus()
                return@setOnClickListener
            }

            user?.let {
                val userCredential = EmailAuthProvider.getCredential(user.email!!, password)
                it.reauthenticate(userCredential).addOnCompleteListener{
                    if (it.isSuccessful){
                        layoutPassword.visibility = View.GONE
                        layoutNewPassword.visibility = View.VISIBLE
                    }else if(it.exception is FirebaseAuthInvalidCredentialsException){
                        et_password.error = "Password Salah"
                        et_password.requestFocus()
                    }else{
                        Toast.makeText(activity, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            btnNewPassword.setOnClickListener {view ->
                val newPassword = etNewPassword.text.toString().trim()
                val newPasswordConfirm = etNewPasswordConfirm.text.toString().trim()

                if (newPassword.isEmpty() || newPassword.length < 6){
                    etNewPassword.error = "Password Harus lebih dari sama dengan 6"
                    etNewPassword.requestFocus()
                    return@setOnClickListener
                }

                if (newPassword != newPasswordConfirm){
                    etNewPasswordConfirm.error = "Password Tidak Sama"
                    etNewPasswordConfirm.requestFocus()
                    return@setOnClickListener
                }


                user?.let {
                    user.updatePassword(newPassword).addOnCompleteListener{
                        if (it.isSuccessful){
                            val actionPasswordChanged = ChangePasswordFragmentDirections.actionPasswordChanged()
                            Navigation.findNavController(view).navigate(actionPasswordChanged)
                            Toast.makeText(activity, "Password Berhasil Diganti", Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(activity, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

    }
}