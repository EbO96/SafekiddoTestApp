package com.safekiddo.testapp.presentation.news.list

import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.navigation.fragment.FragmentNavigator
import com.safekiddo.testapp.R
import com.safekiddo.testapp.di.Di
import com.safekiddo.testapp.functional.util.showShortToast
import com.safekiddo.testapp.presentation.BaseFragment
import com.safekiddo.testapp.presentation.view.JustSpaceItemDivider
import kotlinx.android.synthetic.main.fragment_news_list.*
import javax.inject.Inject


class NewsListFragment : BaseFragment(contentLayoutId = R.layout.fragment_news_list, menuResId = R.menu.menu_news_list), NewsListRecyclerAdapter.Listener {

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

            // This is workaround of problem witch animation between fragments when using "Shared Element Transitions"
            // and "RecyclerView"
            postponeEnterTransition()
            viewTreeObserver.addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
        }
    }

    private fun setListeners() {
        fragment_news_list_create_news_button.setOnClickListener {
            navigateToNewsDetails(null)
        }
    }

    override fun onItemClick(item: NewsItem, transitionExtras: Map<View, String>) {
        navigateToNewsDetails(item, transitionExtras)
    }

    private fun navigateToNewsDetails(news: NewsItem? = null, transitionExtras: Map<View, String>? = null) {
        val extras = transitionExtras?.let {
            FragmentNavigator.Extras.Builder()
                    .addSharedElements(it)
                    .build()
        }
        NewsListFragmentDirections.actionNewsListFragmentToNewsDetailsFragment(news).navigate(extras)
    }

    private fun setObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner, fragment_news_list_swipe_refresh_layout::setRefreshing)

        viewModel.event.observe(viewLifecycleOwner) {
            showShortToast(R.string.message_cannot_fetch_news)
        }

        viewModel.newsList.observe(viewLifecycleOwner) {
            fragment_news_list_empty_list_view.isVisible = it.isEmpty()
            newsListRecyclerAdapter.submitList(it)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_main_switch_theme -> switchTheme()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun switchTheme() {
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES ->
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            Configuration.UI_MODE_NIGHT_NO ->
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    object SharedElements {
        fun getNewsImageTransitionName(newsId: Long?) = newsId?.let { "newsImage${it}" } ?: ""

        fun getNewsTitleTransitionName(newsId: Long?) = newsId?.let { "newsTitle${it}" } ?: ""
    }
}