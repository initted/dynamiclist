package com.blackdot.dynamiclist

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout

/**
 * @author Akhil Mohan
 * @createdOn 15-March-2024
 */
/** this can be used to populate a list of string is a staggered way so that as the more item comes we can make it to the next row
 *
 */
class DynamicRowList @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private var isFocusRequested: Boolean = false
    private var mAdapter: DynamicRowAdapter? = null
    private var viewHash: HashMap<Int, View?>? = null
    private var rowHash: HashMap<Int, LinearLayout>? = null
    private var currentRowIndex = 0
    private var currentRow: LinearLayout? = null
    private var rowSpaceFull = false
    private var dataCreationStarted = false


    private fun createAndUpdateView(parentWidth: Int) {
        this.orientation = VERTICAL
        this.gravity = Gravity.START

        if (parentWidth == 0 || mAdapter == null) {
            return
        }

        if (currentRow == null) {
            createRow()
        }

        var totalWidth = 0

        for (i in 0 until mAdapter!!.itemCount()) {
            val customView = mAdapter?.createView(currentRow)
            if (customView != null) {
                setupCustomView(customView, i)
                totalWidth += customView.measuredWidth
                viewHash?.set(i, customView)

                val remainingWidth = parentWidth - totalWidth
                if (remainingWidth > customView.measuredWidth) {
                    rowSpaceFull = false
                    currentRow?.addView(customView)
                } else {
                    rowSpaceFull = true
                    totalWidth = 0
                }

                if (rowSpaceFull) {
                    currentRowIndex += 1
                    createRow()
                }
            }
        }
    }

    private fun setupCustomView(customView: View, index: Int) {
        mAdapter?.bindView(customView, index)
        customView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
        customView.id = index
        customView.setOnKeyListener { view, _, keyEvent ->
            handleKeyEvents(view, keyEvent)
        }
        if (index == 0 && isFocusRequested) {
            customView.requestFocus()
        }
    }

    private fun handleKeyEvents(view: View, keyEvent: KeyEvent): Boolean {
        return when {
            view.id == mAdapter!!.itemCount() - 1 && keyEvent.keyCode == KeyEvent.KEYCODE_DPAD_RIGHT -> true
            view.id == 0 && keyEvent.keyCode == KeyEvent.KEYCODE_DPAD_LEFT -> true
            else -> false
        }
    }


    private fun createRow(): LinearLayout {
        val row = LinearLayout(context)
        val layoutParams = LayoutParams(
            MATCH_PARENT, // Specify width
            WRAP_CONTENT  // Specify height
        )
        layoutParams.gravity = Gravity.START
        row.rootView?.layoutParams = layoutParams
        row.orientation = HORIZONTAL
        row.gravity = Gravity.START
        this.addView(row)
        currentRow = row
        rowHash?.set(currentRowIndex, row)
        return row
    }

    fun requestChildView() {
        isFocusRequested = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)

        var parentWidth = 0
        if (widthMode == MeasureSpec.EXACTLY) {
            parentWidth = widthSize + 50
        }
        if (parentWidth != 0 && !dataCreationStarted) {
            dataCreationStarted = true
            createAndUpdateView(parentWidth)
        }

    }

    fun setAdapter(dynamicAdapter: DynamicRowAdapter) {
        mAdapter = dynamicAdapter
    }


}