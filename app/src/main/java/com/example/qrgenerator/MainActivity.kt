package com.example.qrgenerator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.qrgenerator.databinding.ActivityMainBinding
import com.example.qrgenerator.fragments.HomeFragment
import com.example.qrgenerator.fragments.ScanQrFragment
import com.example.qrgenerator.viewmodel.QrCodeViewmodel
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var bottomNav: BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        loadFragment(HomeFragment())

        bottomNav = binding.bottomNav
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.page_1 -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.page_2 -> {
                    loadFragment(ScanQrFragment())
                    true
                }
                else -> false
            }
        }


    }
    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.commit()
    }
}