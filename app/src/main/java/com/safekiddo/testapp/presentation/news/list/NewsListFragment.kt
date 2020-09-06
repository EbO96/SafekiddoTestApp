package com.safekiddo.testapp.presentation.news.list

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.safekiddo.testapp.R
import com.safekiddo.testapp.data.rest.RestApiResponse
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
        setViews()
        setObservers()
        setListeners()
    }

    private fun setViews() {
        fragment_news_list_swipe_refresh_layout.setOnRefreshListener(viewModel::refresh)
    }


    private fun setListeners() {
        fragment_news_list_create_news_button.setOnClickListener {
            NewsListFragmentDirections.actionNewsListFragmentToModifyNewsFragment().navigate()
        }
    }

    private fun setObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner) {
            fragment_news_list_swipe_refresh_layout.isRefreshing = it
        }

        viewModel.error.observe(viewLifecycleOwner, ::showError)

        viewModel.newsList.observe(viewLifecycleOwner) {
            it
        }
    }

    private fun showError(type: RestApiResponse.Error.ErrorType) {
        val message = when (type) {
            RestApiResponse.Error.ErrorType.Unknown -> R.string.message_cannot_fetch_news
            RestApiResponse.Error.ErrorType.NoInternetConnection -> R.string.message_no_internet
        }
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
}