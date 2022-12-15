package com.maximshuhman.bsuirschedule.Views

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.maximshuhman.bsuirschedule.R


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

class AboutFragment : Fragment() {


    lateinit var aboutLayout: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_about, container, false)
        aboutLayout = v.findViewById(R.id.about_layout)

        aboutLayout.setOnClickListener {
            val builder = AlertDialog.Builder(requireActivity(), R.style.CustomAlertDialog)
            val dialogView: View =
                inflater.inflate(R.layout.dialog_about, container, false)
            builder.setView(dialogView)
            val alertDialog = builder.create()
            alertDialog.show()

        }


        return v
    }


}