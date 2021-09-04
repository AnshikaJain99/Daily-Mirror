package com.example.dailymirror.fragment

import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dailymirror.AppDatabase
import com.example.dailymirror.R
import com.example.dailymirror.adapter.TodoAdapter
import com.example.dailymirror.model.TodoModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_journal.*
import kotlinx.android.synthetic.main.fragment_todo.*
import kotlinx.android.synthetic.main.fragment_todo_new_task.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


class TodoFragment : Fragment() {

    //    lateinit var bottomNavigationView : BottomNavigationView
    val list = arrayListOf<TodoModel>()
    var adapter = TodoAdapter(list)

    val db by lazy {
        AppDatabase.getDatabase(requireContext())
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_todo, container, false)

//        activity!!.setActionBar(journalToolbar)
//        (activity as AppCompatActivity).setSupportActionBar(todoToolbar)
        val todoFab = view.findViewById<FloatingActionButton>(R.id.todoFab)
        todoFab.setOnClickListener {
            val fragment = TodoNewTaskFragment.newInstance()
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.bottomFrameLayout,fragment,"Todo").addToBackStack("Todo").commit()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        setContentView(R.layout.activity_task)
//        setSupportActionBar(todoHomeToolbar)

        toback.setOnClickListener {
            requireActivity().onBackPressed()
        }

        todoRecyclerView.apply{activity
            layoutManager = LinearLayoutManager(activity)
            adapter = this@TodoFragment.adapter
        }

        initSwipe()

        db.todoDao().getTask().observe(viewLifecycleOwner,{
            if(!it.isNullOrEmpty()){
                list.clear()
                list.addAll(it)
                adapter.notifyDataSetChanged()
            }
            else{
                list.clear()
                adapter.notifyDataSetChanged()
            }
        })



    }

    fun initSwipe(){
        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT ){
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                if(direction == ItemTouchHelper.LEFT){
                    GlobalScope.launch(Dispatchers.IO) {
                        db.todoDao().deleteTask(adapter.getItemId(position))
                    }
                }
                else if(direction == ItemTouchHelper.RIGHT){
                    GlobalScope.launch(Dispatchers.IO) {
                        db.todoDao().finishTask(adapter.getItemId(position))
                    }
                }

            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean) {

                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
                    val itemView = viewHolder.itemView

                    val paint: Paint = Paint()
                    val icon: Bitmap

                    if(dX>0){

                        paint.color = Color.parseColor("#6CA3AE")
                        icon = BitmapFactory.decodeResource(resources,R.mipmap.ic_check)

                        c.drawRect(
                            itemView.left.toFloat(),itemView.top.toFloat(),
                            itemView.left.toFloat()+dX,itemView.bottom.toFloat(),
                            paint
                        )

                        c.drawBitmap(
                            icon,
                            itemView.left.toFloat(),
                            itemView.top.toFloat()+(itemView.bottom.toFloat() - itemView.top.toFloat() - icon.height.toFloat())/2,
                            paint
                        )
//                        Toast.makeText(this@MainActivity,"Task marked as done",Toast.LENGTH_SHORT).show()
                    }
                    else{
                        icon = BitmapFactory.decodeResource(resources,R.mipmap.ic_delete)
                        paint.color = Color.parseColor("#D32F2F")

                        c.drawRect(itemView.right.toFloat()+dX,
                            itemView.top.toFloat(),
                            itemView.right.toFloat(),itemView.bottom.toFloat(),
                            paint)

                        c.drawBitmap(
                            icon,itemView.right.toFloat()-icon.width,
                            itemView.top.toFloat()+(itemView.bottom.toFloat() - itemView.top.toFloat() - icon.height.toFloat())/2,
                            paint
                        )
//                        Toast.makeText(this@MainActivity,"Task deleted",Toast.LENGTH_SHORT).show()
                    }
                    viewHolder.itemView.translationX = dX
                }
                else {

                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(todoRecyclerView)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            TodoFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }


}








