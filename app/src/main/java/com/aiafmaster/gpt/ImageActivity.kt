package com.aiafmaster.gpt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aiafmaster.gpt.ui.image.ImageFragment

class ImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ImageFragment.newInstance())
                .commitNow()
        }
        // To pass argument ImageFragment.newInstance().arguments
    }
}