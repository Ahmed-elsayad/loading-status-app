package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.udacity.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {


    private lateinit var glideUrl  : Repo
    private lateinit var appUrl : Repo
    private lateinit var retrofitUrl : Repo


    private var downloadID: Long = 0
    private var selectedRepo : Repo? = null

    private fun assignRepos()
    {
        glideUrl = Repo(getString(R.string.glide_url), getString(R.string.glide_image_loading_library_by_bumptech))
        appUrl = Repo(getString(R.string.load_app_url), getString(R.string.loadapp_current_repository_by_udacity))
        retrofitUrl = Repo(getString(R.string.retrofit_url),getString(R.string.retrofit_type_safe_http_client_for_android_and_java_by_square_inc))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        assignRepos()
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val binding = ActivityMainBinding.inflate(layoutInflater)

        setSupportActionBar(binding.toolbar)

       //Register a BroadcastReceiver to be run in the main activity thread.
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))



    }


    fun onRadioButtonClicked(view: View){
        if (view is RadioButton){
            val checked = view.isChecked
            when (view.id) {
                R.id.radioGlide -> if (checked) selectedRepo = glideUrl
                R.id.radioLoadApp -> if (checked) selectedRepo = appUrl
                R.id.radioRetrofit -> if (checked) selectedRepo = retrofitUrl
                else -> selectedRepo = null
            }
        }
    }

    fun onLoadingButtonClicked(view: View){
        download()
    }

    private val receiver = object : BroadcastReceiver() {

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

            val query = DownloadManager.Query()
            query.setFilterById(id!!)

            val cursor = downloadManager.query(query)
            var downloadCompletedSuccessfully = false
            if (cursor.moveToFirst()) {
                if (cursor.columnCount>0 ) {
                    val status =
                        cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                    if (status == DownloadManager.STATUS_SUCCESSFUL) downloadCompletedSuccessfully =
                        true
                }
            }
            val notificationManager = ContextCompat.getSystemService(
                applicationContext,
                NotificationManager::class.java
            ) as NotificationManager

            notificationManager.sendNotification(
                selectedRepo!!, downloadCompletedSuccessfully,
                applicationContext)
        }
    }

    private fun download() {
        if (selectedRepo == null) {
            Toast.makeText(applicationContext, "Please select the file to download", Toast.LENGTH_SHORT).show()
        } else if (!isNetworkAvailable()) {
            Toast.makeText(applicationContext, "No internet connection!", Toast.LENGTH_SHORT).show()
        } else {
//            request a new download
            val request =
                DownloadManager.Request(Uri.parse(selectedRepo!!.url))
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID =
                downloadManager.enqueue(request)
        }
    }

    private fun isNetworkAvailable(): Boolean {

   /*
   * when network connectivity changes. Get an instance of this class by calling Context.getSystemService(Context.CONNECTIVITY_SERVICE)
   * The primary responsibilities of this class are to:

1.. Monitor network connections (Wi-Fi, GPRS, UMTS)
2...Send broadcast intents when network connectivity changes
3..Attempt to "fail over" to another network when connectivity to a network is lost
4....Provide an API that allows applications to query the coarse-grained or fine-grained state of the available networks
5.....Provide an API that allows applications to request and select networks for their data traffic
   * */
        val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nw      = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        return actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                || actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                || actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)
    }

}
