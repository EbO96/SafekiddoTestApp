package com.safekiddo.testapp.presentation.news.details

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.safekiddo.testapp.R
import com.safekiddo.testapp.di.Di
import com.safekiddo.testapp.presentation.BaseFragment
import com.safekiddo.testapp.presentation.news.list.NewsListFragment
import kotlinx.android.synthetic.main.fragment_news_details.*
import javax.inject.Inject

class NewsDetailsFragment : BaseFragment(contentLayoutId = R.layout.fragment_news_details, menuResId = R.menu.menu_news_details) {

    @Inject
    lateinit var newsDetailsViewModel: NewsDetailsViewModel
    private val args by navArgs<NewsDetailsFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        Di.applicationComponent
                .getNewsDetailsComponentFactory()
                .create(args.news.newsId, this)
                .inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViews()
    }

    private fun setViews() {
        // Set title with characters count
        val titleCharactersCount = "(${args.news.title.length})"
        val title = buildString {
            append(args.news.title)
            append(" ")
            append(titleCharactersCount)
        }

        val spannableTitle = SpannableStringBuilder(title).apply {
            val spanStart = title.indexOf(titleCharactersCount)
            setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.brand_mint_dark)),
                    spanStart,
                    spanStart + titleCharactersCount.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        fragment_news_details_title_text_view.transitionName = NewsListFragment.SharedElements.getNewsTitleTransitionName(args.news.newsId)
        fragment_news_details_title_text_view.text = spannableTitle

        fragment_news_details_description_text_view.transitionName = NewsListFragment.SharedElements.getNewsDescriptionTransitionName(args.news.newsId)
        // Set description
        fragment_news_details_description_text_view.text = args.news.description

        fragment_news_details_image_image_view.transitionName = NewsListFragment.SharedElements.getNewsImageTransitionName(args.news.newsId)
        // Set image
        Glide.with(this@NewsDetailsFragment)
                .load(args.news.imageUrl)
                .centerCrop()
                .into(fragment_news_details_image_image_view)
    }
}