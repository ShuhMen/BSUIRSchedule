package com.maximshuhman.bsuirschedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment


class EmployeeLessonInf : DialogFragment() {
    companion object {

        const val START = "START"
        const val END = "END"
        const val AUDITORY = "AUDITORY"
        const val FIO = "FIO"
        const val NAME = "NAME"
        const val COMMENT = "COMMENT"
        const val TYPE = "TYPE"


        fun getBundle(
            start: String,
            end: String,
            auditory: String,
            fio: String,
            name: String,
            comment: String?
        ): Bundle {
            return Bundle().apply {
                putString(START, start)
                putString(END, end)
                putString(AUDITORY, auditory)
                putString(FIO, fio)
                putString(NAME, name)
                putString(COMMENT, comment)
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_employee_lesson_inf, container, true)

        if (dialog != null && dialog!!.window != null) {
            dialog!!.window!!.setBackgroundDrawableResource(R.drawable.rounded_back)
            dialog!!.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            // dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        }

        val fioText: TextView = view.findViewById(R.id.fio)
        val time: TextView = view.findViewById(R.id.time)
        val auditory: TextView = view.findViewById(R.id.auditory)
        val note: TextView = view.findViewById(R.id.note)
        val subjectName: TextView = view.findViewById(R.id.subject_name)

        arguments?.let {

            fioText.text =
                if (arguments?.getString(LessonInfDialog.FIO).toString() != "  ") arguments?.getString(
                    LessonInfDialog.FIO
                )
                    .toString() else "Информация отсутствует"



            time.text = arguments?.getString(LessonInfDialog.START) + " - " + arguments?.getString(
                LessonInfDialog.END
            )
            auditory.text = arguments?.getString(LessonInfDialog.AUDITORY)
            note.text = if (arguments?.getString(LessonInfDialog.COMMENT) != null) arguments?.getString(
                LessonInfDialog.COMMENT
            )
                .toString() else ""
            subjectName.text = arguments?.getString(LessonInfDialog.NAME)
        }

        // Inflate the layout for this fragment
        return view
    }

}