package com.maximshuhman.bsuirschedule.Views

import Employees
import android.annotation.SuppressLint
import android.database.Cursor
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.maximshuhman.bsuirschedule.Data.StudentData
import com.maximshuhman.bsuirschedule.DataBase.DBContract
import com.maximshuhman.bsuirschedule.DataBase.DbHelper
import com.maximshuhman.bsuirschedule.DataClasses.Group
import com.maximshuhman.bsuirschedule.R
import java.util.concurrent.Executors


class FavoritesFragment : Fragment() {
    private lateinit var GroupsResyclerView: RecyclerView

    private lateinit var progressBar: ProgressBar
    private lateinit var toolbar: Toolbar
    private lateinit var favoritesAvailable: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorites, container, false)


        GroupsResyclerView = view.findViewById(R.id.list_of_favorites)
        progressBar = view.findViewById(R.id.progress_bar_groups_fav)
        favoritesAvailable = view.findViewById(R.id.favorites_available)

        GroupsResyclerView.layoutManager = LinearLayoutManager(requireContext())
        GroupsResyclerView.adapter = FavoritesRecyclerAdapter()


        setHasOptionsMenu(true)
        toolbar = view.findViewById(R.id.toolbar_favorites)
        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)

        //val prefs = defaultPreference(requireContext())
        val db = DbHelper(requireContext()).writableDatabase
        val settings = db.rawQuery(
            "SELECT ${DBContract.Settings.openedID}, ${DBContract.Settings.openedType} FROM ${DBContract.Settings.TABLE_NAME}",
            null
        )

        settings.moveToFirst()

        val opened =
            settings.getInt(settings.getColumnIndexOrThrow(DBContract.Settings.openedID))  //prefs.openedGroup
        val type =
            settings.getInt(settings.getColumnIndexOrThrow(DBContract.Settings.openedType))  //prefs.openedType
        settings.close()
        //val opened = prefs.openedGroup
        //val type = prefs.openedType
        if (opened != 0) {
            val navController = findNavController()

            val bundle = Bundle()

            val dbHelper = DbHelper(requireContext())

            val db = dbHelper.writableDatabase
            when (type) {
                0 -> {
                    val c: Cursor = db.rawQuery(
                        "SELECT * FROM ${DBContract.Groups.TABLE_NAME} " +
                                "WHERE ${DBContract.Groups.TABLE_NAME}.${DBContract.Groups.groupID} = $opened ",

                        null
                    )

                    c.moveToFirst()
                    with(c) {
                        bundle.putString(
                            "groupNumber",
                            getString(getColumnIndexOrThrow(DBContract.Groups.name))
                        )
                        bundle.putString(
                            "specialityAbbrev",
                            getString(getColumnIndexOrThrow(DBContract.Groups.specialityAbbrev))
                        )
                        bundle.putInt(
                            "course",
                            getInt(getColumnIndexOrThrow(DBContract.Groups.course))
                        )
                        bundle.putInt("id", opened)
                    }
                    //navController?.navigate(R.id.action_listOfdataFilterFragment_to_scheduleFragment)

                    //val action = ScheduleFragment().action
                    navController.navigate(R.id.scheduleFragment, bundle)
                }

                1 -> {
                    val c: Cursor = db.rawQuery(
                        "SELECT * FROM ${DBContract.Employees.TABLE_NAME} " +
                                "WHERE ${DBContract.Employees.TABLE_NAME}.${DBContract.Employees.employeeID} = $opened ",

                        null
                    )
                    c.moveToFirst()
                    with(c) {
                        bundle.putString(
                            "employeeName",
                            getString(getColumnIndexOrThrow(DBContract.Employees.urlId))
                        )
                        bundle.putString(
                            "FIO",
                            "${getString(getColumnIndexOrThrow(DBContract.Employees.lastName))} ${
                                getString(
                                    getColumnIndexOrThrow(DBContract.Employees.firstName)
                                )
                            } ${getString(getColumnIndexOrThrow(DBContract.Employees.middleName))}"

                        )
                        bundle.putInt(
                            "id",
                            getInt(getColumnIndexOrThrow(DBContract.Employees.employeeID))
                        )
                        bundle.putString(
                            "urlId",
                            getString(getColumnIndexOrThrow(DBContract.Employees.urlId))
                        )
                    }

                    navController.navigate(R.id.employeeSchedule, bundle)
                }
            }
        }
        updateUI()

        return view
    }


    private fun updateUI() {

        var grError: Int

        GroupsResyclerView.adapter = null


        Executors.newSingleThreadExecutor().execute {

            grError = StudentData.makeFavoritesList(requireContext())
            Handler(Looper.getMainLooper()).post {
                if (grError == 0) {

                    GroupsResyclerView.adapter = FavoritesRecyclerAdapter()
                    GroupsResyclerView.recycledViewPool.clear()
                    GroupsResyclerView.adapter!!.notifyDataSetChanged()
                    favoritesAvailable.visibility = View.GONE

                } else
                    favoritesAvailable.visibility = View.VISIBLE

                progressBar.visibility = View.INVISIBLE

            }
        }
    }

    private var dataFilter: MutableList<Pair<Group?, Employees?>> = StudentData.FavoritesList

    inner class FavoritesRecyclerAdapter :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {


        private val TYPE_HEADER_GROUP = 1
        private val TYPE_LIST = 0
        private val TYPE_EMPLOYEE = 2
        private val TYPE_HEADER_EMPLOYEE = 3

        override fun getItemViewType(position: Int): Int {

            if ((dataFilter[position].first?.type ?: 4) == TYPE_HEADER_GROUP) {
                return TYPE_HEADER_GROUP
            }
            if ((dataFilter[position].second?.type ?: 4) == TYPE_HEADER_EMPLOYEE) {
                return TYPE_HEADER_EMPLOYEE
            }

            if (dataFilter[position].first?.type ?: 4 == TYPE_LIST)
                return TYPE_LIST

            return TYPE_EMPLOYEE
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            when (viewType) {
                TYPE_HEADER_GROUP -> {
                    val header = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_speciality_name, parent, false)
                    return SpecialityViewHolder(header)
                }

                TYPE_HEADER_EMPLOYEE -> {
                    val header = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_speciality_name, parent, false)
                    return LastNameViewHolder(header)
                }

                TYPE_EMPLOYEE -> {
                    val header = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_group_view, parent, false)
                    return EmployeeViewHolder(header)
                }

                else -> {
                    val itemView =
                        LayoutInflater.from(parent.context)
                            .inflate(R.layout.item_group_view, parent, false)
                    //  itemView.setOnClickListener(myOn)
                    return GroupViewHolder(itemView)
                }
            }

        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            if ((dataFilter[position].first?.type ?: 4) == TYPE_HEADER_GROUP) {
                return (holder as SpecialityViewHolder).bind(dataFilter[position].first!!)
            }
            if ((dataFilter[position].second?.type ?: 4) == TYPE_HEADER_EMPLOYEE) {
                return (holder as LastNameViewHolder).bind(dataFilter[position].second!!)
            }

            if (dataFilter[position].first?.type ?: 4 == TYPE_LIST)
                return (holder as GroupViewHolder).bind(dataFilter[position].first!!)

            return (holder as EmployeeViewHolder).bind(dataFilter[position].second!!)


        }

        override fun getItemCount(): Int = dataFilter.size


        inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
            View.OnClickListener {

            init {
                itemView.setOnClickListener(this)
            }

            private val GroupNumber: TextView = itemView.findViewById(R.id.group_number_text)

            @SuppressLint("SetTextI18n")
            fun bind(group: Group) {
                if (group.name != "")
                    GroupNumber.text =
                        "${group.name}, ${group.facultyAbbrev}, ${group.specialityAbbrev}"
                else
                    GroupNumber.text = "Ошибка"
            }

            override fun onClick(p0: View?) {
                val navController = p0?.findNavController()

                val bundle = Bundle()

                bundle.putString("groupNumber", dataFilter[position].first!!.name.toString())
                bundle.putString(
                    "specialityAbbrev",
                    dataFilter[position].first!!.specialityAbbrev.toString()
                )
                bundle.putInt("course", dataFilter[position].first!!.course!!.toInt())
                bundle.putInt("id", dataFilter[position].first!!.id!!.toInt())

                //navController?.navigate(R.id.action_listOfdataFilterFragment_to_scheduleFragment)

                //val action = ScheduleFragment().action
                navController!!.navigate(R.id.scheduleFragment, bundle)
            }


        }

        inner class EmployeeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
            View.OnClickListener {

            init {
                itemView.setOnClickListener(this)
            }

            private val GroupNumber: TextView = itemView.findViewById(R.id.group_number_text)

            @SuppressLint("SetTextI18n")
            fun bind(emp: Employees) {
                if (emp.lastName != "")
                    GroupNumber.text =
                        "${emp.lastName} ${emp.firstName} ${emp.middleName}"
                else
                    GroupNumber.text = "Ошибка"
            }

            override fun onClick(p0: View?) {
                val navController = p0?.findNavController()

                val bundle = Bundle()

                bundle.putString(
                    "employeeName",
                    dataFilter[layoutPosition].second!!.urlId.toString()
                )
                bundle.putString(
                    "FIO",
                    GroupNumber.text.toString()
                )
                bundle.putInt("id", dataFilter[layoutPosition].second!!.id)
                bundle.putString("urlId", dataFilter[layoutPosition].second!!.urlId)

                //navController?.navigate(R.id.action_listOfdataFilterFragment_to_scheduleFragment)

                //val action = ScheduleFragment().action
                navController!!.navigate(R.id.employeeSchedule, bundle)
            }


        }

        inner class SpecialityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val SpecialityText: TextView = itemView.findViewById(R.id.speciality_name_text)
            fun bind(group: Group) {
                if (group.name != "")
                    SpecialityText.text = group.name
                else
                    SpecialityText.text = "Ошибка"
            }
        }

        inner class LastNameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val SpecialityText: TextView = itemView.findViewById(R.id.speciality_name_text)
            fun bind(group: Employees) {
                if (group.lastName != "")
                    SpecialityText.text = group.lastName
                else
                    SpecialityText.text = "Ошибка"
            }
        }

    }


}