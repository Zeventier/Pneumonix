package com.bangkit.pneumoniadetector.ui.profile

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bangkit.pneumoniadetector.R
import com.bangkit.pneumoniadetector.databinding.FragmentProfileBinding
import com.bangkit.pneumoniadetector.ui.login.LoginActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = Firebase.auth.currentUser

        val circularProgressDrawable = CircularProgressDrawable(requireContext())
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()

        if(user?.photoUrl != null) {
            Glide.with(requireContext())
                .load(user.photoUrl)
                .placeholder(circularProgressDrawable)
                .apply(RequestOptions().override(130, 130))
                .into(binding.imageViewPhoto)
        } else {
            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_NO -> {
                    binding.imageViewPhoto.setImageResource(R.drawable.photo_profile_default)
                } // Night mode is not active, we're using the light theme
                Configuration.UI_MODE_NIGHT_YES -> {
                    binding.imageViewPhoto.setImageResource(R.drawable.photo_profile_default_white)
                } // Night mode is active, we're using dark theme
            }
        }

        binding.textViewName.text = user?.displayName.toString()
        binding.textViewEmail.text = user?.email.toString()

        setupAction()
    }

    private fun setupAction() {
        binding.btnLogout.setOnClickListener {
            Firebase.auth.signOut()

            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        binding.btnEditProfile.setOnClickListener {
            val intent = Intent(activity, EditProfileActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "ProfileFragment"
    }
}