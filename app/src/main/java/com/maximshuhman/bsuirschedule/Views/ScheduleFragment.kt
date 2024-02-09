package com.maximshuhman.bsuirschedule.Views

import Lesson
import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.database.getIntOrNull
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.maximshuhman.bsuirschedule.Data.Requests
import com.maximshuhman.bsuirschedule.Data.StudentData
import com.maximshuhman.bsuirschedule.DataBase.DBContract
import com.maximshuhman.bsuirschedule.DataBase.DbHelper
import com.maximshuhman.bsuirschedule.LessonInfDialog
import com.maximshuhman.bsuirschedule.R
import com.maximshuhman.bsuirschedule.RecyclerLinearManager
import com.maximshuhman.bsuirschedule.widget.ScheduleWidget
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class ScheduleFragment : Fragment() {

    private lateinit var scheduleRecycler: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var scheduleSituated: TextView
    private lateinit var toolBar: androidx.appcompat.widget.Toolbar
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var executors: ExecutorService
    private lateinit var endOfSchedule: TextView
    lateinit var floatingButton: FloatingActionButton

    private var mRecyclerViewAdapter: ScheduleRecyclerAdapter? = null
    private var recyclerViewLinearManager: LinearLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val db = DbHelper(requireContext()).writableDatabase
                val values = ContentValues().apply {
                    put(DBContract.Settings.openedID, 0)
                    put(DBContract.Settings.openedType, 2)
                }

                db.update(DBContract.Settings.TABLE_NAME, values, null, null)
                // (requireActivity() as MainActivity).bottomNavigationView.visibility = View.VISIBLE
                findNavController().popBackStack()
            }
        })
    }

    private var dataFilter = StudentData.ScheduleList
    private lateinit var rawData: MutableList<Lesson>

    lateinit var db: SQLiteDatabase
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_schedule, container, false)

        scheduleRecycler = view.findViewById(R.id.schedule_recycler_view)
        progressBar = view.findViewById(R.id.progressBar)
        scheduleSituated = view.findViewById(R.id.schedule_situating_text)
        toolBar = view.findViewById(R.id.toolbar)
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh)
        endOfSchedule = view.findViewById(R.id.endOfSchedule)
        floatingButton = view.findViewById(R.id.floatingActionButton)

        floatingButton.hide()
        swipeRefreshLayout.setColorSchemeResources(R.color.BSUIR_blue)

        //  val prefs = PreferenceHelper.defaultPreference(requireContext())

        val dbHelper = DbHelper(requireContext())
        db = dbHelper.writableDatabase

        if (arguments?.getInt("id") != null) {
            StudentData.curGroupID = arguments?.getInt("id")
            val values = ContentValues().apply {
                put(DBContract.Settings.openedID, arguments?.getInt("id"))
                put(DBContract.Settings.openedType, 0)
                put(DBContract.Settings.widgetID, arguments?.getInt("id"))
                put(DBContract.Settings.widgetOpened, 0)
            }

            db.update(DBContract.Settings.TABLE_NAME, values, null, null)
            //  prefs.openedGroup = arguments?.getInt("id")!!
            //prefs.openedType = 0
        } else
            StudentData.curGroupID = null

        StudentData.curGroupName = arguments?.getString("groupNumber").toString()
        StudentData.curGroupSpeciality = arguments?.getString("specialityAbbrev").toString()

        if (arguments?.getInt("id") != null)
            StudentData.curGroupCourse = arguments?.getInt("course")
        else
            StudentData.curGroupCourse = 0


        val appWidgetIds: IntArray = AppWidgetManager.getInstance(requireContext())
            .getAppWidgetIds(ComponentName(requireContext(), ScheduleWidget::class.java))
        appWidgetIds.forEach { appWidgetId ->
            val appWidgetManager = AppWidgetManager.getInstance(context)

            val views = RemoteViews(requireContext().packageName, R.layout.schedule_widget)
            val c = db.rawQuery(
                "SELECT COUNT(*) as cnt FROM " +
                        "${DBContract.Groups.TABLE_NAME} WHERE ${DBContract.Groups.groupID} = ${StudentData.curGroupID}",
                null
            )
            c.moveToFirst()
            if (c.getInt(0) != 0) {
                c.close()
                val cursor = db.rawQuery(
                    "SELECT * FROM " +
                            "${DBContract.Groups.TABLE_NAME} WHERE ${DBContract.Groups.groupID} = ${StudentData.curGroupID}",
                    null
                )

                cursor.moveToFirst()

                val name = cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Groups.name))

                views.setTextViewText(R.id.name_text, "Группа $name")
            }
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.list_view)

            appWidgetManager.updateAppWidget(appWidgetIds, views)
        }
        toolBar.title = "Группа ${StudentData.curGroupName} " //+
        // "${Data.curGroupCourse} курс"


        val exist = db.rawQuery(
            "SELECT COUNT(*) as cnt FROM ${DBContract.CommonSchedule.TABLE_NAME} WHERE ${DBContract.CommonSchedule.commonScheduleID} = ${StudentData.curGroupID}",
            null
        )
        exist.moveToFirst()
        if (exist.getInt(0) != 0) {
            exist.close()
            //ToolBar.setSubtitleTextColor(R.color.white)
            //ToolBar.setTitleTextColor(R.color.white)

            recyclerViewLinearManager = LinearLayoutManager(requireContext())
            scheduleRecycler.layoutManager = recyclerViewLinearManager

            val lastUpdateResponse = Requests.getGroupScheduleLastUpdate(StudentData.curGroupName)
            val lastUpdate: String

            if (lastUpdateResponse.errorCode == 0) {
                lastUpdate = lastUpdateResponse.res

                val formatter =
                    SimpleDateFormat("dd.MM.yyyy", Locale.getDefault(Locale.Category.FORMAT))

                val lastUpdateDate = formatter.parse(lastUpdate)
                //  val calendar = Calendar.getInstance()
                //val curent = formatter.parse(formatter.format(calendar.time))

                val c: Cursor = db.rawQuery(
                    "SELECT * FROM ${DBContract.CommonSchedule.TABLE_NAME} WHERE ${DBContract.CommonSchedule.commonScheduleID} = ${StudentData.curGroupID}",
                    null
                )

                var curLastUpdate: String
                c.moveToFirst()

                with(c) {
                    curLastUpdate =
                        getString(getColumnIndexOrThrow(DBContract.CommonSchedule.lastUpdate))
                }
                c.close()
                if (curLastUpdate != "") {
                    if (formatter.parse(curLastUpdate)!!.before(lastUpdateDate)) {

                        MaterialAlertDialogBuilder(requireContext(), R.style.RoundShapeTheme)
                            .setTitle(resources.getString(R.string.update))
                            .setMessage(resources.getString(R.string.update_schedule))
                            .setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                                updateUI(1)
                                val values = ContentValues().apply {
                                    put(DBContract.CommonSchedule.lastUpdate, lastUpdate)
                                }
                                db.update(
                                    DBContract.CommonSchedule.TABLE_NAME,
                                    values,
                                    "${DBContract.CommonSchedule.commonScheduleID} = ${StudentData.curGroupID}",
                                    null
                                )
                            }
                            .setNegativeButton(resources.getString(R.string.no)) { _, _ ->
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


        /*  try {
              (requireActivity() as MainActivity).bottomNavigationView.visibility =
                      //.menu.findItem(R.id.favoritesFragment).isChecked =
                      //true
                  View.GONE
          } catch (_: UninitializedPropertyAccessException) {
              //do nothing
          }
  */
        scheduleRecycler.layoutManager = RecyclerLinearManager(requireContext())

        scheduleRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
            scheduleRecycler.smoothScrollToPosition(0)
        }

        (activity as AppCompatActivity?)!!.setSupportActionBar(toolBar)

        toolBar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        toolBar.setNavigationOnClickListener {
            val values = ContentValues().apply {
                put(DBContract.Settings.openedID, 0)
                put(DBContract.Settings.openedType, 2)
            }

            db.update(DBContract.Settings.TABLE_NAME, values, null, null)
            //prefs.openedGroup = 0
            // prefs.openedType = 0
            //(requireActivity() as MainActivity).bottomNavigationView.visibility = View.VISIBLE
            findNavController().popBackStack()
        }

        swipeRefreshLayout.setOnRefreshListener {
            updateUI(1)
        }



        return view
    }

    private fun filter(subGroup: Int) {
        rawData = StudentData.ScheduleList.map { it.copy() } as MutableList<Lesson>
        //   dataFilter = StudentData.ScheduleList//.map { it.copy() } as MutableList<Lesson>


        //val pos = ScheduleRecycler.layoutManager.findFirstVisibleItemPosition()

        when (subGroup) {
            0 -> {
                var i = 0
                var j = 0
                while (i < dataFilter.size && j < rawData.size) {

                    if (dataFilter[i] != rawData[j]) {
                        scheduleRecycler.adapter!!.notifyItemInserted(i)
                        dataFilter.add(i, rawData[j])
                    }

                    i++
                    j++
                }
                if (j < rawData.size) {
                    while (j < rawData.size) {
                        scheduleRecycler.adapter!!.notifyItemInserted(i)
                        dataFilter.add(i, rawData[j])

                        i++
                        j++
                    }
                }
            }

            1 -> {
                var i = 0
                while (i < dataFilter.size) {

                    if (dataFilter[i].numSubgroup == 2) {
                        scheduleRecycler.adapter!!.notifyItemRemoved(i)
                        dataFilter.removeAt(i)
                        i--
                    }
                    i++
                }
            }

            2 -> {
                var i = 0
                var j = 0
                while (i < dataFilter.size && j < rawData.size) {

                    if (dataFilter[i] != rawData[j]) {
                        scheduleRecycler.adapter!!.notifyItemInserted(i)
                        dataFilter.add(i, rawData[j])
                    }

                    i++
                    j++
                }
                if (j < rawData.size) {
                    while (j < rawData.size) {
                        scheduleRecycler.adapter!!.notifyItemInserted(i)
                        dataFilter.add(i, rawData[j])

                        i++
                        j++
                    }
                }

                i = 0
                while (i < dataFilter.size) {

                    if (dataFilter[i].numSubgroup == 1) {
                        scheduleRecycler.adapter!!.notifyItemRemoved(i)
                        dataFilter.removeAt(i)
                        i--
                    }
                    i++
                }

               /* i = 0
                j = 0
                while (i < dataFilter.size) {
                    if (dataFilter[i].numSubgroup == 1) {
                        dataFilter.removeAt(i)
                        scheduleRecycler.adapter!!.notifyItemRemoved(i)
                        i--
                    }
                    i++
                }

                i = 0
                j = 0
                while (i < dataFilter.size && j < rawData.size) {
                    while (rawData[j].numSubgroup == 1)
                        j++
                    if (dataFilter[i] != rawData[j] && rawData[j].numSubgroup == 2) {
                        dataFilter.add(i, rawData[j])
                        scheduleRecycler.adapter!!.notifyItemInserted(i)
                    }



                    i++
                    j++
                }


                if (j < rawData.size) {
                    while (j < rawData.size) {
                        while (rawData[j].numSubgroup == 1)
                            j++
                        if (rawData[j].numSubgroup == 2) {
                            scheduleRecycler.adapter!!.notifyItemInserted(i)
                            dataFilter.add(i, rawData[j])
                        }

                        i++
                        j++
                    }
                }
*/
            }

            else -> {
                var i = 0
                var j = 0
                while (i < dataFilter.size && j < rawData.size) {

                    if (dataFilter[i] != rawData[j]) {
                        scheduleRecycler.adapter!!.notifyItemInserted(i)
                        dataFilter.add(i, rawData[j])
                    }

                    i++
                    j++
                }
                if (j < rawData.size) {
                    while (j < rawData.size) {
                        scheduleRecycler.adapter!!.notifyItemInserted(i)
                        dataFilter.add(i, rawData[j])

                        i++
                        j++
                    }
                }
            }

        }
        var i = 0
        while( i < dataFilter.size)
        {
            if(dataFilter[i].day_of_week == 9 && dataFilter[i+1].day_of_week == 9) {
                dataFilter.removeAt(i)
                scheduleRecycler.adapter!!.notifyItemRemoved(i)
                i--
            }
i++
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.group_schedule_menu, menu)

        val favIndicator: MenuItem = menu.findItem(R.id.favorites)

        var exams = db.rawQuery(
            "SELECT COUNT(*) as cnt FROM ${DBContract.Exams.TABLE_NAME} WHERE ${DBContract.Exams.TABLE_NAME}.${DBContract.Schedule.groupID} = ${StudentData.curGroupID}",
            null
        )
        exams.moveToFirst()
        if (exams.getInt(0) != 0) {
            menu.findItem(R.id.exams).isVisible = true
            exams.close()

            exams = db.rawQuery(
                "SELECT ${DBContract.CommonSchedule.endExamsDate} FROM ${DBContract.CommonSchedule.TABLE_NAME} WHERE ${DBContract.CommonSchedule.TABLE_NAME}.${DBContract.CommonSchedule.commonScheduleID} = ${StudentData.curGroupID}",
                null
            )

            exams.moveToFirst()

            val endString =
                exams.getString(exams.getColumnIndexOrThrow(DBContract.CommonSchedule.endExamsDate))
            if (endString != null) {
                val calendar: Calendar = Calendar.getInstance()
                val formatter =
                    SimpleDateFormat("dd.MM.yyyy", Locale.getDefault(Locale.Category.FORMAT))
                val curent = formatter.parse(formatter.format(calendar.time))
                val end = formatter.parse(endString)


                if (curent!!.after(end))
                    menu.findItem(R.id.exams).isVisible = false
            }
            exams.close()
        }
        exams.close()


        var exist: Cursor =
            db.rawQuery(
                "SELECT COUNT(*) as cnt FROM ${DBContract.Favorites.TABLE_NAME} " +
                        "WHERE ${DBContract.Favorites.TABLE_NAME}.${DBContract.Favorites.groupID} = ${StudentData.curGroupID}",
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


        exist =
            db.rawQuery(
                "SELECT COUNT(*) as cnt FROM ${DBContract.SubgroupSettings.TABLE_NAME} " +
                        "WHERE ${DBContract.SubgroupSettings.TABLE_NAME}.${DBContract.SubgroupSettings.groupID} = ${StudentData.curGroupID}",
                null
            )
        exist.moveToFirst()
        var subgroup = 0

        if (exist.getIntOrNull(exist.getColumnIndexOrThrow("cnt")) != 0) {

            exist.close()
            exist =
                db.rawQuery(
                    "SELECT * FROM ${DBContract.SubgroupSettings.TABLE_NAME} " +
                            "WHERE ${DBContract.SubgroupSettings.TABLE_NAME}.${DBContract.SubgroupSettings.groupID} = ${StudentData.curGroupID}",
                    null
                )
            exist.moveToFirst()



            subgroup =
                exist.getIntOrNull(exist.getColumnIndexOrThrow(DBContract.SubgroupSettings.subGroup))!!
            exist.close()
        } else {
            exist.close()
            val values = ContentValues().apply {
                put(DBContract.SubgroupSettings.groupID, StudentData.curGroupID)
                put(DBContract.SubgroupSettings.subGroup, 0)
            }
            db.insert(DBContract.SubgroupSettings.TABLE_NAME, null, values)
        }


        when (subgroup) {
            1 -> {
                //filter(1)
                menu.findItem(R.id.subgroup).setIcon(R.drawable.subgroup1_white)
            }

            2 -> {
                // filter(2)
                menu.findItem(R.id.subgroup).setIcon(R.drawable.subgroup2_white)

            }

            else -> {
                menu.findItem(R.id.subgroup).setIcon(R.drawable.group_svgrepo_com)
            }
        }


    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.favorites -> {


                if (item.isChecked) {
                    item.setIcon(R.drawable.ic_baseline_favorite_border_24)
                    StudentData.add_removeFavGroup(requireContext(), 1, StudentData.curGroupID!!)

                } else {
                    item.setIcon(R.drawable.ic_baseline_favorite_24)
                    StudentData.add_removeFavGroup(requireContext(), 0, StudentData.curGroupID!!)
                }
                item.isChecked = !item.isChecked

                return true
            }

            R.id.exams -> {
                val args = StudentData.curGroupID?.let {
                    ExamsFragment.getBundle(
                        it,
                        toolBar.title.toString()
                    )
                }
                val navController = findNavController()
                navController.navigate(R.id.action_scheduleFragment_to_examsFragment, args)

                return true
            }

            R.id.subgroup -> {


                val exist =
                    db.rawQuery(
                        "SELECT * FROM ${DBContract.SubgroupSettings.TABLE_NAME} " +
                                "WHERE ${DBContract.SubgroupSettings.TABLE_NAME}.${DBContract.SubgroupSettings.groupID} = ${StudentData.curGroupID}",
                        null
                    )
                exist.moveToFirst()

                val subgroup =
                    exist.getIntOrNull(exist.getColumnIndexOrThrow(DBContract.SubgroupSettings.subGroup))
                exist.close()
                if (subgroup != null) {
                    when (subgroup) {
                        1 -> {
                            filter(2)
                            StudentData.setSubGroup(requireContext(), 2, StudentData.curGroupID!!)
                            item.setIcon(R.drawable.subgroup2_white)
                        }

                        2 -> {
                            filter(0)
                            StudentData.setSubGroup(requireContext(), 0, StudentData.curGroupID!!)
                            item.setIcon(R.drawable.group_svgrepo_com)
                        }

                        else -> {
                            filter(1)
                            StudentData.setSubGroup(requireContext(), 1, StudentData.curGroupID!!)
                            item.setIcon(R.drawable.subgroup1_white)
                        }
                    }
                }

                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }


    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateUI(mode: Int) {

        if (mode == 0) {
            progressBar.visibility = View.VISIBLE
            progressBar.isIndeterminate = true
        }
        // ScheduleRecycler.adapter = null


        var err: Int

        executors = Executors.newSingleThreadExecutor()
        executors.execute {


            err = StudentData.makeSchedule(
                StudentData.curGroupName,
                activity?.applicationContext,
                StudentData.curGroupID,
                mode
            )
            Handler(Looper.getMainLooper()).post {
                when (err) {
                    0 -> {
                        dataFilter =
                            StudentData.ScheduleList.map { it.copy() } as MutableList<Lesson>

                        if (dataFilter.size == 0) {
                            scheduleSituated.visibility = View.VISIBLE
                            scheduleRecycler.visibility = View.INVISIBLE
                        } else {
                            scheduleSituated.visibility = View.GONE
                            scheduleRecycler.visibility = View.VISIBLE
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

                progressBar.visibility = View.INVISIBLE
                swipeRefreshLayout.isRefreshing = false


                var exist: Cursor =
                    db.rawQuery(
                        "SELECT COUNT(*) as cnt FROM ${DBContract.SubgroupSettings.TABLE_NAME} " +
                                "WHERE ${DBContract.SubgroupSettings.TABLE_NAME}.${DBContract.SubgroupSettings.groupID} = ${StudentData.curGroupID}",
                        null
                    )
                exist.moveToFirst()

                var subgroup = 0

                if (exist.getIntOrNull(exist.getColumnIndexOrThrow("cnt")) != 0) {

                    exist.close()
                    exist =
                        db.rawQuery(
                            "SELECT * FROM ${DBContract.SubgroupSettings.TABLE_NAME} " +
                                    "WHERE ${DBContract.SubgroupSettings.TABLE_NAME}.${DBContract.SubgroupSettings.groupID} = ${StudentData.curGroupID}",
                            null
                        )
                    exist.moveToFirst()



                    subgroup =
                        exist.getIntOrNull(exist.getColumnIndexOrThrow(DBContract.SubgroupSettings.subGroup))!!
                    exist.close()
                }


                //filter(0)
                mRecyclerViewAdapter = ScheduleRecyclerAdapter(dataFilter)
                scheduleRecycler.adapter = mRecyclerViewAdapter
                scheduleRecycler.adapter!!.notifyDataSetChanged()


                when (subgroup) {
                    1 -> {
                        filter(1)
                    }

                    2 -> {
                        filter(2)
                    }
                }
                setHasOptionsMenu(true)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        StudentData.ScheduleList.clear()
        StudentData.listOfGroups.clear()
        dataFilter.clear()
        executors.shutdown()

            //  db.close()
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

            private val pairNameText: TextView = itemView.findViewById(R.id.pair_name_text)
            private val startTimeText: TextView = itemView.findViewById(R.id.start_time_text)
            private val endTimeText: TextView = itemView.findViewById(R.id.end_time_text)
            private val auditoryText: TextView = itemView.findViewById(R.id.auditory_text)
            private val subGroupNumber: TextView = itemView.findViewById(R.id.subgroup_text)
            private val subGroupImage: ImageView = itemView.findViewById(R.id.subgroup_image)
            private val dividerIndex: View = itemView.findViewById(R.id.divider)
            private val employeesText: TextView = itemView.findViewById(R.id.employees_text)
            private val noteText: TextView = itemView.findViewById(R.id.note_text)


            @SuppressLint("SetTextI18n")
            fun bind(pair: Lesson) {
                pairNameText.text = "${pair.subject} (${pair.lessonTypeAbbrev})"
                startTimeText.text = pair.startLessonTime
                endTimeText.text = pair.endLessonTime
                try {
                    if (pair.auditories.isEmpty())
                        auditoryText.text = ""
                    else
                        auditoryText.text = pair.auditories
                } catch (e: Exception) {
                }
                if (pair.numSubgroup != 0) {
                    subGroupNumber.visibility = View.VISIBLE
                    subGroupImage.visibility = View.VISIBLE
                    subGroupNumber.text = pair.numSubgroup.toString()
                } else {
                    subGroupNumber.visibility = View.INVISIBLE
                    subGroupImage.visibility = View.INVISIBLE
                }
                when (pair.lessonTypeAbbrev) {
                    "ПЗ" -> dividerIndex.foreground = ResourcesCompat.getDrawable(
                        itemView.resources,
                        R.drawable.divder_practical,
                        null
                    )

                    "ЛК" -> dividerIndex.foreground = ResourcesCompat.getDrawable(
                        itemView.resources,
                        R.drawable.divder_lectures,
                        null
                    )

                    "ЛР" -> dividerIndex.foreground =
                        ResourcesCompat.getDrawable(
                            itemView.resources,
                            R.drawable.divder_labs,
                            null
                        )
                }

                try {
                    var emp: String = ""
                    for (i in 0 until pair.employees.size) {
                        if (i != 0)
                            emp += "\n"
                        emp +=
                            "${pair.employees[i].lastName} " +
                                    if(pair.employees[i].firstName.isNullOrBlank()) "" else "${pair.employees[i].firstName!!.substring(0, 1)}. " +
                                            if(pair.employees[i].firstName.isNullOrBlank()) "" else "${pair.employees[i].firstName!!.substring(0, 1)}. "

                    }
                    employeesText.text = emp
                } catch (e: Exception) {
                    employeesText.text = ""
                }

                try {
                    if (pair.note == "" || pair.note == null)
                        noteText.visibility = View.GONE
                    else {
                        noteText.visibility = View.VISIBLE
                        noteText.text = pair.note
                    }
                } catch (e: Exception) {
                    noteText.visibility = View.GONE
                }
            }

            override fun onClick(p0: View?) {
                val args = LessonInfDialog.getBundle(
                    pairs[adapterPosition].employees[0].photo,
                    startTimeText.text.toString(),
                    endTimeText.text.toString(),
                    pairs[adapterPosition].auditories,

                    pairs[adapterPosition].employees[0].lastName.toString() + ' ' +
                            pairs[adapterPosition].employees[0].firstName.toString() + ' ' +
                            pairs[adapterPosition].employees[0].middleName.toString(),

                    pairs[adapterPosition].subjectFullName.toString() + '(' + pairs[adapterPosition].lessonTypeAbbrev + ')',
                    pairs[adapterPosition].note
                )
                val navController = findNavController()
                try {
                    navController.navigate(R.id.action_scheduleFragment_to_lessonInfDialog, args)
                } catch (e: IllegalArgumentException) {
                    Log.d("LessInf", "ScheduleFragment")

                }
            }


        }

        inner class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val dayNumberText: TextView = itemView.findViewById(R.id.day_number_text)
            fun bind(pair: Lesson) {
                dayNumberText.text = pair.note
            }
        }

    }
}