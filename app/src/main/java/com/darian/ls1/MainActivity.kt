package com.darian.ls1

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.darian.ls1.Model.ApiResponse
import com.darian.ls1.Model.LinkCreateResponse
import com.darian.ls1.Model.ShowStaticsResponse
import com.darian.ls1.Services.ILinkService
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


class MainActivity : AppCompatActivity() {


    lateinit var linkService: ILinkService
    var btn_confirm_check = false
    var baseUrl = "https://ls1.ir"
    private var isInMainActivity = true
    private var doubleBackToExitPressedOnce = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //retrofit
        linkService = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(ILinkService::class.java)


        //site button
        var btn_site = findViewById<Button>(R.id.btn_siteUrl)
        btn_site.setOnClickListener {
            openSite()
        }

        //statics btn
        var btn_statics = findViewById<Button>(R.id.btn_statics)
        btn_statics.setOnClickListener {
            openStatics()
        }

        //about btn
        var btn_about = findViewById<Button>(R.id.btn_about)
        btn_about.setOnClickListener {
            openAbout()
        }

    }


    fun getClipboard(): ClipboardManager {
        val clipboard = getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
        return clipboard
    }


    override fun onBackPressed() {
        if (isInMainActivity) {
            if (doubleBackToExitPressedOnce) {
                finish()
                return
            }

            this.doubleBackToExitPressedOnce = true
            Toast.makeText(
                this@MainActivity,
                getString(R.string.double_back_exit),
                Toast.LENGTH_SHORT
            ).show()

            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                doubleBackToExitPressedOnce = false
            }, 2000)
            //finish()
        } else {
            var mainView = MainView()
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragmentHolder, mainView)
                .commitAllowingStateLoss()
            isInMainActivity = true
        }
    }

    private fun openStatics() {
        isInMainActivity = false
        var staticsFragment = StaticsFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragmentHolder, staticsFragment)
            .commitAllowingStateLoss()
    }

    private fun openAbout() {
        isInMainActivity = false
        var aboutFragment = AboutFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragmentHolder, aboutFragment)
            .commitAllowingStateLoss()
    }

    private fun openSite() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(baseUrl);
        startActivity(intent)
    }


    fun startLoading(btn: Button) {
        btn.text = getString(R.string.loadingLink)
        btn_confirm_check = true
    }

    fun stopLoading(btn: Button) {
        btn.text = getString(R.string.btnStart)
        btn_confirm_check = false
    }


    fun closeKeyBoard() {
        val imm: InputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        if (imm.isActive)
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }


}
