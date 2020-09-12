package com.safekiddo.testapp.presentation.news.list

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.safekiddo.testapp.R
import kotlinx.android.synthetic.main.item_news.view.*

class NewsListRecyclerAdapter(private val listener: Listener) : ListAdapter<NewsItem, NewsListRecyclerAdapter.NewsViewHolder>(DIFFER) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(news: NewsItem) = itemView.apply {
            item_news_image_image_view.apply {
                // Image
                transitionName = NewsListFragment.SharedElements.getNewsImageTransitionName(news.newsId)
                Glide.with(this)
                        .load(news.imageSource?.getImage())
                        .placeholder(R.drawable.ic_image_placeholder)
                        .error(R.drawable.ic_broken_image_placeholder)
                        .dontAnimate()
                        .addListener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                                item_news_image_image_view.scaleType = ImageView.ScaleType.FIT_CENTER
                                return false
                            }

                            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                                item_news_image_image_view.scaleType = ImageView.ScaleType.CENTER_CROP
                                return false
                            }

                        })
                        .into(this)
            }

            // Title
            item_news_title_text_view.apply {
                transitionName = NewsListFragment.SharedElements.getNewsTitleTransitionName(news.newsId)
                isVisible = news.title.isNotBlank()
                text = news.title
            }

            // Description
            item_news_description_text_view.apply {
                transitionName = NewsListFragment.SharedElements.getNewsDescriptionTransitionName(news.newsId)
                isVisible = news.description.isNotBlank()
                text = news.description
            }

            setOnClickListener {
                val extras = mapOf<View, String>(
                        item_news_image_image_view to NewsListFragment.SharedElements.getNewsImageTransitionName(news.newsId),
                        item_news_title_text_view to NewsListFragment.SharedElements.getNewsTitleTransitionName(news.newsId),
                        item_news_description_text_view to NewsListFragment.SharedElements.getNewsDescriptionTransitionName(news.newsId)
                )
                listener.onItemClick(news, extras)
            }
        }
    }

    interface Listener {
        fun onItemClick(item: NewsItem, transitionExtras: Map<View, String>)
    }

    companion object {
        private val DIFFER = object : DiffUtil.ItemCallback<NewsItem>() {
            override fun areItemsTheSame(oldItem: NewsItem, newItem: NewsItem): Boolean {
                return oldItem.newsId == newItem.newsId
            }

            override fun areContentsTheSame(oldItem: NewsItem, newItem: NewsItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}