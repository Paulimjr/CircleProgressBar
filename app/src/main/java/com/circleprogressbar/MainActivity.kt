package com.circleprogressbar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.pcamilojr.circleprogressbar.CircleProgressBar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        progress_circular.setProgressAnimation(40f,2000)
        progress_circular.runProgress(progressListener)
    }

    private val progressListener = object : CircleProgressBar.CircleProgressBarCallback {

        override fun onProgressEnd() {
            Toast.makeText(this@MainActivity, "Progress finished!", Toast.LENGTH_SHORT).show()
        }

        override fun onProgressValue(progress: String, textColor: Int) {
            tvProgress.setTextColor(textColor)
            tvProgress.text = progress
        }
    }
}
