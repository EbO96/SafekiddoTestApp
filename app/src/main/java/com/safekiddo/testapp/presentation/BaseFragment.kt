package com.safekiddo.testapp.presentation

import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.Navigator
import androidx.navigation.fragment.findNavController

abstract class BaseFragment(contentLayoutId: Int) : Fragment(contentLayoutId) {

    fun NavDirections.navigate(extras: Navigator.Extras? = null) {
        if (extras != null) {
            findNavController().navigate(this, extras)
        } else {
            findNavController().navigate(this)
        }
    }
}