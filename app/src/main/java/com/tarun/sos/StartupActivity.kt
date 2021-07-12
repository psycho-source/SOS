package com.tarun.sos

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.tarun.sos.`interface`.FragmentChangeListener
import com.tarun.sos.fragments.AddContactIntro
import com.tarun.sos.fragments.StartupIntro
import com.tarun.sos.utils.isFirstStart

class StartupActivity : AppCompatActivity(), FragmentChangeListener,
    ActivityCompat.OnRequestPermissionsResultCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!isFirstStart(this)) {
            startActivity(Intent(this, MainActivity::class.java))
            finishAfterTransition()
        }

        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        setContentView(R.layout.activity_startup)

        window.setBackgroundDrawable(null)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, StartupIntro())
            .commit()

    }

    override fun changeFragment(fragment: Fragment) {
        supportFragmentManager.commit {
            setCustomAnimations(
                R.anim.frag_slide_in,
                R.anim.frag_fade_out,
                R.anim.frag_fade_in,
                R.anim.frag_slide_out
            )
            replace(R.id.fragmentContainer, fragment)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 4) {
            changeFragment(AddContactIntro())
        }
    }

}