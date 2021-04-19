package com.tarun.sos.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tarun.sos.R

open class DrawRoundedBottomSheet(
    var fragment: Fragment,
    var fragmentTag: String,
    var closeOnTouchOutside: Boolean = true
) : RoundedBottomSheet() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.layout_bottom_sheet, container, false)
        dialog?.setCanceledOnTouchOutside(closeOnTouchOutside)
        childFragmentManager.beginTransaction()
            .replace(R.id.bottom_sheet_root, fragment, fragmentTag).commitNow()
        return view

    }

}