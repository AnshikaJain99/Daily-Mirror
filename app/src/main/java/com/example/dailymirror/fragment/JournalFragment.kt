package com.example.dailymirror.fragment

import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.ImageDecoder.createSource
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.chaquo.python.Python
import com.example.dailymirror.AppDatabase
import com.example.dailymirror.R
import com.example.dailymirror.model.JournalModel
import com.example.dailymirror.util.Constants
import com.example.dailymirror.util.Constants.TAG2
import com.example.dailymirror.util.Constants.TAG3
import com.example.dailymirror.util.Constants.code
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_journal.*
import kotlinx.android.synthetic.main.fragment_todo_new_task.*
import kotlinx.android.synthetic.main.todo_item.*
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*


class JournalFragment : Fragment() {


    private val PERMISSION_CODE = 100
    private val RESULT_LOAD_IMAGE = 1
    private lateinit var finalDate: String
    private lateinit var todayDate: String
    private lateinit var myCalendar: Calendar
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private var update = 0
    private var bitmap: Bitmap? = null
    private var isedited: Boolean = false
    private var isFirst: Boolean = false



    val db by lazy{
        AppDatabase.getDatabase(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_journal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialDate()

        diaryBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        diaryEntryDate.setOnClickListener{
//            Constants.tag = TAG2
            val imm = requireActivity().getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(diaryDate.windowToken, 0)
            setDateListener()
        }
        diaryEntryTitle.setOnClickListener {
            Constants.tag = TAG2
//            activity!!.bottomNavigation.isVisible = false
        }

        diaryEntryDescription.setOnFocusChangeListener { v, hasFocus ->
            Constants.tag = TAG2
            Log.d("JT",Constants.tag)
        }

        diarySave.setOnClickListener {
//            activity!!.bottomNavigation.isVisible = true
            saveDiary()
        }
        diaryDelete.setOnClickListener {
//            activity!!.bottomNavigation.isVisible = true
            Constants.tag = TAG3
            deleteDiary()
        }

        attach_image.setOnClickListener {
//            activity!!.bottomNavigation.isVisible = true
            if(checkPermissions())
                uploadImage()
            else
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_CODE)
        }

    }



    private fun displayWeatherInfo() {


        Log.d("wt", code.toString())

        if ( code < 300) {
            var toast = Toast.makeText(requireContext(),"Stay cozy at home",Toast.LENGTH_SHORT)
            var view = toast.view
            var toastMsg = view!!.findViewById<TextView>(android.R.id.message)
//                        toastMsg.setBackgroundColor()
            toast.setGravity(Gravity.CENTER,0,0)
            toastMsg.textSize = 20F
//        toastMsg.background = R.drawable.rounded_corners
            toastMsg.setTextColor(resources.getColor(R.color.toasttext))
            toastMsg.setBackgroundColor(resources.getColor(R.color.toastbg))
//        toastMsg.setBackgroundResource(R.drawable.rounded_corners)
            toast.show()
        }
        else
        {
            if(code<600)
            {
                var toast = Toast.makeText(requireContext(),"It's rainy outside! Take umbrella",Toast.LENGTH_SHORT)
                var view = toast.view
                var toastMsg = view!!.findViewById<TextView>(android.R.id.message)
//                        toastMsg.setBackgroundColor()
                toast.setGravity(Gravity.CENTER,0,0)
                toastMsg.textSize = 20F
//        toastMsg.background = R.drawable.rounded_corners
                toastMsg.setTextColor(resources.getColor(R.color.toasttext))
                toastMsg.setBackgroundColor(resources.getColor(R.color.toastbg))
//        toastMsg.setBackgroundResource(R.drawable.rounded_corners)
                toast.show()
            }
            else
            {
                if(code<700)
                {
                    var toast = Toast.makeText(requireContext(),"It's snowy outside! Wear warm clothes",Toast.LENGTH_SHORT)
                    var view = toast.view
                    var toastMsg = view!!.findViewById<TextView>(android.R.id.message)
//                        toastMsg.setBackgroundColor()
                    toast.setGravity(Gravity.CENTER,0,0)
                    toastMsg.textSize = 20F
//        toastMsg.background = R.drawable.rounded_corners
                    toastMsg.setTextColor(resources.getColor(R.color.toasttext))
                    toastMsg.setBackgroundColor(resources.getColor(R.color.toastbg))
//        toastMsg.setBackgroundResource(R.drawable.rounded_corners)
                    toast.show()
                }
                else
                {
                    if(code<800)
                    {
                        var toast = Toast.makeText(requireContext(),"Life is foggy; always try to see what lies behind the fog",Toast.LENGTH_SHORT)
                        var view = toast.view
                        var toastMsg = view!!.findViewById<TextView>(android.R.id.message)
//                        toastMsg.setBackgroundColor()
                        toast.setGravity(Gravity.CENTER,0,0)
                        toastMsg.textSize = 20F
//        toastMsg.background = R.drawable.rounded_corners
                        toastMsg.setTextColor(resources.getColor(R.color.toasttext))
                        toastMsg.setBackgroundColor(resources.getColor(R.color.toastbg))
//        toastMsg.setBackgroundResource(R.drawable.rounded_corners)
                        toast.show()
                    }
                    else
                    {
                        if(code<900){
                            var toast = Toast.makeText(requireContext(),"It's sunny outside! Don't shy away from exploring",Toast.LENGTH_SHORT)
                            var view = toast.view
                            var toastMsg = view!!.findViewById<TextView>(android.R.id.message)
//                        toastMsg.setBackgroundColor()
                            toast.setGravity(Gravity.CENTER,0,0)
                            toastMsg.textSize = 20F
//        toastMsg.background = R.drawable.rounded_corners
                            toastMsg.setTextColor(resources.getColor(R.color.toasttext))
                            toastMsg.setBackgroundColor(resources.getColor(R.color.toastbg))
//        toastMsg.setBackgroundResource(R.drawable.rounded_corners)
                            toast.show()
                        }
                    }
                }
            }

        }

    }

    private fun getDiary() {

        isedited = true
        var x: JournalModel
        GlobalScope.launch(Dispatchers.Main){
            val id = withContext(Dispatchers.IO){
//               Python function
                x = db.journalDao().getEntry(finalDate)
            }
            if(x!=null){
                diaryEntryTitle.setText(x.title)
                diaryEntryDescription.setText(x.description)
            }
            if(x.image!=null)
            {
                bitmap = getImage(x.image!!)
                image.setImageBitmap(getImage(x.image!!))
                imageCardView.isVisible = true
                val param = textEntryPoint.layoutParams as ConstraintLayout.LayoutParams
                param.topToBottom = gotit.id
                textEntryPoint.requestLayout()
            }
            else{
                imageCardView.visibility = View.GONE
                val param = textEntryPoint.layoutParams as ConstraintLayout.LayoutParams
                param.topToBottom = diaryEntryPoint.id
                textEntryPoint.requestLayout()
            }
        }
    }

    private fun checkPermissions(): Boolean {
        return (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    uploadImage()
                else {
                    Toast.makeText(context, "Permissions Denied", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun uploadImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, RESULT_LOAD_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data!=null){
            var uri = data.getData()
//            image.setImageURI(uri)

            bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(createSource(requireContext().contentResolver, uri!!))
            } else {
                MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
            }
            Constants.tag = TAG2
            image.setImageBitmap(bitmap)
//            imageToString = BitMaptoString(bitmap)
            imageCardView.isVisible = true
            val param = textEntryPoint.layoutParams as ConstraintLayout.LayoutParams
            param.topToBottom = gotit.id
            textEntryPoint.requestLayout()
        }
    }

    private fun setDateListener() {

        myCalendar = Calendar.getInstance()
        dateSetListener = DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->

            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            updateDate()
        }

        val datePickerDialog = DatePickerDialog(
            requireActivity(), dateSetListener, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()

    }

    private fun updateDate() {

        Constants.tag = TAG3
        val myFormat = "dd MMM yyyy"
        val sdf = SimpleDateFormat(myFormat)
        val ddate = sdf.format(myCalendar.time).toString()
        diaryDate.text = ddate.substring(0, 2)
        diaryMonth.text = ddate.substring(3, 6)+"."
        diaryYear.text = ddate.substring(7, 11)

        val mydbFormat = "yyyy-MM-dd"
        val dbsdf = SimpleDateFormat(mydbFormat)
        finalDate = dbsdf.format(myCalendar.time).toString()
        isedited = false
        var check: Boolean
        GlobalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO){
                check = db.journalDao().isPresent(finalDate)
            }
            if(check)
                getDiary()
            else{
                imageCardView.isVisible = false
                val param = textEntryPoint.layoutParams as ConstraintLayout.LayoutParams
                param.topToBottom = diaryEntryPoint.id
                textEntryPoint.requestLayout()
                diaryEntryTitle.setText("")
                diaryEntryDescription.setText("")
            }
        }

    }

    private fun initialDate() {

        Constants.tag = TAG3
        myCalendar = Calendar.getInstance()
        val myFormat = "dd MMM yyyy"
        val sdf = SimpleDateFormat(myFormat)
        val ddate = sdf.format(myCalendar.time).toString()
        diaryDate.text = ddate.substring(0, 2)
        diaryMonth.text = ddate.substring(3, 6) + "."
        diaryYear.text = ddate.substring(7, 11)
        val mydbFormat = "yyyy-MM-dd"
        val dbsdf = SimpleDateFormat(mydbFormat)
        todayDate = dbsdf.format(myCalendar.time).toString()
        finalDate = dbsdf.format(myCalendar.time).toString()
        var check: Boolean
        GlobalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                check = db.journalDao().isPresent(finalDate)
            }
            if (check)
                getDiary()
        }
    }

    private fun getEmotions(desc: String): Array<Int> {
        Log.d("print","start")
        val python = Python.getInstance()
        val pythonFile = python.getModule("hello")
        var x = pythonFile.callAttr("helloWorld",desc).toString()
        Log.d("print",x)
        x = x.substring(1,x.length-1)
        var arr = x.split(":",",").map {
            it.trim()
        }.toMutableList()
        var ha = (arr[1].toFloat()*100).toInt()
        var an = (arr[3].toFloat()*100).toInt()
        var su = (arr[5].toFloat()*100).toInt()
        var sa = (arr[7].toFloat()*100).toInt()
        var fe = (arr[9].toFloat()*100).toInt()

        Log.d("Fear",x.toString())
        Log.d("ALL",arr[7]+" "+arr[9])

        if(ha+an+su+sa+fe != 100 && ha+an+su+sa+fe!=0){
            val ma = maxOf(ha,su,an,sa,fe)
            when(ma){
                ha-> ha += 1
                an-> an += 1
                su-> su += 1
                sa-> sa += 1
                fe-> fe += 1
            }
        }

        return arrayOf(ha,an,su,sa,fe)
    }
    public fun saveDiary() {

        Toast.makeText(requireActivity(), "SAVE DIARY"+ Constants.tag, Toast.LENGTH_SHORT).show()
        Constants.tag = TAG3
        val title = diaryEntryTitle.text.toString()
        val description = diaryEntryDescription.text.toString()
        var list: Array<Int>

//        list.add(0)
//        list.add(1)
//        list.add(2)
//        list.add(3)
//        list.add(4)

        if(isedited)
        {
            val builder = AlertDialog.Builder(requireContext())
            //set title for alert dialog
            builder.setTitle("Alert")
            //set message for alert dialog
            builder.setMessage("Do you want to save the changes in this entry?")
            builder.setIcon(android.R.drawable.ic_dialog_alert)

            builder.setPositiveButton("SAVE") { dialogInterface, which ->
//                    confirm = true
                GlobalScope.launch(Dispatchers.Main){

                    withContext(Dispatchers.IO) {
                        Log.d("Save","Savedbefore")
                        list = getEmotions(description)
//                        Log.d("List",list.toString())
                        db.journalDao().updateEntry(getBytes(bitmap), title, description, list[0], list[1], list[2], list[3], list[4], finalDate)
                    }
//                    imageCardView.isVisible = false
//                    diaryEntryTitle.setText("")
//                    diaryEntryDescription.setText("")
                    var bundle = Bundle()
                    bundle.putInt("Happy", list[0])
                    bundle.putInt("Angry", list[1])
                    bundle.putInt("Surprise", list[2])
                    bundle.putInt("Sad", list[3])
                    bundle.putInt("Fear", list[4])
//                    Constants.tag = TAG3
                    val fragment  = StatsFragment.newInstance()
                    fragment.arguments = bundle
                    val fragmentManager = requireActivity().supportFragmentManager
                    val fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.bottomFrameLayout, fragment, TAG3).addToBackStack(
                        TAG3).commit()
                    requireActivity().bottomNavigation.show(2)

                }
            }

            //performing negative action
            builder.setNegativeButton("CANCEL") { dialogInterface, which ->

            }
            // Create the AlertDialog
            val alertDialog: AlertDialog = builder.create()
            // Set other dialog properties
            alertDialog.setCancelable(false)
            alertDialog.show()
        }

        else{
            GlobalScope.launch(Dispatchers.Main){

                withContext(Dispatchers.IO) {
                    list = getEmotions(description)
                    Log.d("Save","Saved")
                    db.journalDao().insertEntry(JournalModel(getBytes(bitmap), title, description, list[0], list[1], list[2], list[3], list[4], finalDate))
                }
                var bundle = Bundle()
                bundle.putInt("Happy", list[0])
                bundle.putInt("Angry", list[1])
                bundle.putInt("Surprise", list[2])
                bundle.putInt("Sad", list[3])
                bundle.putInt("Fear", list[4])
                Constants.tag = TAG3
                val fragment  = StatsFragment.newInstance()
                fragment.arguments = bundle
                val fragmentManager = requireActivity().supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.bottomFrameLayout, fragment, TAG3).addToBackStack(
                    TAG3).commit()
                requireActivity().bottomNavigation.show(2)
            }
        }
    }

    private fun deleteDiary() {
        Constants.tag = TAG3
        var check: Boolean = false
        var confirm: Boolean = false
        GlobalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO){
                check = db.journalDao().isPresent(finalDate)
//                db.journalDao().deleteEntry(finalDate)
                Log.d("Delete","Not present")
            }
            if(check){
                val builder = AlertDialog.Builder(requireContext())
                //set title for alert dialog
                builder.setTitle("Alert")
                //set message for alert dialog
                builder.setMessage("Do you want to delete this entry?")
                builder.setIcon(android.R.drawable.ic_dialog_alert)

                builder.setPositiveButton("DELETE") { dialogInterface, which ->
//                    confirm = true

                    GlobalScope.launch(Dispatchers.Main){

                        withContext(Dispatchers.IO) {
                            db.journalDao().deleteEntry(finalDate)
                        }
                        imageCardView.isVisible = false
                        val param = textEntryPoint.layoutParams as ConstraintLayout.LayoutParams
                        param.topToBottom = diaryEntryPoint.id
                        textEntryPoint.requestLayout()
                        diaryEntryTitle.setText("")
                        diaryEntryDescription.setText("")
                    }
                }

                //performing negative action
                builder.setNegativeButton("CANCEL") { dialogInterface, which ->

                }
                // Create the AlertDialog
                val alertDialog: AlertDialog = builder.create()
                // Set other dialog properties
                alertDialog.setCancelable(true)
                alertDialog.show()
            }

        }
    }

    private fun getBytes(bitmap: Bitmap?): ByteArray? {
        if(bitmap == null)
            return null
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return  stream.toByteArray()
    }

    private fun getImage(image: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(image, 0, image.size)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            JournalFragment().apply {
                isFirst = true
                arguments = Bundle().apply {

                }
            }
    }


}

