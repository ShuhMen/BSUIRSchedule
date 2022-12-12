package com.maximshuhman.bsuirschedule.Views

import android.annotation.SuppressLint
import android.database.Cursor
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.maximshuhman.bsuirschedule.Data.Data
import com.maximshuhman.bsuirschedule.DataBase.DBContract
import com.maximshuhman.bsuirschedule.DataBase.DbHelper
import com.maximshuhman.bsuirschedule.DataClasses.Group
import com.maximshuhman.bsuirschedule.PreferenceHelper.OPENED_GROUP
import com.maximshuhman.bsuirschedule.PreferenceHelper.customPreference
import com.maximshuhman.bsuirschedule.PreferenceHelper.defaultPreference
import com.maximshuhman.bsuirschedule.PreferenceHelper.openedGroup
import com.maximshuhman.bsuirschedule.R
import java.util.concurrent.Executors


class FavoritesFragment : Fragment() {
    private lateinit var GroupsResyclerView: RecyclerView

    private lateinit var progressBar: ProgressBar
    lateinit var searchView: SearchView
    private lateinit var toolbar: Toolbar
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var favoritesAvailable: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorites, container, false)


        GroupsResyclerView = view.findViewById(R.id.list_of_favorites)
        progressBar = view.findViewById(R.id.progress_bar_groups_fav)
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_favorites)
        favoritesAvailable = view.findViewById(R.id.favorites_available)
        //SearchView = view.findViewById(R.id.search_view)

        swipeRefreshLayout.setColorSchemeResources(R.color.BSUIR_blue)
        //  swipeRefreshLayout.setProgressBackgroundColorSchemeColor(R.color.night_icon_stroke)

        GroupsResyclerView.layoutManager = LinearLayoutManager(requireContext())
        GroupsResyclerView.adapter = FavoritesRecyclerAdapter()


        setHasOptionsMenu(true)
        toolbar = view.findViewById(R.id.toolbar_favorites)
        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)

        val prefs = defaultPreference(requireContext())

        val group =  prefs.openedGroup

        if(group != 0)
        {
            val navController = findNavController()

            val bundle = Bundle()

            val dbHelper = DbHelper(requireContext())

            val db = dbHelper.writableDatabase

            val c: Cursor = db.rawQuery(
                "SELECT * FROM ${DBContract.Groups.TABLE_NAME} " +
                        "WHERE ${DBContract.Groups.TABLE_NAME}.${DBContract.Groups.groupID} = $group ",

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
                bundle.putInt("course",  getInt(getColumnIndexOrThrow(DBContract.Groups.course)))
                bundle.putInt("id",  group)
            }
            //navController?.navigate(R.id.action_listOfdataFilterFragment_to_scheduleFragment)

            //val action = ScheduleFragment().action
            navController!!.navigate(R.id.scheduleFragment, bundle)
        }


        updateUI(0)

        swipeRefreshLayout.setOnRefreshListener {

            updateUI(1)

        }

        return view
    }


    private fun updateUI(mode: Int) {
        if (mode == 0) {
            progressBar.visibility = View.VISIBLE
            progressBar.isIndeterminate = true
        }
        var grError = 0

        GroupsResyclerView.adapter = null


        Executors.newSingleThreadExecutor().execute {

            grError = Data.makeFavoritesList(requireContext(), mode)
            Handler(Looper.getMainLooper()).post {
                if (grError == 0) {

                        GroupsResyclerView.adapter = FavoritesRecyclerAdapter()
                        GroupsResyclerView.recycledViewPool.clear()
                        GroupsResyclerView.adapter!!.notifyDataSetChanged()
                        favoritesAvailable.visibility = View.GONE

                }else
                favoritesAvailable.visibility = View.VISIBLE

                progressBar.visibility = View.INVISIBLE
                swipeRefreshLayout.isRefreshing = false

            }
        }
    }

    private var dataFilter: MutableList<Group> = Data.FavoritesList

    inner class FavoritesRecyclerAdapter :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {


        private val TYPE_HEADER: Int = 1
        private val TYPE_LIST: Int = 0

        override fun getItemViewType(position: Int): Int {

            if (dataFilter[position].type == TYPE_HEADER) {
                return TYPE_HEADER
            }
            return TYPE_LIST
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            if (viewType == TYPE_HEADER) {
                val header = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_speciality_name, parent, false)
                return SpecialityViewHolder(header)
            }
            val itemView =
                LayoutInflater.from(parent.context).inflate(R.layout.item_group_view, parent, false)
            //  itemView.setOnClickListener(myOn)
            return GroupViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (dataFilter[position].type) {
                TYPE_HEADER -> (holder as SpecialityViewHolder).bind(dataFilter[position])
                else ->
                    (holder as GroupViewHolder).bind(dataFilter[position])
            }
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

                bundle.putString("groupNumber", dataFilter[position].name.toString())
                bundle.putString(
                    "specialityAbbrev",
                    dataFilter[position].specialityAbbrev.toString()
                )
                bundle.putInt("course", dataFilter[position].course!!.toInt())
                bundle.putInt("id", dataFilter[position].id!!.toInt())

                //navController?.navigate(R.id.action_listOfdataFilterFragment_to_scheduleFragment)

                //val action = ScheduleFragment().action
                navController!!.navigate(R.id.scheduleFragment, bundle)
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


    }


}