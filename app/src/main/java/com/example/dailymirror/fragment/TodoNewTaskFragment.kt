package com.example.dailymirror.fragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import com.example.dailymirror.AppDatabase
import com.example.dailymirror.R
import com.example.dailymirror.model.TodoModel
import com.example.dailymirror.util.Constants
import com.example.dailymirror.util.Constants.TAG1
import com.example.dailymirror.util.Constants.TAG3
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_todo_new_task.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*


class TodoNewTaskFragment : Fragment(), View.OnClickListener {

//    lateinit var bottomNavigationView : BottomNavigationView

    lateinit var myCalendar: Calendar

    lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    lateinit var timeSetListener: TimePickerDialog.OnTimeSetListener

    private val labels = arrayListOf("Personal","Business","Shopping","Banking","Medicine")

    val db by lazy{
        AppDatabase.getDatabase(requireActivity())
    }

    var finalDate = 0L
    var finalTime = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Constants.tag = TAG1
//        (activity as AppCompatActivity).setSupportActionBar(newTaskToolbar)


        return inflater.inflate(R.layout.fragment_todo_new_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        todoBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        setDate.setOnClickListener(this)
        setTime.setOnClickListener(this)
        todoSave.setOnClickListener(this)
        setUpSpinner()

    }


    private fun setUpSpinner() {
        val adapter = ArrayAdapter<String>(requireActivity(),android.R.layout.simple_dropdown_item_1line,labels)

        labels.sort()
        spinnerCategory.adapter = adapter
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.setDate->{
                val imm = requireActivity().getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(setDate.windowToken,0)
                setDateListener()
            }
            R.id.setTime->{
                Log.d("MSGSS:","Time")
                setTimeListener()
            }
            R.id.todoSave->{
                saveTodo()
            }
        }
    }

    private fun saveTodo() {
        val category = spinnerCategory.selectedItem.toString()
        val title = taskTitle.text.toString()
        val description = taskDescription.text.toString()

        if(description == "" && title == "")
        {
            Toast.makeText(requireContext(),"Enter task title or description",Toast.LENGTH_SHORT).show()
            return
        }
        if(finalDate == 0L){
            Toast.makeText(requireContext(),"Enter date",Toast.LENGTH_SHORT).show()
            return
        }
        if(finalTime == 0L){
            Toast.makeText(requireContext(),"Enter time",Toast.LENGTH_SHORT).show()
            return
        }

        GlobalScope.launch(Dispatchers.Main) {
            val id = withContext(Dispatchers.IO) {
                return@withContext db.todoDao().insertTask(
                    TodoModel(title, description, category, finalDate, finalTime)
                )
            }
//            onDestroy()
//            supp
            requireFragmentManager().popBackStack()
        }
        Constants.tag = TAG3
        Log.d("Saved","save")

    }

    private fun setTimeListener() {

        myCalendar = Calendar.getInstance()

        timeSetListener = TimePickerDialog.OnTimeSetListener{ _: TimePicker, hour: Int, minute: Int ->
            myCalendar.set(Calendar.HOUR_OF_DAY,hour)
            myCalendar.set(Calendar.MINUTE,minute)
            updateTime()
        }

        val timePickerDialog = TimePickerDialog(
            requireActivity(),timeSetListener,myCalendar.get(Calendar.HOUR_OF_DAY),myCalendar.get(Calendar.MINUTE),false
        )

        timePickerDialog.show()
    }

    private fun updateTime() {
        val myformat = "h : mm a"
        val stf = SimpleDateFormat(myformat)
        setTime.setText(stf.format(myCalendar.time))
        finalTime = myCalendar.time.time
    }

    private fun setDateListener() {


        myCalendar = Calendar.getInstance()
        dateSetListener = DatePickerDialog.OnDateSetListener{ _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            myCalendar.set(Calendar.YEAR,year)
            myCalendar.set(Calendar.MONTH,month)
            myCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth)

            updateDate()

        }

        val datePickerDialog = DatePickerDialog(
            requireActivity(),dateSetListener,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun updateDate() {
        val myFormat = "EEE, dd MMM yyyy"
        val sdf = SimpleDateFormat(myFormat)
        val x: String = sdf.format(myCalendar.time)
        setDate.setText(x)
        finalDate = myCalendar.time.time
        time_layout.visibility = View.VISIBLE

    }

    companion object {
        @JvmStatic
        fun newInstance() =
            TodoNewTaskFragment().apply {
                arguments = Bundle().apply {

                }
            }

//        fun save(context: Context){
//            Toast.makeText(context,"Saved", Toast.LENGTH_SHORT).show()
//        }
    }
}
