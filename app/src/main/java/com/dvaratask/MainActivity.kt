package com.dvaratask

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.dvaratask.databinding.ActivityMainBinding
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var networkReceiver: NetworkChangeReceiver
    val handler = Handler()
    var runnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        networkReceiver = NetworkChangeReceiver()
        registerReceiver(networkReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

        runnable = Runnable {
            getMobileNetSpeed()
        }

        binding.tvSubmit.setOnClickListener {
            val mobileNo = binding.etMobileNo.text.toString()
            val upSpeed = binding.tvUploadSpeed.text.toString().replace("Upload Speed: ", "")
            val downSpeed = binding.tvDownSpeed.text.toString().replace("Download Speed: ", "")
            if (mobileNo.length != 10) {
                Toast.makeText(
                    applicationContext,
                    "Please Enter valid mobile number",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (upSpeed.length == 0 || downSpeed.length == 0) {
                Toast.makeText(
                    applicationContext,
                    "Please check your network connection",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                binding.tvSubmit.isEnabled = false
                val helper = AppDBHelper(applicationContext)
                val dateFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss aa", Locale.ENGLISH)
                val timeStamp = dateFormat.format(Date(Calendar.getInstance().timeInMillis))
                val data = InternetData(
                    mobileNo,
                    downSpeed,
                    upSpeed,
                    timeStamp
                )
                helper.insertData(data)
                binding.tvSubmit.isEnabled = true
            }
        }

        binding.tvSearch.setOnClickListener {
            val intent  = Intent(applicationContext, SearchActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getMobileNetSpeed() {
        val connectivityManager =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nc = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (isNetworkConnected()) {
            val downSpeed = nc!!.linkDownstreamBandwidthKbps
            val upSpeed = nc.linkUpstreamBandwidthKbps

            binding.tvDownSpeed.text = "Download Speed: $downSpeed Kbps"
            binding.tvUploadSpeed.text = "Upload Speed: $upSpeed Kbps"
            binding.tvConnectedStatus.text = "Network connected"
            binding.tvConnectedStatus.setBackgroundColor(Color.GREEN)

            handler.postDelayed(runnable!!, 2000)
        } else {
            binding.tvDownSpeed.text = ""
            binding.tvUploadSpeed.text = ""
            binding.tvConnectedStatus.text = "Network Disconnected"
            binding.tvConnectedStatus.setBackgroundColor(Color.RED)
        }
    }

    private fun isNetworkConnected(): Boolean {
        var result = false
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            result =
                connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo?.isConnected!!
        }

        return result
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable!!)
    }

    override fun onResume() {
        super.onResume()
        if (handler != null && runnable != null) {
            handler.postDelayed(runnable!!, 1000)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(networkReceiver)
    }

    inner class NetworkChangeReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (isNetworkConnected()) {
                binding.tvConnectedStatus.setBackgroundColor(Color.GREEN)
                if (handler != null && runnable != null) {
                    handler.postDelayed(runnable!!, 1000)
                }
            } else {
                binding.tvDownSpeed.text = ""
                binding.tvUploadSpeed.text = ""
                binding.tvConnectedStatus.text = "Network Disconnected"
                binding.tvConnectedStatus.setBackgroundColor(Color.RED)
                if (handler != null && runnable != null) {
                    handler.removeCallbacks(runnable!!)
                }
            }
        }
    }
}

