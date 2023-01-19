package com.maximshuhman.bsuirschedule.Views

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.maximshuhman.bsuirschedule.Data.Data
import com.maximshuhman.bsuirschedule.DataClasses.Group
import com.maximshuhman.bsuirschedule.R
import java.util.concurrent.Executors

class ListOfGroupsFragment : Fragment() {


    private lateinit var GroupsResyclerView: RecyclerView

    private lateinit var progressBar: ProgressBar
    lateinit var searchView: SearchView
    private lateinit var toolbar: Toolbar
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var floatingButton: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list_of_groups, container, false)

        findNavController().clearBackStack(R.id.scheduleFragment)

        GroupsResyclerView = view.findViewById(R.id.list_of_groups)
        progressBar = view.findViewById(R.id.progress_bar_groups)
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_groups)
        //SearchView = view.findViewById(R.id.search_view)
        floatingButton = view.findViewById(R.id.upButtonGroups)

        floatingButton.hide()

        swipeRefreshLayout.setColorSchemeResources(R.color.BSUIR_blue)
        //  swipeRefreshLayout.setProgressBackgroundColorSchemeColor(R.color.night_icon_stroke)

        GroupsResyclerView.layoutManager = LinearLayoutManager(requireContext())
        GroupsResyclerView.adapter = GroupsRecyclerAdapter()

        GroupsResyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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

        floatingButton.setOnClickListener{
            GroupsResyclerView.smoothScrollToPosition(0)
        }


        setHasOptionsMenu(true)
        toolbar = view.findViewById(R.id.toolbar_groups)
        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)

       /* val prefs = PreferenceHelper.defaultPreference(requireContext())

        val group = prefs.openedGroup

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
         }*/

        updateUI(0)

        swipeRefreshLayout.setOnRefreshListener {

            updateUI(1)

        }

        return view
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)

        val searchItem: MenuItem? = menu.findItem(R.id.action_search)
        //val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = searchItem?.actionView as SearchView

        searchView.maxWidth = Integer.MAX_VALUE

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (Data.GroupsList.size != 0)
                    GroupsRecyclerAdapter().filter.filter(newText)

                return false
            }
        })

    }

    private fun updateUI(mode: Int) {
        if (mode == 0) {
            progressBar.visibility = View.VISIBLE
            progressBar.isIndeterminate = true
        }
        GroupsResyclerView.adapter = null

        var emError = 0
        var grError = 0


        Executors.newSingleThreadExecutor().execute {

            emError = Data.makeEmployeesList(requireContext())

            grError = Data.makeGroupsList(requireContext(), mode)
            Handler(Looper.getMainLooper()).post {
                if (emError != 0 || grError != 0) {
                    Toast.makeText(requireContext(), "Ошибка получения данных", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    GroupsResyclerView.adapter = GroupsRecyclerAdapter()
                    GroupsResyclerView.recycledViewPool.clear()
                    GroupsResyclerView.adapter!!.notifyDataSetChanged()
                }
                progressBar.visibility = View.INVISIBLE
                swipeRefreshLayout.isRefreshing = false

            }
        }
    }

    private var dataFilter: MutableList<Group> = Data.GroupsList

    inner class GroupsRecyclerAdapter :
        RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {


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
            private val cardView: CardView = itemView.findViewById(R.id.group_card_view)

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

                dataFilter = Data.GroupsList
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

        override fun getFilter(): Filter {
            return object : Filter() {
                override fun performFiltering(constraint: CharSequence?): FilterResults {
                    val charSearch = constraint.toString()



                    dataFilter =
                        if (charSearch.isEmpty()) {
                            Data.GroupsList
                        } else {
                            val resultList = mutableListOf<Group>()
                            for (row in Data.GroupsList) {
                                if (row.name.toString().contains(
                                        charSearch,
                                        true
                                    ) || row.name.toString() == charSearch
                                ) {
                                    resultList.add(row)
                                }
                            }
                            resultList
                        }

                    val filterResults = FilterResults()
                    filterResults.values = dataFilter
                    return filterResults
                }

                override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                    try {
                        dataFilter = results?.values as MutableList<Group>
                        GroupsResyclerView.adapter!!.notifyDataSetChanged()
                    }catch (e:java.lang.NullPointerException){
                        Firebase.crashlytics.log(results?.values.toString())

                    }
                }

            }


        }
    }


}


