package com.safekiddo.testapp.presentation.news.details

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.safekiddo.testapp.R
import com.safekiddo.testapp.di.Di
import com.safekiddo.testapp.presentation.BaseFragment
import kotlinx.android.synthetic.main.fragment_news_details.*
import javax.inject.Inject

class NewsDetailsFragment : BaseFragment(R.layout.fragment_news_details) {

    @Inject
    lateinit var viewModel: NewsDetailsViewModel
    private val args by navArgs<NewsDetailsFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        Di.applicationComponent.getNewsDetailsComponentFactory()
                .create(newsId = args.newsId, newsDetailsFragment = this)
                .inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
    }

    private fun setObservers() {
        viewModel.news.observe(viewLifecycleOwner) {
            it?.apply {
                Glide.with(this@NewsDetailsFragment)
                        .load(it.news.imageUrl)
                        .into(fragment_news_details_image_image_view)

                val titleCharactersCount = "(${it.titleCharactersCount})"
                val title = buildString {
                    append(it.news.title)
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

                fragment_news_details_title_text_view.text = spannableTitle
                fragment_news_details_description_text_view.text = it.news.description
            }
        }
    }
}