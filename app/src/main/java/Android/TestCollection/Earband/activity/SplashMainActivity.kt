package Android.TestCollection.Earband.activity

import Android.TestCollection.Earband.R
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashMainActivity : AppCompatActivity() {
override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash_main)

        Handler(Looper.getMainLooper())
            .postDelayed(
                {
                    startActivity(Intent(this, WelcomePermissionRequestActivity::class.java))
                    finish()
                },
                2000
            )
    }
}