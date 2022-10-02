package com.maximshuhman.bsuirschedule

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.maximshuhman.bsuirschedule.DataClass.Group
import io.ktor.http.cio.*
import java.util.concurrent.Executors

class ListOfGroupsFragment : Fragment() {

    val bundle = Bundle()
    private fun getGroupsList() {

        Data.makeGroupsList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    private lateinit var GroupsResyclerView: RecyclerView

    lateinit var ProgressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_list_of_groups, container, false)

        GroupsResyclerView = view.findViewById(R.id.list_of_groups)
        ProgressBar = view.findViewById(R.id.progress_bar_groups)
        GroupsResyclerView.layoutManager = LinearLayoutManager(requireContext())
        GroupsResyclerView.adapter = GroupsRecyclerAdapter(Data.GroupsList)

        updateUI()

        return view
    }


    fun updateUI()
    {
        ProgressBar.visibility = View.VISIBLE
         ProgressBar.isIndeterminate = true

        GroupsResyclerView.adapter = null

        Executors.newSingleThreadExecutor().execute {

            getGroupsList()
            Handler(Looper.getMainLooper()).post {


                GroupsResyclerView.adapter = GroupsRecyclerAdapter(Data.GroupsList)
                GroupsResyclerView.recycledViewPool.clear()
                GroupsResyclerView.adapter!!.notifyDataSetChanged()

                 ProgressBar.visibility = View.INVISIBLE
            }
        }
    }

    inner class GroupsRecyclerAdapter(private val groups: MutableList<Group>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val TYPE_HEADER : Int = 1
        private val TYPE_LIST : Int = 0

        override fun getItemViewType(position: Int): Int {

            if(groups[position].type == TYPE_HEADER)
            {
                return TYPE_HEADER
            }
            return TYPE_LIST
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            if(viewType == TYPE_HEADER)
            {
                val header = LayoutInflater.from(parent.context).inflate(R.layout.item_speciality_name,parent,false)
                return SpecialityViewHolder(header)
            }
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_group_view, parent, false)
          //  itemView.setOnClickListener(myOn)
            return GroupViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (groups[position].type) {
                TYPE_HEADER -> (holder as SpecialityViewHolder).bind(groups[position])
                else ->
                    (holder as GroupViewHolder).bind(groups[position])}
        }

        override fun getItemCount(): Int = groups.size-1


        inner class GroupViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener{

            init {
                itemView.setOnClickListener(this)
            }

            val GroupNumber: TextView = itemView.findViewById(R.id.group_number_text)

            @SuppressLint("SetTextI18n")
            fun bind(group: Group) {
                if(group.name != "")
                    GroupNumber.text = "${group.name}, ${group.facultyAbbrev}, ${group.specialityAbbrev}"
                else
                    GroupNumber.text = "Ошибка"
            }

            override fun onClick(p0: View?) {
                val navController = p0?.findNavController()

                bundle.putString("groupNumber", groups[position].name.toString())
                bundle.putString("specialityAbbrev", groups[position].specialityAbbrev.toString())
                bundle.putInt("course", groups[position].course!!.toInt())

                //navController?.navigate(R.id.action_listOfGroupsFragment_to_scheduleFragment)

                //val action = ScheduleFragment().action
                findNavController().navigate(R.id.scheduleFragment, bundle)
            }


        }

        inner class SpecialityViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
            val SpecialityText: TextView = itemView.findViewById(R.id.speciality_name_text)
            fun bind(group: Group){
                if(group.name != "")
                    SpecialityText.text = group.name
                else
                    SpecialityText.text = "Ошибка"
            }
        }
}
}