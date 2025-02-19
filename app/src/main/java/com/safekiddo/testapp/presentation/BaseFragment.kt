package com.safekiddo.testapp.presentation

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.Navigator
import androidx.navigation.fragment.findNavController
import com.safekiddo.testapp.functional.util.hideKeyboard

abstract class BaseFragment(contentLayoutId: Int, menuResId: Int = 0) : Fragment(contentLayoutId) {

    private val menuResId = menuResId.takeIf { it != 0 }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(menuResId != null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findNavController().addOnDestinationChangedListener { _, _, _ -> hideKeyboard() }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menuResId?.apply { inflater.inflate(this, menu) }
        super.onCreateOptionsMenu(menu, inflater)
    }

    fun NavDirections.navigate(extras: Navigator.Extras? = null) {
        if (extras != null) {
            findNavController().navigate(this, extras)
        } else {
            findNavController().navigate(this)
        }
    }

    fun back() {
        findNavController().popBackStack()
    }
}