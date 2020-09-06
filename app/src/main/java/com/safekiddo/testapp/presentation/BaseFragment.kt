package com.safekiddo.testapp.presentation

import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController

abstract class BaseFragment(contentLayoutId: Int) : Fragment(contentLayoutId) {

    fun NavDirections.navigate() {
        findNavController().navigate(this)
    }
}