package com.dvaratask

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.dvaratask.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {

    lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search)

        binding.tvSearch.setOnClickListener {
            val mobileNo = binding.etMobileNo.text.toString()
            if (mobileNo.length != 10) {
                Toast.makeText(
                    applicationContext,
                    "Please Enter valid mobile number",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                binding.tvSearch.isEnabled = false
                val helper = AppDBHelper(applicationContext)
                val data = helper.getData(mobileNo)
                if (data?.mobileNo != null) {
                    val formatData = "Result: \n\n" +
                            "Upload Speed : ${data.uploadSpeed} \n\n" +
                            "Download Speed : ${data.downloadSpeed} \n\n" +
                            "Timestamp : ${data.timeStamp}"
                    binding.tvData.text = formatData
                } else {
                    binding.tvData.text = ""
                    Toast.makeText(
                        applicationContext,
                        "No record found.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                binding.tvSearch.isEnabled = true
            }
        }
    }
}