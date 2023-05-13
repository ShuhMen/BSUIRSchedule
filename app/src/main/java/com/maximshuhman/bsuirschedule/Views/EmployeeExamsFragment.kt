package com.maximshuhman.bsuirschedule.Views

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.maximshuhman.bsuirschedule.Data.EmployeeData
import com.maximshuhman.bsuirschedule.DataClasses.EmployeeExam
import com.maximshuhman.bsuirschedule.EmployeeLessonInf
import com.maximshuhman.bsuirschedule.R

class EmployeeExamsFragment : Fragment() {
    private var groupID: Int? = null
    private var groupInf: String? = null
    //  private var groupNum: String? = null

    lateinit var ExamsRecyclerView: RecyclerView
    lateinit var toolbarExams: Toolbar

    companion object {

        const val ID = "ID"

        const val NAME = "ID"
        const val INF = "INF"


        fun getBundle(
            id: Int,
            inf: String,
            //name: String
        ): Bundle {
            return Bundle().apply {
                putString(INF, inf)
                // putString(NAME, name)
                putInt(ID, id)
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        })

        arguments?.let {
            // groupNum = it.getString("NAME")
            groupInf = it.getString("INF")
            groupID = it.getInt("ID")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_employee_exams, container, false)

        ExamsRecyclerView = v.findViewById(R.id.exams_recycler_view)
        toolbarExams = v.findViewById(R.id.exams_toolbar)

        toolbarExams.title = "Экзамены"

        toolbarExams.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        toolbarExams.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        ExamsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        updateUI()

        return v
    }


    fun updateUI() {


        ExamsRecyclerView.adapter = null

        groupID?.let { EmployeeData.makeExams(requireContext(), it) }

        ExamsRecyclerView.adapter = ExamsRecyclerAdapter(EmployeeData.ExamsList)
        ExamsRecyclerView.recycledViewPool.clear()
        ExamsRecyclerView.adapter!!.notifyDataSetChanged()

    }

    inner class ExamsRecyclerAdapter(var exams: MutableList<EmployeeExam>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val TYPE_LIST: Int = 1

        override fun getItemViewType(position: Int): Int {
            return TYPE_LIST
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val itemView =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_exam_lesson_view, parent, false)

            return PairViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as PairViewHolder).bind(exams[position])
        }

        override fun getItemCount(): Int = exams.size


        inner class PairViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
            View.OnClickListener {

            init {
                itemView.setOnClickListener(this)
            }

            val PairNameText: TextView = itemView.findViewById(R.id.pair_name_text)
            val StartTimeText: TextView = itemView.findViewById(R.id.start_time_text)
            val EndTimeText: TextView = itemView.findViewById(R.id.end_time_text)
            val AuditoryText: TextView = itemView.findViewById(R.id.auditory_text)
            val SubGroupNumber: TextView = itemView.findViewById(R.id.subgroup_text)
            val SubGroupImage: ImageView = itemView.findViewById(R.id.subgroup_image)
            val Dividerindex: View = itemView.findViewById(R.id.divider)
            val EmployeesText: TextView = itemView.findViewById(R.id.employees_text)
            val NoteText: TextView = itemView.findViewById(R.id.note_text)
            val DateView: TextView = itemView.findViewById(R.id.date_view)


            @SuppressLint("SetTextI18n")
            fun bind(pair: EmployeeExam) {
                PairNameText.text = "${pair.subject} (${pair.lessonTypeAbbrev})"
                StartTimeText.text = pair.startLessonTime
                EndTimeText.text = pair.endLessonTime
                try {
                    if (pair.auditories.isEmpty())
                        AuditoryText.text = ""
                    else
                        AuditoryText.text = pair.auditories
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
                        ResourcesCompat.getDrawable(
                            itemView.resources,
                            R.drawable.divder_labs,
                            null
                        )
                }

                try {
                    var emp: String = ""
                    for (i in 0 until pair.groups.size) {
                        if (i != 0)
                            emp += "\n"
                        emp += "${pair.groups[i].name}"

                    }
                    EmployeesText.text = emp
                } catch (e: Exception) {
                    EmployeesText.text = ""
                }

                try {
                    if (pair.note == "" || pair.note == null)
                        NoteText.visibility = View.GONE
                    else {
                        NoteText.visibility = View.VISIBLE
                        NoteText.text = pair.note
                    }
                } catch (e: Exception) {
                    NoteText.visibility = View.GONE
                }
                try {

                    // val dayOfWeek :String = formatter.parse(date)
                    val dayOfMonth = pair.dateLesson!!.substring(0, 3)

                    DateView.text = /*"${
                        when (dayOfWeek) {
                            1 -> "Понедельник"
                            2 -> "Вторник"
                            3 -> "Среда"
                            4 -> "Четверг"
                            5 -> "Пятница"
                            6 -> "Суббота"
                            7 -> "Воскресенье"
                            else -> "Ошибка"
                        }
                    }," + */ pair.dateLesson!!.substring(0, 2) + " " +
                            when (pair.dateLesson!!.substring(3, 5)) {
                                "01" -> "Января"
                                "02" -> "Февраля"
                                "03" -> "Марта"
                                "04" -> "Апреля"
                                "05" -> "Мая"
                                "06" -> "Июня"
                                "07" -> "Июля"
                                "08" -> "Августа"
                                "09" -> "Сентября"
                                "10" -> "Октября"
                                "11" -> "Ноября"
                                "12" -> "Декабря"
                                else -> "Ошибка"
                            }
                } catch (e: Exception) {

                }

            }

            override fun onClick(p0: View?) {
                var groups = ""
                for (i in 0 until exams[layoutPosition].groups.size) {
                    if (i != 0)
                        groups += ", "
                    groups += "${exams[layoutPosition].groups[i].name}"

                }
                val args = EmployeeLessonInf.getBundle(
                    StartTimeText.text.toString(),
                    EndTimeText.text.toString(),
                    exams[layoutPosition].auditories,
                    groups,

                    exams[layoutPosition].subjectFullName.toString() + '(' + exams[layoutPosition].lessonTypeAbbrev + ')',
                    exams[layoutPosition].note
                )
                val navController = findNavController()
                try {
                    navController.navigate(
                        R.id.action_employeeExamsFragment_to_employeeLessonInf,
                        args
                    )
                } catch (e: IllegalArgumentException) {
                    Log.d("LessEmployeeInf", "EmployeeScheduleFragment")

                }
            }


        }

    }
}