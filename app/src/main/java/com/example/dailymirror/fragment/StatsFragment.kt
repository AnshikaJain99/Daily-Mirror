package com.example.dailymirror.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.dailymirror.AppDatabase
import com.example.dailymirror.R
import com.example.dailymirror.model.BarchartModel
import com.example.dailymirror.util.Constants
import com.example.dailymirror.util.Constants.code
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_journal.*
import kotlinx.android.synthetic.main.fragment_stats.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class StatsFragment : Fragment() {

    private val month = arrayListOf("January","February","March","April","May","June","July","August","September","October","November","December")
    private val emotion = arrayListOf("Happy","Angry","Sad","Fear","Surprise")



    val clear = android.graphics.drawable.GradientDrawable(android.graphics.drawable.GradientDrawable.Orientation.LEFT_RIGHT,
        intArrayOf(
            0XFFFFD859.toInt(),
            0XFFFD962F.toInt()
        ))

    val storm = android.graphics.drawable.GradientDrawable(android.graphics.drawable.GradientDrawable.Orientation.LEFT_RIGHT,
        intArrayOf(
            0XFFE6EAE9.toInt(),
            0XFF788B91.toInt()
        ))

    val fog = android.graphics.drawable.GradientDrawable(android.graphics.drawable.GradientDrawable.Orientation.LEFT_RIGHT,
        intArrayOf(
            0XFFFEBA9A.toInt(),
            0XFF97464C.toInt()
        ))

    val snow = android.graphics.drawable.GradientDrawable(android.graphics.drawable.GradientDrawable.Orientation.LEFT_RIGHT,
        intArrayOf(
            0XFF569DB3.toInt(),
            0XFF234181.toInt()
        ))

    val rain = android.graphics.drawable.GradientDrawable(android.graphics.drawable.GradientDrawable.Orientation.LEFT_RIGHT,
        intArrayOf(
            0XFFA6DEB4.toInt(),
            0XFF005862.toInt()
        ))

    val db by lazy{
        AppDatabase.getDatabase(activity!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_stats, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("wts",code.toString())
        if ( code < 300) {
            weatherBG.setBackgroundDrawable(storm)
        }
        else
        {
            if(code<600)
            {
                weatherBG.setBackgroundDrawable(rain)
                weatherIcon.setImageResource(R.drawable.rainy)
            }
            else
            {
                if(code<700)
                {
                    weatherBG.setBackgroundDrawable(snow)
                    weatherIcon.setImageResource(R.drawable.snowy)
                }
                else
                {
                    if(code<800)
                    {
                        weatherBG.setBackgroundDrawable(fog)
                        weatherIcon.setImageResource(R.drawable.foggy)
                    }
                    else
                    {
                        if(code<900){
                            weatherBG.setBackgroundDrawable(clear)
                            weatherIcon.setImageResource(R.drawable.clear)
                        }
                    }
                }
            }

        }



        setupSpinner()

        statsback.setOnClickListener {
            activity!!.onBackPressed()
        }

        initBarchart()

        barButton.setOnClickListener {
            setBarchart()
        }

        weatherTemp.text = Constants.weather_temp
        weatherLoc.text = Constants.weather_loc
        weatherIcon.setImageResource(R.drawable.clear)

        var bundle = this.arguments
//        happy.text = bundle!!.getInt("Happy").toString()
//        angry.text = bundle!!.getInt("Angry").toString()
//        surprise.text = bundle!!.getInt("Surprise").toString()
//        sad.text = bundle!!.getInt("Sad").toString()
//        fear.text = bundle!!.getInt("Fear").toString()



        var emotions_array = arrayOf( bundle!!.getInt("Happy"),
            bundle!!.getInt("Angry"),
            bundle!!.getInt("Surprise"),
            bundle!!.getInt("Sad"),
            bundle!!.getInt("Fear"))



        var list = arrayListOf<PieEntry>()

        if(emotions_array[0]>0)
            list.add(PieEntry(emotions_array[0].toFloat(),"Happy"))
        if(emotions_array[1]>0)
            list.add(PieEntry(emotions_array[1].toFloat(),"Angry"))
        if(emotions_array[2]>0)
            list.add(PieEntry(emotions_array[2].toFloat(),"Surprise"))
        if(emotions_array[3]>0)
            list.add(PieEntry(emotions_array[3].toFloat(),"Sad"))
        if(emotions_array[4]>0)
            list.add(PieEntry(emotions_array[4].toFloat(),"Fear"))

        latest_piechart.isVisible = list.size > 0

        latest_piechart.animateY(1000, Easing.EaseInOutCubic)

        var dataSet = PieDataSet(list,"Emotions")


        var happy = context?.let { ContextCompat.getColor(it,R.color.happy) }!!
        var sad = context?.let { ContextCompat.getColor(it,R.color.sad) }!!
        var surprise = context?.let { ContextCompat.getColor(it,R.color.surprise) }!!
        var fear = context?.let { ContextCompat.getColor(it,R.color.fear) }!!
        var angry = context?.let { ContextCompat.getColor(it,R.color.angry) }!!

        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f
        dataSet.colors = mutableListOf(happy,sad,surprise,fear,angry)
        var data = PieData(dataSet)
        data.apply {
            setValueTextSize(10f)
            setValueTextColor(R.color.white)
        }

        latest_piechart.data = data
    }

    private fun setupSpinner() {
        val monthAdapter = ArrayAdapter<String>(activity!!,android.R.layout.simple_dropdown_item_1line,month)
        val emotionAdapter = ArrayAdapter<String>(activity!!,android.R.layout.simple_dropdown_item_1line,emotion)

        monthSpinner.adapter = monthAdapter
        emotionSpinner.adapter = emotionAdapter

    }

    private fun initBarchart() {

        var list: List<BarchartModel>
        val myCalendar = Calendar.getInstance()
        val mydbFormat = "yyyy-MM-dd"
        val dbsdf = SimpleDateFormat(mydbFormat)
        val todayDate = dbsdf.format(myCalendar.time).toString()
        var monthText = todayDate.substring(5,7)
        var x = monthText.toInt()-1
        monthSpinner.setSelection(x)

        GlobalScope.async {
            withContext(Dispatchers.IO){
                list = db.journalDao().getHappy(monthText)
                Log.d("bar",list.size.toString())
            }
            var barEntry = ArrayList<BarEntry>()
            for( a in list){
                barEntry.add(BarEntry(a.date.substring(8,10).toFloat(),a.emotion.toFloat()))
            }
            var barDataset = BarDataSet(barEntry,"Happy")
            var barData = BarData(barDataset)

            latest_barchart.setFitBars(true)
            latest_barchart.data = barData
            latest_barchart.animateY(2000)
            var happy = context?.let { ContextCompat.getColor(it,R.color.happy) }
            barDataset.color = Color.YELLOW
        }

    }


    private fun setBarchart() {
        lateinit var list: List<BarchartModel>
        var monthText = monthSpinner.selectedItemPosition
        monthText+=1;
        var finalMonth:String
        if(monthText<10)
            finalMonth = "0"+monthText.toString()
        else
            finalMonth = monthText.toString()

        var emotionText = emotionSpinner.selectedItem
        lateinit var label: String
        GlobalScope.async(Dispatchers.Main) {
            withContext(Dispatchers.IO){
                when(emotionText){
                    "Happy" -> {
                        list = db.journalDao().getHappy(finalMonth)
                        label = "Happy"
                    }
                    "Sad" -> {
                        list = db.journalDao().getSad(finalMonth)
                        label = "Sad"
                    }
                    "Angry" -> {
                        list = db.journalDao().getAngry(finalMonth)
                        label = "Angry"
                    }
                    "Surprise" ->{
                        list = db.journalDao().getSurprise(finalMonth)
                        label = "Surprise"
                    }
                    "Fear" ->{
                        list = db.journalDao().getFear(finalMonth)
                        label = "Fear"
                    }
                }
            }

            val barEntry = ArrayList<BarEntry>()
            for( a in list){
                barEntry.add(BarEntry(a.date.substring(8,10).toFloat(),a.emotion.toFloat()))
            }

            var barDataset = BarDataSet(barEntry,label)
            var barData = BarData(barDataset)

            latest_barchart.setFitBars(true)
            latest_barchart.data = barData
            latest_barchart.animateY(2000)
            barDataset.color = Color.YELLOW
        }

    }

    companion object {
        @JvmStatic
        fun newInstance() =
            StatsFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}