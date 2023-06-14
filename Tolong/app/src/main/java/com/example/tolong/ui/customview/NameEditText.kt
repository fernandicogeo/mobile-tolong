package com.example.tolong.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.tolong.R

class NameEditText : AppCompatEditText {

    private lateinit var announcementIcon: Drawable

    constructor(context: Context) : super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        hint = ""
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    private fun showIcon() {
        setError("Nama minimal 3 karakter", announcementIcon)
        setButtonDrawables(endOfTheText = announcementIcon)
    }

    private fun hideIcon() {
        setButtonDrawables()
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null)
    {
        setCompoundDrawablesWithIntrinsicBounds(startOfTheText, topOfTheText, endOfTheText, bottomOfTheText)
    }

    private fun init() {
        announcementIcon = ContextCompat.getDrawable(context, R.drawable.baseline_announcement_24) as Drawable

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence, start: Int, before: Int, count: Int) {
                if (p0.toString().length in 1..2) showIcon() else hideIcon()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

}