package com.safekiddo.testapp.presentation.news.details

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.safekiddo.testapp.R
import com.safekiddo.testapp.di.Di
import com.safekiddo.testapp.functional.util.setReadOnly
import com.safekiddo.testapp.functional.util.textOrBlank
import com.safekiddo.testapp.presentation.BaseFragment
import com.safekiddo.testapp.presentation.news.list.NewsListFragment
import kotlinx.android.synthetic.main.fragment_news_details.*
import javax.inject.Inject

class NewsDetailsFragment : BaseFragment(contentLayoutId = R.layout.fragment_news_details, menuResId = R.menu.menu_news_details) {

    @Inject
    lateinit var viewModel: NewsDetailsViewModel
    private val args by navArgs<NewsDetailsFragmentArgs>()
    private var menu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        Di.applicationComponent
                .getNewsDetailsComponentFactory()
                .create(args.news, this)
                .inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViews()
        setObservers()
    }

    private fun setViews() {
        fragment_news_details_title_edit_text.doAfterTextChanged {
            viewModel.updateTitleCharactersCount(it?.toString())
        }
    }

    private fun setObservers() {
        viewModel.viewMode.observe(viewLifecycleOwner) { viewMode ->
            val newsDetails = viewMode.newsDetails
            setUi(viewMode)

            // Set image
            Glide.with(this@NewsDetailsFragment)
                    .load(newsDetails?.image)
                    .error(R.drawable.ic_broken_image_placeholder)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .centerCrop()
                    .into(fragment_news_details_image_image_view)

            newsDetails ?: return@observe

            fragment_news_details_title_edit_text.transitionName = NewsListFragment.SharedElements.getNewsTitleTransitionName(newsDetails.newsId)
            fragment_news_details_title_edit_text.setText(newsDetails.title)

            fragment_news_details_description_edit_text.transitionName = NewsListFragment.SharedElements.getNewsDescriptionTransitionName(newsDetails.newsId)
            // Set description
            fragment_news_details_description_edit_text.setText(newsDetails.description)

            fragment_news_details_image_image_view.transitionName = NewsListFragment.SharedElements.getNewsImageTransitionName(newsDetails.newsId)
        }

        viewModel.titleCharactersCount.observe(viewLifecycleOwner) {
            fragment_news_details_title_characters_count_text_view.text = getString(R.string.format_title_characters_count, it)
        }
    }

    private fun setUi(viewMode: NewsDetailsViewModel.ViewMode) {
        val isInEditMode = viewMode is NewsDetailsViewModel.ViewMode.Edit
        // Active EditText's
        fragment_news_details_title_edit_text.setReadOnly(!isInEditMode)
        fragment_news_details_description_edit_text.setReadOnly(!isInEditMode)
        setMenuItemsVisibility(menu, isInEditMode)
    }

    private fun setMenuItemsVisibility(menu: Menu?, isInEditMode: Boolean) {
        menu?.setGroupVisible(R.id.menu_news_details_edit_group, isInEditMode)
        menu?.findItem(R.id.menu_news_details_edit)?.isVisible = !isInEditMode
        menu?.findItem(R.id.menu_news_details_details_delete)?.isVisible = args.news != null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        this.menu = menu
        setMenuItemsVisibility(menu, viewModel.isInEditMode)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_news_details_edit -> viewModel.editNews()
            R.id.menu_news_details_apply_changes -> viewModel.saveNews(
                    imagePath = "",// TODO
                    title = fragment_news_details_title_edit_text.textOrBlank(),
                    description = fragment_news_details_description_edit_text.textOrBlank()
            )
        }
        return super.onOptionsItemSelected(item)
    }
}