package com.safekiddo.testapp.presentation.news.details

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.textfield.TextInputEditText
import com.safekiddo.testapp.R
import com.safekiddo.testapp.data.model.ImageSource
import com.safekiddo.testapp.di.Di
import com.safekiddo.testapp.functional.util.hideKeyboard
import com.safekiddo.testapp.functional.util.shortToast
import com.safekiddo.testapp.functional.util.textOrBlank
import com.safekiddo.testapp.presentation.BaseFragment
import com.safekiddo.testapp.presentation.news.list.NewsListFragment
import kotlinx.android.synthetic.main.fragment_news_details.*
import kotlinx.android.synthetic.main.layout_edit_news.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject


class NewsDetailsFragment : BaseFragment(contentLayoutId = R.layout.fragment_news_details, menuResId = R.menu.menu_news_details) {

    @Inject
    lateinit var viewModel: NewsDetailsViewModel
    private val args by navArgs<NewsDetailsFragmentArgs>()
    private var menu: Menu? = null

    private var imageSource: ImageSource? = null
    private var title: String? = null
        get() = field?.takeIf(String::isNotBlank)
    private var description: String? = null
        get() = field?.takeIf(String::isNotBlank)

    override fun onCreate(savedInstanceState: Bundle?) {
        Di.applicationComponent
                .getNewsDetailsComponentFactory()
                .create(args.news, this)
                .inject(this)
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)

        imageSource = savedInstanceState?.getParcelable(ARG_PHOTO_SOURCE) ?: args.news?.imageSource
        title = savedInstanceState?.getString(ARG_TITLE) ?: args.news?.title
        description = savedInstanceState?.getString(ARG_DESCRIPTION) ?: args.news?.description
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(ARG_PHOTO_SOURCE, imageSource)
        outState.putString(ARG_TITLE, layout_edit_news_title_edit_text.textOrBlank())
        outState.putString(ARG_DESCRIPTION, layout_edit_news_description_edit_text.textOrBlank())
        super.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViews()
        setObservers()
    }

    private fun setViews() {
        // Listen for title characters count
        updateTitleCharactersCount(title)
        layout_edit_news_title_edit_text.doAfterTextChanged { updateTitleCharactersCount(it?.toString()) }
    }

    private fun updateTitleCharactersCount(title: String?) {
        viewModel.updateTitleCharactersCount(title)
    }

    private fun setObservers() {
        viewModel.viewMode.observe(viewLifecycleOwner) { viewMode ->
            val news = viewMode.newsDetails
            setUi(viewMode)
            //Set image
            setImage(imageSource)
            news ?: return@observe
//            fragment_news_details_image_image_view.transitionName = NewsListFragment.SharedElements.getNewsImageTransitionName(news.newsId)

            val title = this.title ?: news.title
            val description = this.description ?: news.description

            fragment_news_details_title_text_view.apply {
                transitionName = NewsListFragment.SharedElements.getNewsTitleTransitionName(news.newsId)
                text = title
            }

            fragment_news_details_description_text_view.apply {
                transitionName = NewsListFragment.SharedElements.getNewsDescriptionTransitionName(news.newsId)
                text = description
            }

            // Editable fields
            layout_edit_news_title_edit_text.apply {
                setText(title)
                setSelection(title.length)
            }
            layout_edit_news_description_edit_text.apply {
                setText(description)
                setSelection(description.length)
            }
        }

        viewModel.titleCharactersCount.observe(viewLifecycleOwner) {
            fragment_news_details_title_characters_count_text_view.text = getString(R.string.format_title_characters_count, it)
        }

        viewModel.event.observe(viewLifecycleOwner) {
            when (it) {
                NewsDetailsViewModel.Event.NewsDeleted -> {
                    requireContext().shortToast(R.string.message_deleted)
                    back()
                }
                NewsDetailsViewModel.Event.Error -> requireContext().shortToast(R.string.message_error)
                NewsDetailsViewModel.Event.NewsSaved -> requireContext().shortToast(R.string.message_news_saved)
            }
        }
    }

    private fun setUi(viewMode: NewsDetailsViewModel.ViewMode) {
        val isInEditMode = viewMode is NewsDetailsViewModel.ViewMode.Edit
        fragment_news_details_text_preview_group.isVisible = !isInEditMode
        fragment_news_details_edit_layout.isVisible = isInEditMode
        setMenuItemsVisibility(
                menu = menu,
                canDeleteNews = viewMode.newsDetails != null,
                isInEditMode = isInEditMode
        )
    }

    private fun setMenuItemsVisibility(menu: Menu?, canDeleteNews: Boolean, isInEditMode: Boolean) {
        menu?.setGroupVisible(R.id.menu_news_details_edit_group, isInEditMode)
        menu?.findItem(R.id.menu_news_details_edit)?.isVisible = !isInEditMode
        menu?.findItem(R.id.menu_news_details_delete)?.isVisible = canDeleteNews
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        this.menu = menu
        setMenuItemsVisibility(
                menu = menu,
                canDeleteNews = args.news != null,
                isInEditMode = viewModel.isInEditMode
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_news_details_edit -> viewModel.editNews()
            R.id.menu_news_details_apply_changes -> saveNews()
            R.id.menu_news_details_delete -> deleteNews()
            R.id.menu_news_details_change_photo -> NewsDetailsFragmentDirections.actionNewsDetailsFragmentToChangeNewsPhotoDialog().navigate()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveNews() {
        hideKeyboard()

        val title = layout_edit_news_title_edit_text.textOrBlank().also { this.title = it }
        val description = layout_edit_news_description_edit_text.textOrBlank().also { this.description = it }

        viewModel.saveNews(
                imageSource = imageSource,
                title = title,
                description = description
        )
    }

    private fun deleteNews() {
        AlertDialog.Builder(requireContext())
                .setTitle(R.string.title_warning)
                .setMessage(R.string.message_delete_news)
                .setPositiveButton(R.string.delete) { dialog, _ ->
                    dialog.dismiss()
                    viewModel.deleteNews()
                }
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
    }

    @Subscribe(sticky = true)
    fun onPickPhotoEvent(event: ChangeNewsPhotoDialog.Event) {
        EventBus.getDefault().removeStickyEvent(event)
        when (event) {
            ChangeNewsPhotoDialog.Event.PickFromGallery -> pickPhotoFromGallery()
            ChangeNewsPhotoDialog.Event.Delete -> setImage(null)
        }
    }

    private fun pickPhotoFromGallery() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        startActivityForResult(intent, PICK_GALLERY_PHOTO_REQUEST_CODE)
    }

    private fun setImage(imageSource: ImageSource?) {
        this.imageSource = imageSource
        Glide.with(this@NewsDetailsFragment)
                .load(imageSource?.getImage())
                .error(R.drawable.ic_broken_image_placeholder)
                .placeholder(R.drawable.ic_image_placeholder)
                .into(fragment_news_details_image_image_view)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            PICK_GALLERY_PHOTO_REQUEST_CODE -> {
                setImage(ImageSource.Factory.create(data?.data))
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    companion object {
        private const val ARG_PHOTO_SOURCE = "ARG_PHOTO_SOURCE"
        private const val ARG_TITLE = "ARG_TITLE"
        private const val ARG_DESCRIPTION = "ARG_DESCRIPTION"

        private const val PICK_GALLERY_PHOTO_REQUEST_CODE = 1
    }
}