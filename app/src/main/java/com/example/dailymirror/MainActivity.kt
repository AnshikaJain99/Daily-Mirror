package com.example.dailymirror

import android.R.attr.fragment
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.database.CursorWindow
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.example.dailymirror.fragment.JournalFragment
import com.example.dailymirror.fragment.StatsFragment
import com.example.dailymirror.fragment.TodoFragment
import com.example.dailymirror.util.Constants.FINISH
import com.example.dailymirror.util.Constants.TAG1
import com.example.dailymirror.util.Constants.TAG2
import com.example.dailymirror.util.Constants.TAG3
import com.example.dailymirror.util.Constants.code
import com.example.dailymirror.util.Constants.tag
import com.example.dailymirror.util.Constants.weather_loc
import com.example.dailymirror.util.Constants.weather_temp
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_journal.*
import kotlinx.android.synthetic.main.fragment_stats.*
import kotlinx.coroutines.*
import org.json.JSONObject
import java.lang.reflect.Field
//]=[-p0o9iu]


class MainActivity : AppCompatActivity() {


//    Clear/Cloudy  #FFD859 #FD962F
//    Rain+Drizzle #A6DEB4 #005862
//    Fog #FEBA9A #97464C
//    Snow #569DB3 #234181

    private var weather_url1 = ""
    private val PERMISSION_CODE = 100
    private var api_id1 = "097d9dde607a431caf37d90544bab1b3"
    //    private lateinit var weather: TextView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var givenPermission: Boolean = false

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initPython()


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//        Log.e("lat", weather_url1)
//
//        Log.e("lat", "onClick")

        if(checkPermission())
            givenPermission = true
        else
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION
            ), PERMISSION_CODE)

        if(givenPermission){
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->

                    weather_url1 = "https://api.weatherbit.io/v2.0/current?" + "lat=" + location?.latitude +"&lon="+ location?.longitude + "&key="+ api_id1

                    getTemp()
                }
        }

        bottomNavigation.show(1)
        tag = TAG2
        replaceFragment(JournalFragment.newInstance(), true, TAG2)


        bottomNavigation.add(MeowBottomNavigation.Model(0, R.drawable.ic_baseline_post_add_24))
        bottomNavigation.add(MeowBottomNavigation.Model(1, R.drawable.ic_baseline_receipt_24))
        bottomNavigation.add(MeowBottomNavigation.Model(2, R.drawable.ic_baseline_show_chart_24))

        bottomNavigation.setOnClickMenuListener {
            when(it.id){
                0 -> {
                    replaceFragment(TodoFragment.newInstance(), true, TAG1)
                    if (tag == TAG2) {
                        tag = TAG3
                        journalAlert()
                    } else {
                        tag = TAG3
                    }
                }
                1 -> {
                    replaceFragment(JournalFragment.newInstance(), true, TAG2)
                    if (tag == TAG1) {
                        tag = TAG3
                        todoAlert()
                    } else {
                        tag = TAG3
                    }
                }
                2 -> {
                    replaceFragment(StatsFragment.newInstance(), true, TAG3)
                    if (tag == TAG1) {
                        tag = TAG3
                        todoAlert()
                    } else if (tag == TAG2) {
                        tag = TAG3
                        journalAlert()
                    } else
                        tag = TAG3
                }
            }
        }
        try {
            val field: Field = CursorWindow::class.java.getDeclaredField("sCursorWindowSize")
            field.setAccessible(true)
            field.set(null, 20 * 1024 * 1024) //the 20MB is the new size
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initPython() {
        if(!Python.isStarted())
            Python.start(AndroidPlatform(this))
    }

    private fun replaceFragment(fragment: Fragment, isTransition: Boolean, TAG: String){

        val fragmentTransition = supportFragmentManager.beginTransaction()
        if(!isTransition){
            fragmentTransition.setCustomAnimations(
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
            )
        }
        else{
            fragmentTransition.replace(R.id.bottomFrameLayout, fragment, TAG).addToBackStack(TAG).commit()
//            fragmentTransition.remove(fragment).commit()
        }
    }

    fun todoAlert()
    {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle("Alert")
        //set message for alert dialog
        builder.setMessage("Keep changes made in Todo")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing cancel action
        builder.setNeutralButton("CANCEL") { dialogInterface, which ->
            bottomNavigation.show(0)
            if(tag != FINISH)
                super.onBackPressed()
            tag = TAG1
        }
        //performing negative action
        builder.setNegativeButton("DISCARD") { dialogInterface, which ->
            if(tag == FINISH)
                finish()
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    override fun onBackPressed() {
        if(tag == TAG1) {
            tag = FINISH
            todoAlert()
        }
        else if(tag == TAG2){
            tag = FINISH
            journalAlert()
        }
        else
            finish()
    }

    fun journalAlert()
    {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle("Alert")
        //set message for alert dialog
        builder.setMessage("Keep changes made in diary entry")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing cancel action
        builder.setNeutralButton("CANCEL") { dialogInterface, which ->
            bottomNavigation.show(1)
            if(tag != FINISH)
                super.onBackPressed()
            tag = TAG2
        }
        //performing negative action
        builder.setNegativeButton("DISCARD") { dialogInterface, which ->
            if(tag == FINISH)
                finish()
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

//    private fun setWeather() {
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
////        Log.e("lat", weather_url1)
////        Log.e("lat", "onClick")
//
//        if(checkPermission())
//            givenPermission = true
//        else
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION
//                ), PERMISSION_CODE)
//            }
//
//        if(givenPermission){
//            fusedLocationClient.lastLocation
//                .addOnSuccessListener { location: Location? ->
//                    weather_url1 = "https://api.weatherbit.io/v2.0/current?" + "lat=" + location?.latitude +"&lon="+ location?.longitude + "&key="+ api_id1
//
//                }
//        }
//    }

    private fun checkPermission():Boolean{
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            (checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)) == PackageManager.PERMISSION_GRANTED &&
                    (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)) == PackageManager.PERMISSION_GRANTED
        } else {
            return false
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode){
            PERMISSION_CODE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    givenPermission = true
                }
            }
        }
    }

    private fun getTemp() {
        val queue = Volley.newRequestQueue(this)
        val url: String = weather_url1

        Log.e("lat", url)
        val stringReq = StringRequest(
            Request.Method.GET, url,
            { response ->
                Log.e("lat", response.toString())

                val obj = JSONObject(response)

                val arr = obj.getJSONArray("data")
                Log.e("lat obj1", arr.toString())


                val obj2 = arr.getJSONObject(0)
                val obj3 = obj2.getJSONObject("weather");
                val obj4 = obj3.getString("code")

                Log.e("lat obj2", obj2.toString())
                weather_temp = obj2.getString("temp") + "\u2103"
                weather_loc = obj2.getString("city_name")
                code = (obj3.getString("code")).toInt()
                if ((obj3.getString("code")).toInt()< 300) {
                    var toast = Toast.makeText(this,"Stay cozy at home",Toast.LENGTH_SHORT)
                    toastView(toast)
                }
                else
                {
                    if((obj3.getString("code")).toInt()<600)
                    {
//                        parent_layout.setBackgroundDrawable(rain)
                        var toast = Toast.makeText(this,"It's rainy outside! Take umbrella",Toast.LENGTH_SHORT)
                        toastView(toast)

                    }
                    else
                    {
                        if((obj3.getString("code")).toInt()<700)
                        {
//                            parent_layout.setBackgroundDrawable(snow)
                            var toast = Toast.makeText(this,"It's snowy outside! Wear warm clothes",Toast.LENGTH_SHORT)
                            toastView(toast)
                        }
                        else
                        {
                            if((obj3.getString("code")).toInt()<800)
                            {
//                                parent_layout.setBackgroundDrawable(fog)
                                var toast = Toast.makeText(this,"Life is foggy; always try to see what lies behind the fog",Toast.LENGTH_SHORT)
                                toastView(toast)
                            }
                            else
                            {
                                if((obj3.getString("code")).toInt()<900){
//                                    parent_layout.setBackgroundDrawable(clear)
                                    var toast = Toast.makeText(this,"It's sunny outside! Don't shy away from exploring",Toast.LENGTH_SHORT)
                                   toastView(toast)
                                }
                            }
                        }
                    }

                }

            },
            //In case of any error
            {
                weather_temp = "Didn't work"
            })
        queue.add(stringReq)
    }

    fun toastView(toast: Toast)
    {
        var view = toast.view
        var toastMsg = view!!.findViewById<TextView>(android.R.id.message)
        toastMsg.setTextColor(resources.getColor(R.color.toasttext))
        toastMsg.setBackgroundColor(resources.getColor(R.color.toastbg))
        toast.show()
    }
}