package com.bangkit.pneumoniadetector.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bangkit.pneumoniadetector.R
import com.bangkit.pneumoniadetector.databinding.ActivityMainBinding
import com.bangkit.pneumoniadetector.tools.GeneralTools
import com.bangkit.pneumoniadetector.ui.home.HomeFragment
import com.bangkit.pneumoniadetector.ui.login.LoginActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.red_1)))

        // Initialize Firebase Auth
        auth = Firebase.auth

        supportActionBar?.hide()

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_history, R.id.navigation_profile
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        isLogin()
        setupViewModel()
    }

    private fun setupViewModel() {
//        mainViewModel.isLogin().observe(this) { user ->
//            if (user != null) {
//                // User is signed in
//            } else {
//                // No user is signed in
//                val intent = Intent(this, LoginActivity::class.java)
//                startActivity(intent)
//                finish()
//            }
//        }
    }

    private fun isLogin() {
        val user = Firebase.auth.currentUser
        if (user != null) {
            // User is signed in
        } else {
            // No user is signed in
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Receive request permission from fragment
    // example from HomeFragment for requesting camera permissions
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == HomeFragment.REQUEST_CAMERA_CODE_PERMISSION){
            if(!GeneralTools.allPermissionGranted(HomeFragment.REQUIRED_CAMERA_PERMISSION, this)){
                GeneralTools.showAlertDialog(this, getString(R.string.camera_usage_not_granted))
            }
            else{
                Toast.makeText(
                    this,
                    getString(R.string.camera_usage_granted),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}