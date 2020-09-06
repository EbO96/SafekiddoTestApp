package com.safekiddo.testapp.presentation.news.list

import android.os.Bundle
import android.view.View
import com.safekiddo.testapp.R
import com.safekiddo.testapp.presentation.BaseFragment
import kotlinx.android.synthetic.main.fragment_news_list.*

class NewsListFragment : BaseFragment(R.layout.fragment_news_list) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }

    private fun setListeners() {
        fragment_news_list_create_news_button.setOnClickListener {
            NewsListFragmentDirections.actionNewsListFragmentToModifyNewsFragment().navigate()
        }
    }
}