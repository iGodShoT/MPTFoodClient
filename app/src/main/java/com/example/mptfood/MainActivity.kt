package com.example.mptfood

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {


    lateinit var menuFragment: Fragment
    lateinit var  cartFragment: Fragment
    lateinit var accountFragment : Fragment
    //lateinit var mGoogleSignInClient : GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        menuFragment = MenuFragment()
        cartFragment = CartFragment()
        accountFragment = AccountFragment()

        val bottomMenu = findViewById<BottomNavigationView>(R.id.bottom_menu)


        bottomMenu.setOnNavigationItemSelectedListener {
            when (it.itemId){
                R.id.food_item -> makeCurrentFragment(menuFragment)
                R.id.cart_item -> makeCurrentFragment(cartFragment)
                R.id.account_item -> makeCurrentFragment(accountFragment)
            }
            true
        }

        makeCurrentFragment(menuFragment)
    }
    private fun makeCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frames_view, fragment)
            commit()
        }
}