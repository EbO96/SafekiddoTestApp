package com.safekiddo.testapp.presentation.news.list

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.FragmentNavigator
import com.safekiddo.testapp.R
import com.safekiddo.testapp.di.Di
import com.safekiddo.testapp.functional.util.shortToast
import com.safekiddo.testapp.presentation.BaseFragment
import com.safekiddo.testapp.presentation.view.JustSpaceItemDivider
import kotlinx.android.synthetic.main.fragment_news_list.*
import javax.inject.Inject

class NewsListFragment : BaseFragment(R.layout.fragment_news_list), NewsListRecyclerAdapter.Listener {

    @Inject
    lateinit var viewModel: NewsListViewModel

    private lateinit var newsListRecyclerAdapter: NewsListRecyclerAdapter

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

        newsListRecyclerAdapter = NewsListRecyclerAdapter(this)
        fragment_news_list_recycler_view.apply {
            adapter = newsListRecyclerAdapter
            addItemDecoration(JustSpaceItemDivider(requireContext(), R.dimen.padding_big))
            postponeEnterTransition()
            viewTreeObserver.addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
        }
    }

    private fun setListeners() {
        fragment_news_list_create_news_button.setOnClickListener {
            NewsListFragmentDirections.actionNewsListFragmentToNewsDetailsFragment(null).navigate()
        }
    }

    override fun onItemClick(item: NewsItem, transitionExtras: Map<View, String>) {
        navigateToNewsDetails(item, transitionExtras)
    }

    private fun navigateToNewsDetails(news: NewsItem, transitionExtras: Map<View, String>) {
        val extras = FragmentNavigator.Extras.Builder()
                .addSharedElements(transitionExtras)
                .build()

        NewsListFragmentDirections.actionNewsListFragmentToNewsDetailsFragment(news).navigate(extras)
    }

    private fun setObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner) {
            fragment_news_list_swipe_refresh_layout.isRefreshing = it
        }

        viewModel.event.observe(viewLifecycleOwner) {
            requireContext().shortToast(R.string.message_cannot_fetch_news)
        }

        viewModel.newsList.observe(viewLifecycleOwner) {
            fragment_news_list_empty_list_view.isVisible = it.isEmpty()
            newsListRecyclerAdapter.submitList(it)
        }
    }

    object SharedElements {
        fun getNewsImageTransitionName(newsId: Long?) = newsId?.let { "newsImage${it}" } ?: ""

        fun getNewsTitleTransitionName(newsId: Long?) = newsId?.let { "newsTitle${it}" } ?: ""

        fun getNewsDescriptionTransitionName(newsId: Long?) = newsId?.let { "newsDescription${it}" } ?: ""
    }
}