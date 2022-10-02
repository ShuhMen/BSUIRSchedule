package com.maximshuhman.bsuirschedule

import Lesson
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract.CommonDataKinds.Note
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.Executors


class ScheduleFragment : Fragment() {

    private var adapter: ScheduleRecyclerAdapter? = null
    val bundle = Bundle()

    fun getSchedule(g: String?) {
        Data.makeSchedule(g!!)

    }

    lateinit var ScheduleRecycler: RecyclerView
    lateinit var ProgressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_schedule, container, false)
        // Inflate the layout for this fragment
        ScheduleRecycler = view.findViewById(R.id.schedule_recycler_view)
        ProgressBar = view.findViewById(R.id.progressBar)
        ScheduleRecycler.layoutManager = LinearLayoutManager(requireContext())

        (requireActivity() as MainActivity).bottomNavigationView.menu.findItem(2)

        updateUI(view, arguments?.getString("groupNumber").toString())
        //helloText.text = Data.response


        return view
    }

    fun updateUI(view: View, g: String?) {

        ProgressBar.visibility = View.VISIBLE
        ProgressBar.isIndeterminate = true

        ScheduleRecycler.adapter = null

        Executors.newSingleThreadExecutor().execute {


            getSchedule(g)
            Handler(Looper.getMainLooper()).post {

                ScheduleRecycler.adapter = ScheduleRecyclerAdapter(Data.ScheduleList)
                ScheduleRecycler.recycledViewPool.clear()
                ScheduleRecycler.adapter!!.notifyDataSetChanged()

                ProgressBar.visibility = View.INVISIBLE
            }
        }
    }


}

class ScheduleRecyclerAdapter(private val pairs: MutableList<Lesson>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_HEADER: Int = 0
    private val TYPE_LIST: Int = 1

    override fun getItemViewType(position: Int): Int {

        if (pairs[position].day_of_week == 9) {
            return TYPE_HEADER
        }
        return TYPE_LIST
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_HEADER) {
            val header = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_number_of_day_view, parent, false)
            return DayViewHolder(header)
        }
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_lesson_view, parent, false)
        return PairViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (pairs[position].day_of_week) {
            9 -> (holder as DayViewHolder).bind(pairs[position])
            else -> (holder as PairViewHolder).bind(pairs[position])
        }
    }

    override fun getItemCount(): Int = pairs.size


    inner class PairViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val PairNameText: TextView = itemView.findViewById(R.id.pair_name_text)
        val StartTimeText: TextView = itemView.findViewById(R.id.start_time_text)
        val EndTimeText: TextView = itemView.findViewById(R.id.end_time_text)
        val AuditoryText: TextView = itemView.findViewById(R.id.auditory_text)
        val SubGroupNumber: TextView = itemView.findViewById(R.id.subgroup_text)
        val SubGroupImage: ImageView = itemView.findViewById(R.id.subgroup_image)
        val Dividerindex: View = itemView.findViewById(R.id.divider)
        val EmployeesText: TextView = itemView.findViewById(R.id.employees_text)
        val NoteText: TextView = itemView.findViewById(R.id.note_text)


        @SuppressLint("SetTextI18n")
        fun bind(pair: Lesson) {
            PairNameText.text = "${pair.subject} (${pair.lessonTypeAbbrev})"
            StartTimeText.text = pair.startLessonTime
            EndTimeText.text = pair.endLessonTime
            try {
                AuditoryText.text = pair.auditories[0]
            } catch (e: Exception) {
            }
            if (pair.numSubgroup != 0) {
                SubGroupNumber.visibility = View.VISIBLE
                SubGroupImage.visibility = View.VISIBLE
                SubGroupNumber.text = pair.numSubgroup.toString()
            } else {
                SubGroupNumber.visibility = View.INVISIBLE
                SubGroupImage.visibility = View.INVISIBLE
            }
            when (pair.lessonTypeAbbrev) {
                "ПЗ" -> Dividerindex.foreground = ResourcesCompat.getDrawable(
                    itemView.resources,
                    R.drawable.divder_practical,
                    null
                )
                "ЛК" -> Dividerindex.foreground = ResourcesCompat.getDrawable(
                    itemView.resources,
                    R.drawable.divder_lectures,
                    null
                )
                "ЛР" -> Dividerindex.foreground =
                    ResourcesCompat.getDrawable(itemView.resources, R.drawable.divder_labs, null)
            }

            try {
                EmployeesText.text =
                    "${pair.employees!![0].lastName} " +
                            "${pair.employees!![0].firstName!!.substring(0, 1)}. " +
                            "${pair.employees!![0].middleName!!.substring(0, 1)
                    }."
            } catch (e: Exception) {
                EmployeesText.text = ""
            }

            try {
                if(pair.note == "" || pair.note == null)
                    NoteText.visibility = View.GONE
                    else {
                    NoteText.visibility = View.VISIBLE
                    NoteText.text = pair.note
                }
            } catch (e: Exception) {
                NoteText.visibility = View.GONE
            }
        }


    }

    inner class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val DayNumberText: TextView = itemView.findViewById(R.id.day_number_text)
        fun bind(pair: Lesson) {
            DayNumberText.text = pair.note
        }
    }
}