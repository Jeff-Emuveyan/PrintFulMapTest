package com.example.printfulltest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.printfulltest.databinding.ActivityMainBinding
import com.example.users.ui.UsersFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportFragmentManager.beginTransaction().replace(R.id.container, UsersFragment.newInstance()).commit()
    }
}