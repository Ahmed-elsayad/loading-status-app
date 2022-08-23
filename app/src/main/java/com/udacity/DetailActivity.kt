package com.udacity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityDetailBinding.inflate(layoutInflater)
        val repoName = intent.getStringExtra(EXTRA_NAME)
        val dowwnloadStatus = intent.getBooleanExtra(EXTRA_DOWNLOAD_STATUS,false)

        setContentView(binding.root)


        setSupportActionBar(binding.toolbar)

        binding.contentDetail.tvFileName.text = repoName

        if (dowwnloadStatus) {
            binding.contentDetail.statusText.text = getString(R.string.download_success)
            binding.contentDetail.statusText.setTextColor(Color.GREEN)
        } else {
            binding.contentDetail.statusText.text = getString(R.string.download_fail)
            binding.contentDetail.statusText.setTextColor(Color.RED)
        }

        binding.contentDetail.buttonOk.setOnClickListener{
            val intent = Intent(this,MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

    }


}
