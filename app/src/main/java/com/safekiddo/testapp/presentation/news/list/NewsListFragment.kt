package com.safekiddo.testapp.presentation.news.list

import android.os.Bundle
import android.view.View
import com.safekiddo.testapp.R
import com.safekiddo.testapp.di.Di
import com.safekiddo.testapp.presentation.BaseFragment
import kotlinx.android.synthetic.main.fragment_news_list.*
import javax.inject.Inject

class NewsListFragment : BaseFragment(R.layout.fragment_news_list) {

    @Inject
    lateinit var viewModel: NewsListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        Di.applicationComponent
                .getNewsListComponentFactory()
                .create(this)
                .inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
        setListeners()
    }

    private fun setListeners() {
        fragment_news_list_create_news_button.setOnClickListener {
            NewsListFragmentDirections.actionNewsListFragmentToModifyNewsFragment().navigate()
        }
    }

    private fun setObservers() {
        viewModel.newsList.observe(viewLifecycleOwner) {
            it
        }
    }
}