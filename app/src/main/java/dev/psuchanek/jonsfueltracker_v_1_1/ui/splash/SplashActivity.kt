package dev.psuchanek.jonsfueltracker_v_1_1.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dev.psuchanek.jonsfueltracker_v_1_1.MainActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashIntent = Intent(this, MainActivity::class.java)
        startActivity(splashIntent)
        finish()

    }



}