package com.maximshuhman.bsuirschedule.Views

import Lesson
import android.annotation.SuppressLint
import android.database.Cursor
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.maximshuhman.bsuirschedule.Data.Data
import com.maximshuhman.bsuirschedule.DataBase.DBContract
import com.maximshuhman.bsuirschedule.DataBase.DbHelper
import com.maximshuhman.bsuirschedule.LessonInfDialog
import com.maximshuhman.bsuirschedule.MainActivity
import com.maximshuhman.bsuirschedule.PreferenceHelper
import com.maximshuhman.bsuirschedule.PreferenceHelper.openedGroup
import com.maximshuhman.bsuirschedule.R
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class ScheduleFragment : Fragment() {

    lateinit var ScheduleRecycler: RecyclerView
    lateinit var ProgressBar: ProgressBar
    lateinit var scheduleSituated: TextView
    private lateinit var ToolBar: androidx.appcompat.widget.Toolbar
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var executors: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val prefs = PreferenceHelper.defaultPreference(requireContext())
                prefs.openedGroup = 0
                findNavController().popBackStack()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_schedule, container, false)

        ScheduleRecycler = view.findViewById(R.id.schedule_recycler_view)
        ProgressBar = view.findViewById(R.id.progressBar)
        scheduleSituated = view.findViewById(R.id.schedule_situating_text)
        ToolBar = view.findViewById(R.id.toolbar)
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh)

        swipeRefreshLayout.setColorSchemeResources(R.color.BSUIR_blue)

        val prefs = PreferenceHelper.defaultPreference(requireContext())

        if (arguments?.getInt("id") != null) {
            Data.curGroupID = arguments?.getInt("id")
            prefs.openedGroup = arguments?.getInt("id")!!
        } else
            Data.curGroupID = null

        Data.curGroupName = arguments?.getString("groupNumber").toString()
        Data.curGroupSpeciality = arguments?.getString("specialityAbbrev").toString()

        if (arguments?.getInt("id") != null)
            Data.curGroupCourse = arguments?.getInt("course")
        else
            Data.curGroupCourse = 0



        ToolBar.title = "Группа ${Data.curGroupName}, " +
                "${Data.curGroupCourse} курс"


        //ToolBar.setSubtitleTextColor(R.color.white)
        //ToolBar.setTitleTextColor(R.color.white)

        ScheduleRecycler.layoutManager = LinearLayoutManager(requireContext())



        try {
            (requireActivity() as MainActivity).bottomNavigationView.menu.findItem(R.id.listOfGroupsFragment).isChecked =
                true
        } catch (_: UninitializedPropertyAccessException) {
            //do nothing
        }
        updateUI(0)

        //helloText.text = Data.response

        setHasOptionsMenu(true)

        (activity as AppCompatActivity?)!!.setSupportActionBar(ToolBar)

        ToolBar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        ToolBar.setNavigationOnClickListener {
            prefs.openedGroup = 0
            findNavController().popBackStack()
        }


        swipeRefreshLayout.setOnRefreshListener {
            updateUI(1)
        }

        return view
    }


    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.group_schedule_menu, menu)

        val favIndicator: MenuItem = menu.findItem(R.id.favorites)

        val dbHelper = DbHelper(requireContext())
        val db = dbHelper.writableDatabase

        val exist: Cursor =
            db.rawQuery(
                "SELECT COUNT(*) as cnt FROM ${DBContract.Favorites.TABLE_NAME} " +
                        "WHERE ${DBContract.Favorites.TABLE_NAME}.${DBContract.Favorites.groupID} = ${Data.curGroupID}",
                null
            )
        exist.moveToFirst()

        if (exist.getInt(0) != 0) {
            favIndicator.isChecked = true
            favIndicator.setIcon(R.drawable.ic_baseline_favorite_24)
        } else {
            favIndicator.isChecked = false
            favIndicator.setIcon(R.drawable.ic_baseline_favorite_border_24)
        }
        exist.close()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.favorites -> {

                if (item.isChecked) {
                    item.setIcon(R.drawable.ic_baseline_favorite_border_24)
                    Data.add_removeFavGroup(requireContext(), 1, Data.curGroupID!!)

                } else {
                    item.setIcon(R.drawable.ic_baseline_favorite_24)
                    Data.add_removeFavGroup(requireContext(), 0, Data.curGroupID!!)
                }
                item.isChecked = !item.isChecked



                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }


    }

    fun updateUI(mode: Int) {

        if (mode == 0) {
            ProgressBar.visibility = View.VISIBLE
            ProgressBar.isIndeterminate = true
        }
        ScheduleRecycler.adapter = null

        var err = 0

        executors =  Executors.newSingleThreadExecutor()
        executors.execute {


            err = Data.makeSchedule(
                Data.curGroupName,
                activity?.applicationContext,
                Data.curGroupID,
                mode
            )
            Handler(Looper.getMainLooper()).post {

                /*   if (err != 0)
                       Toast.makeText(
                           activity?.applicationContext,
                           "Ошибка получения данных",
                           Toast.LENGTH_SHORT
                       ).show()
   */
                if (Data.ScheduleList.size == 0) {
                    scheduleSituated.visibility = View.VISIBLE
                    ScheduleRecycler.visibility = View.GONE
                } else {
                    scheduleSituated.visibility = View.GONE
                    ScheduleRecycler.visibility = View.VISIBLE
                    ScheduleRecycler.adapter = ScheduleRecyclerAdapter(Data.ScheduleList)
                    ScheduleRecycler.recycledViewPool.clear()
                    ScheduleRecycler.adapter!!.notifyDataSetChanged()
                }
                ProgressBar.visibility = View.INVISIBLE
                swipeRefreshLayout.isRefreshing = false

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        Data.ScheduleList.clear()
        Data.listOfGroups.clear()
    executors.shutdown()

    }


    inner class ScheduleRecyclerAdapter(var pairs: MutableList<Lesson>) :
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
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_lesson_view, parent, false)
            return PairViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (pairs[position].day_of_week) {
                9 -> (holder as DayViewHolder).bind(pairs[position])
                else -> (holder as PairViewHolder).bind(pairs[position])
            }
        }

        override fun getItemCount(): Int = pairs.size


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


            @SuppressLint("SetTextI18n")
            fun bind(pair: Lesson) {
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
                    EmployeesText.text =
                        "${pair.employees.lastName} " +
                                "${pair.employees.firstName!!.substring(0, 1)}. " +
                                "${
                                    pair.employees.middleName!!.substring(0, 1)
                                }."
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
            }

            override fun onClick(p0: View?) {
                val args = LessonInfDialog.getBundle(
                    pairs[position].employees.photo,
                    StartTimeText.text.toString(),
                    EndTimeText.text.toString(),
                    pairs[position].auditories,

                    pairs[position].employees.lastName.toString() + ' ' +
                            pairs[position].employees.firstName.toString() + ' ' +
                            pairs[position].employees.middleName.toString(),

                    pairs[position].subjectFullName.toString() + '(' + pairs[position].lessonTypeAbbrev + ')',
                    pairs[position].note
                )
                val navController = findNavController()
                navController.navigate(R.id.action_scheduleFragment_to_lessonInfDialog, args)
            }


        }

        inner class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val DayNumberText: TextView = itemView.findViewById(R.id.day_number_text)
            fun bind(pair: Lesson) {
                DayNumberText.text = pair.note
            }
        }
    }
}