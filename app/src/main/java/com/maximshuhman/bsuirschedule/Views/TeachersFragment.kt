package com.maximshuhman.bsuirschedule.Views

import Employees
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
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.maximshuhman.bsuirschedule.Data.EmployeeData
import com.maximshuhman.bsuirschedule.R
import com.maximshuhman.bsuirschedule.RecyclerLinearManager
import java.util.concurrent.Executors

class TeachersFragment : Fragment() {

    private lateinit var employeesResyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var toolbar: Toolbar
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var floatingButton: FloatingActionButton

    private var dataFilter: MutableList<Employees> = EmployeeData.employeesList

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_teachers, container, false)

        findNavController().clearBackStack(R.id.scheduleFragment)

        employeesResyclerView = view.findViewById(R.id.list_of_employees)
        progressBar = view.findViewById(R.id.progress_bar_employees)
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_employees)
        //SearchView = view.findViewById(R.id.search_view)
        floatingButton = view.findViewById(R.id.upButtonEmployees)

        floatingButton.hide()

        swipeRefreshLayout.setColorSchemeResources(R.color.BSUIR_blue)
        //  swipeRefreshLayout.setProgressBackgroundColorSchemeColor(R.color.night_icon_stroke)

        employeesResyclerView.layoutManager = RecyclerLinearManager(requireContext(), 5f)
        employeesResyclerView.adapter = GroupsRecyclerAdapter()

        employeesResyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
            employeesResyclerView.smoothScrollToPosition(0)
        }

        setHasOptionsMenu(true)
        toolbar = view.findViewById(R.id.toolbar_employees)
        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)

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
                if (EmployeeData.employeesList.size != 0)
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
        employeesResyclerView.adapter = null

        var emError = 0
        var grError = 0


        Executors.newSingleThreadExecutor().execute {

            emError = EmployeeData.makeEmployeesList(requireContext(), mode)

            Handler(Looper.getMainLooper()).post {
                if (emError != 0 || grError != 0) {
                    Toast.makeText(context, "Ошибка получения данных", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    dataFilter = EmployeeData.employeesList
                    employeesResyclerView.adapter = GroupsRecyclerAdapter()
//                    employeesResyclerView.recycledViewPool.clear()
                    employeesResyclerView.adapter!!.notifyDataSetChanged()
                }
                progressBar.visibility = View.INVISIBLE
                swipeRefreshLayout.isRefreshing = false

            }
        }
    }


    inner class GroupsRecyclerAdapter :
        RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {


        private val TYPE_HEADER: Int = 1
        private val TYPE_EMPLOYEE: Int = 0

        override fun getItemViewType(position: Int): Int {
            if ((dataFilter[position].type) == TYPE_HEADER) {
                return TYPE_HEADER
            }

            return TYPE_EMPLOYEE
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            if (viewType == 1) {
                val header = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_speciality_name, parent, false)
                return LastNameViewHolder(header)
            }
            val itemView =
                LayoutInflater.from(parent.context).inflate(R.layout.item_group_view, parent, false)
            return EmployeeViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (dataFilter[position].type == 1) {
                (holder as LastNameViewHolder).bind(dataFilter[position])
            } else
                (holder as EmployeeViewHolder).bind(dataFilter[position])

        }

        override fun getItemCount(): Int = dataFilter.size


        inner class EmployeeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
            View.OnClickListener {

            init {
                itemView.setOnClickListener(this)
            }

            private val GroupNumber: TextView = itemView.findViewById(R.id.group_number_text)
            private val cardView: CardView = itemView.findViewById(R.id.group_card_view)

            @SuppressLint("SetTextI18n")
            fun bind(employee: Employees) {
                GroupNumber.text =
                    "${employee.lastName} ${employee.firstName} ${employee.middleName}"
            }

            override fun onClick(p0: View?) {
                val navController = p0?.findNavController()

                val bundle = Bundle()

                bundle.putString("employeeName", dataFilter[layoutPosition].urlId.toString())
                bundle.putString(
                    "FIO",
                    GroupNumber.text.toString()
                )
                bundle.putInt("id", dataFilter[layoutPosition].id)
                bundle.putString("urlId", dataFilter[layoutPosition].urlId)

                //navController?.navigate(R.id.action_listOfdataFilterFragment_to_scheduleFragment)

                //val action = ScheduleFragment().action

                dataFilter = EmployeeData.employeesList
                navController!!.navigate(R.id.employeeSchedule, bundle)
            }


        }

        override fun getFilter(): Filter {
            return object : Filter() {
                override fun performFiltering(constraint: CharSequence?): FilterResults {
                    val charSearch = constraint.toString()



                    dataFilter =
                        if (charSearch.isEmpty()) {
                            EmployeeData.employeesList
                        } else {
                            val resultList = mutableListOf<Employees>()
                            for (row in EmployeeData.employeesList) {
                                if (("${row.lastName} ${row.firstName} ${row.middleName}").contains(
                                        charSearch,
                                        true
                                    )
                                // ||row.name.toString() == charSearch
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
                        dataFilter = results?.values as MutableList<Employees>
                        employeesResyclerView.adapter!!.notifyDataSetChanged()
                    } catch (e: java.lang.NullPointerException) {
                        Firebase.crashlytics.log(results?.values.toString())

                    }
                }

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