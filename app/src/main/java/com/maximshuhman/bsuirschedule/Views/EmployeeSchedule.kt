package com.maximshuhman.bsuirschedule.Views

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.Cursor
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.maximshuhman.bsuirschedule.*
import com.maximshuhman.bsuirschedule.Data.EmployeeData
import com.maximshuhman.bsuirschedule.Data.Requests
import com.maximshuhman.bsuirschedule.DataBase.DBContract
import com.maximshuhman.bsuirschedule.DataBase.DbHelper
import com.maximshuhman.bsuirschedule.DataClasses.EmployeeLesson
import com.maximshuhman.bsuirschedule.PreferenceHelper.openedGroup
import com.maximshuhman.bsuirschedule.PreferenceHelper.openedType
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class EmployeeSchedule : Fragment() {
    lateinit var ScheduleRecycler: RecyclerView
    lateinit var ProgressBar: ProgressBar
    lateinit var scheduleSituated: TextView
    private lateinit var ToolBar: androidx.appcompat.widget.Toolbar
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var executors: ExecutorService
    lateinit var endOfSchedule: TextView
    lateinit var floatingButton: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val prefs = PreferenceHelper.defaultPreference(requireContext())
                prefs.openedGroup = 0
                prefs.openedType = 1
                // (requireActivity() as MainActivity).bottomNavigationView.visibility = View.VISIBLE
                findNavController().popBackStack()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_employee_schedule, container, false)

        ScheduleRecycler = view.findViewById(R.id.schedule_recycler_view)
        ProgressBar = view.findViewById(R.id.progressBar)
        scheduleSituated = view.findViewById(R.id.schedule_situating_text)
        ToolBar = view.findViewById(R.id.toolbar)
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh)
        endOfSchedule = view.findViewById(R.id.endOfSchedule)
        floatingButton = view.findViewById(R.id.floatingActionButton)

        floatingButton.hide()
        swipeRefreshLayout.setColorSchemeResources(R.color.BSUIR_blue)

        val prefs = PreferenceHelper.defaultPreference(requireContext())

        if (arguments?.getInt("id") != null) {
            EmployeeData.curEmployeeID = arguments?.getInt("id")
            prefs.openedGroup = arguments?.getInt("id")!!
            prefs.openedType = 1
        } else
            EmployeeData.curEmployeeID = null

        if (arguments?.getString("employeeName") != null) {
            EmployeeData.curEmployeeName = arguments?.getString("employeeName").toString()
            //prefs.openedGroup = arguments?.getInt("id")!!
        } else
            EmployeeData.curEmployeeName = ""

        val fio = if (arguments?.getString("FIO") != null) {
          arguments?.getString("FIO").toString()
            //prefs.openedGroup = arguments?.getInt("id")!!
        } else
            ""
        if (arguments?.getString("urlId") != null) {
            EmployeeData.curEmployeeUrlId =  arguments?.getString("urlId").toString()
            //prefs.openedGroup = arguments?.getInt("id")!!
        } else
            EmployeeData.curEmployeeUrlId = ""
        //curGroupName = arguments?.getString("groupNumber").toString()
        //curGroupSpeciality = arguments?.getString("specialityAbbrev").toString()

        /*if (arguments?.getInt("id") != null)
            curGroupCourse = arguments?.getInt("course")
        else
            curGroupCourse = 0
*/
        ToolBar.title = fio


        val dbHelper = DbHelper(requireContext())
        val db = dbHelper.writableDatabase

        val exist = db.rawQuery(
            "SELECT COUNT(*) as cnt FROM ${DBContract.CommonEmployee.TABLE_NAME} WHERE ${DBContract.CommonEmployee.commonEmployeeID} = ${EmployeeData.curEmployeeID}",
            null
        )
        exist.moveToFirst()
        if (exist.getInt(0) != 0) {
            exist.close()
            //ToolBar.setSubtitleTextColor(R.color.white)
            //ToolBar.setTitleTextColor(R.color.white)
            ScheduleRecycler.layoutManager = LinearLayoutManager(requireContext())
            val lastUpdateResponse =
                Requests.getEmployeeScheduleLastUpdate(EmployeeData.curEmployeeID!!)
            var lastUpdate = ""

            if (lastUpdateResponse.errorCode == 0) {
                lastUpdate = lastUpdateResponse.res

                val formatter =
                    SimpleDateFormat("dd.MM.yyyy", Locale.getDefault(Locale.Category.FORMAT))

                val lastUpdateDate = formatter.parse(lastUpdate)
                //  val calendar = Calendar.getInstance()
                //val curent = formatter.parse(formatter.format(calendar.time))

                val c: Cursor = db.rawQuery(
                    "SELECT * FROM ${DBContract.CommonEmployee.TABLE_NAME} WHERE ${DBContract.CommonEmployee.commonEmployeeID} = ${EmployeeData.curEmployeeID}",
                    null
                )

                var curLastUpdate = ""
                c.moveToFirst()

                with(c) {
                    curLastUpdate =
                        getString(getColumnIndexOrThrow(DBContract.CommonSchedule.lastUpdate))
                }
                c.close()
                if (curLastUpdate != "") {
                    if (formatter.parse(curLastUpdate).before(lastUpdateDate)) {

                        MaterialAlertDialogBuilder(requireContext(), R.style.RoundShapeTheme)
                            .setTitle(resources.getString(R.string.update))
                            .setMessage(resources.getString(R.string.update_schedule))
                            .setPositiveButton(resources.getString(R.string.yes)) { dialogInterface, i ->
                                updateUI(1)
                                val values = ContentValues().apply {
                                    put(DBContract.CommonEmployee.lastUpdate, lastUpdate)
                                }
                                db.update(
                                    DBContract.CommonEmployee.TABLE_NAME,
                                    values,
                                    "${DBContract.CommonEmployee.commonEmployeeID} = ${EmployeeData.curEmployeeID}",
                                    null
                                )
                            }
                            .setNegativeButton(resources.getString(R.string.no)) { dialogInterface, i ->
                                updateUI(0)
                            }
                            .show()
                    } else
                        updateUI(0)
                } else
                    updateUI(0)

            } else
                updateUI(0)
        } else {
            exist.close()
            updateUI(1)
        }



        ScheduleRecycler.layoutManager = RecyclerLinearManager(requireContext())

        ScheduleRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                /*  if ((recyclerView?.layoutManager as LinearLayoutManager)
                          .findFirstCompletelyVisibleItemPosition() == 0) {
                      floatingButton.visibility = View.GONE
                  }*/
                if (dy > 10 && floatingButton.isShown) {
                    floatingButton.hide()
                }

                // if the recycler view is
                // scrolled above show the FAB
                if (dy < -10 && !floatingButton.isShown) {
                    floatingButton.show()
                }

                // of the recycler view is at the first
                // item always show the FAB
                if (!recyclerView.canScrollVertically(-1)) {
                    floatingButton.hide()
                }

            }
        })

        floatingButton.setOnClickListener {
            ScheduleRecycler.smoothScrollToPosition(0)
        }


        (activity as AppCompatActivity?)!!.setSupportActionBar(ToolBar)

        ToolBar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        ToolBar.setNavigationOnClickListener {
            prefs.openedGroup = 0
            prefs.openedType = 1
            //(requireActivity() as MainActivity).bottomNavigationView.visibility = View.VISIBLE
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

        var exams = db.rawQuery(
            "SELECT COUNT(*) as cnt FROM ${DBContract.EmployeeExams.TABLE_NAME} WHERE ${DBContract.EmployeeExams.TABLE_NAME}.${DBContract.EmployeeSchedule.employeeID} = ${EmployeeData.curEmployeeID}",
            null
        )
        exams.moveToFirst()
        if (exams.getInt(0) != 0) {
            menu.findItem(R.id.exams).isVisible = true
            exams.close()

            exams = db.rawQuery(
                "SELECT ${DBContract.CommonEmployee.endExamsDate} FROM ${DBContract.CommonEmployee.TABLE_NAME} WHERE ${DBContract.CommonEmployee.TABLE_NAME}.${DBContract.CommonEmployee.commonEmployeeID} = ${EmployeeData.curEmployeeID}",
                null
            )

            exams.moveToFirst()

            val endString =
                exams.getString(exams.getColumnIndexOrThrow(DBContract.CommonSchedule.endExamsDate))
            if (endString != null) {
                var calendar: Calendar = Calendar.getInstance()
                val formatter =
                    SimpleDateFormat("dd.MM.yyyy", Locale.getDefault(Locale.Category.FORMAT))
                var curent = formatter.parse(formatter.format(calendar.time))
                val end = formatter.parse(endString)


                if (curent.after(end))
                    menu.findItem(R.id.exams).isVisible = false
            }
            exams.close()
        }
        exams.close()


        val exist: Cursor =
            db.rawQuery(
                "SELECT COUNT(*) as cnt FROM ${DBContract.Favorites.TABLE_NAME} " +
                        "WHERE ${DBContract.Favorites.TABLE_NAME}.${DBContract.Favorites.groupID} = ${EmployeeData.curEmployeeID}",
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

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.favorites -> {

                if (item.isChecked) {
                    item.setIcon(R.drawable.ic_baseline_favorite_border_24)
                    EmployeeData.add_removeFavGroup(requireContext(), 1, EmployeeData.curEmployeeID!!)

                } else {
                    item.setIcon(R.drawable.ic_baseline_favorite_24)
                    EmployeeData.add_removeFavGroup(requireContext(), 0, EmployeeData.curEmployeeID!!)
                }
                item.isChecked = !item.isChecked



                return true
            }

            R.id.exams -> {
                val args = EmployeeData.curEmployeeID?.let {
                    EmployeeExamsFragment.getBundle(
                        it,
                        ToolBar.title.toString()
                    )
                }
                val navController = findNavController()
                navController.navigate(R.id.action_employeeSchedule_to_employeeExamsFragment, args)

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

        executors = Executors.newSingleThreadExecutor()
        executors.execute {


            err = EmployeeData.makeSchedule(
                EmployeeData.curEmployeeUrlId,
                activity?.applicationContext,
                EmployeeData.curEmployeeID,
                mode
            )
            Handler(Looper.getMainLooper()).post {
                when (err) {
                    0 -> {
                        if (EmployeeData.ScheduleList.size == 0) {
                            scheduleSituated.visibility = View.VISIBLE
                            ScheduleRecycler.visibility = View.INVISIBLE
                        } else {
                            scheduleSituated.visibility = View.GONE
                            ScheduleRecycler.visibility = View.VISIBLE
                        }
                    }
                    4 -> {
                        endOfSchedule.visibility = View.VISIBLE
                    }
                    5 -> try {
                        Toast.makeText(
                            context,
                            "Расписание отсутствует!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: java.lang.NullPointerException) {
                        Firebase.crashlytics.log("SсheduleFragmentToast5")
                    }
                    6 -> try {
                        Toast.makeText(
                            context,
                            "Что-то пошло не так",
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: java.lang.NullPointerException) {
                        Firebase.crashlytics.log("SсheduleFragmentToast6")
                    }
                    else -> try {
                        Toast.makeText(
                            context,
                            "Ошибка получения данных",
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: java.lang.NullPointerException) {
                        Firebase.crashlytics.log("SсheduleFragmentToastElse")
                    }
                }

                ProgressBar.visibility = View.INVISIBLE
                swipeRefreshLayout.isRefreshing = false
                ScheduleRecycler.adapter = ScheduleRecyclerAdapter(EmployeeData.ScheduleList)
                ScheduleRecycler.adapter!!.notifyDataSetChanged()
                setHasOptionsMenu(true)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        EmployeeData.ScheduleList.clear()
        //  EmployeeData.clear()
        executors.shutdown()

    }


    inner class ScheduleRecyclerAdapter(var pairs: MutableList<EmployeeLesson>) :
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
            fun bind(pair: EmployeeLesson) {
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
                        if(i%2 != 0)
                            emp += ", "
                        if (i != 0 && i%2 == 0)
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
            }

            override fun onClick(p0: View?) {
                var groups = ""
                for (i in 0 until pairs[layoutPosition].groups.size) {
                    if (i != 0)
                        groups += ", "
                    groups += "${pairs[layoutPosition].groups[i].name}"

                }
                  val args = EmployeeLessonInf.getBundle(
                      StartTimeText.text.toString(),
                      EndTimeText.text.toString(),
                      pairs[layoutPosition].auditories,
                      groups,

                      pairs[layoutPosition].subjectFullName.toString() + '(' + pairs[layoutPosition].lessonTypeAbbrev + ')',
                      pairs[layoutPosition].note
                  )
                val navController = findNavController()
                try {
                    navController.navigate(R.id.action_employeeSchedule_to_employeeLessonInf, args)
                } catch (e: IllegalArgumentException) {
                    Log.d("LessEmployeeInf", "EmployeeScheduleFragment")

                }
            }


            }

            inner class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
                val DayNumberText: TextView = itemView.findViewById(R.id.day_number_text)
                fun bind(pair: EmployeeLesson) {
                    DayNumberText.text = pair.note
                }
            }
        }

}
