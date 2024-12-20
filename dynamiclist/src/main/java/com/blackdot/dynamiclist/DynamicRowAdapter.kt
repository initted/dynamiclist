package com.blackdot.dynamiclist

import android.view.View

/**
 * @author Akhil Mohan
 * @createdOn 17-March-2024
 */
abstract class DynamicRowAdapter {

    abstract fun createView(parentView: View?): View
    abstract fun bindView(customView: View?, position: Int)
    abstract fun itemCount():Int
}