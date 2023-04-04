package com.maximshuhman.bsuirschedule

import android.graphics.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment


class LessonInfDialog : DialogFragment() {

    companion object {

        const val PHOTO = "PHOTO"
        const val START = "START"
        const val END = "END"
        const val AUDITORY = "AUDITORY"
        const val FIO = "FIO"
        const val NAME = "NAME"
        const val COMMENT = "COMMENT"
        const val TYPE = "TYPE"


        fun getBundle(
            photo: ByteArray?,
            start: String,
            end: String,
            auditory: String,
            fio: String,
            name: String,
            comment: String?
        ): Bundle {
            return Bundle().apply {
                putByteArray(PHOTO, photo)
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
        val view: View = inflater.inflate(R.layout.lesson_inform_dialog, container, true)
        // Set transparent background and no title
        if (dialog != null && dialog!!.window != null) {
            dialog!!.window!!.setBackgroundDrawableResource(R.drawable.rounded_back)
            dialog!!.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            // dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        }

        val fioText: TextView = view.findViewById(R.id.fio)
        val image: ImageView = view.findViewById(R.id.employee_image)
        val time: TextView = view.findViewById(R.id.time)
        val auditory: TextView = view.findViewById(R.id.auditory)
        val note: TextView = view.findViewById(R.id.note)
        val subjectName: TextView = view.findViewById(R.id.subject_name)

        arguments?.let {

            fioText.text =
                if (arguments?.getString(FIO).toString() != "  ") arguments?.getString(FIO)
                    .toString() else "Информация отсутствует"

            if (arguments?.getByteArray(PHOTO) != null && arguments?.getByteArray(PHOTO)!!.isNotEmpty()) {
                val imageBytes = arguments?.getByteArray(PHOTO)

                var decodedImage =
                    BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes!!.size)

                val mutableBitmap: Bitmap = decodedImage.copy(Bitmap.Config.ARGB_8888, true)

                val borderSizePx = 1
                val cornerSizePx = 12

                val output = Bitmap.createBitmap(
                    mutableBitmap.width + borderSizePx * 2,
                    mutableBitmap.height + borderSizePx * 2, Bitmap.Config.ARGB_8888
                )

                val canvas = Canvas(output)


                val paint = Paint()
                val rect = Rect(0, 0, mutableBitmap.width, mutableBitmap.height)
                val rectF = RectF(rect)


                paint.isAntiAlias = true
                paint.color = -0x1
                paint.style = Paint.Style.FILL
                canvas.drawARGB(0, 0, 0, 0)
                canvas.drawRoundRect(rectF, cornerSizePx.toFloat(), cornerSizePx.toFloat(), paint)

                paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
                canvas.drawBitmap(mutableBitmap, rect, rect, paint)
                paint.color = resources.getColor(R.color.black, null)

                paint.style = Paint.Style.STROKE
                paint.strokeWidth = borderSizePx.toFloat()
                canvas.drawRoundRect(rectF, cornerSizePx.toFloat(), cornerSizePx.toFloat(), paint)

                image.setImageBitmap(output)
            } else {
                image.setImageResource(R.drawable.person_circle)
            }

            time.text = arguments?.getString(START) + " - " + arguments?.getString(END)
            auditory.text = arguments?.getString(AUDITORY)
            note.text = if (arguments?.getString(COMMENT) != null) arguments?.getString(COMMENT)
                .toString() else ""
            subjectName.text = arguments?.getString(NAME)
        }

        return view
    }

/*
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            val builder = AlertDialog.Builder(it, R.style.BaseBottomSheetDialog)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater

            val view = inflater.inflate(R.layout.lesson_inform_dialog, null)




            //fioText.text = "dsdsdsdsds"
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(view)

                // Add action buttons

            return builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

*/
}