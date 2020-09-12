package com.safekiddo.testapp.presentation.news.details

import android.content.Intent
import android.os.Bundle
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
import com.safekiddo.testapp.R
import com.safekiddo.testapp.data.model.ImageSource
import com.safekiddo.testapp.di.Di
import com.safekiddo.testapp.functional.util.hideKeyboard
import com.safekiddo.testapp.functional.util.showShortToast
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

    override fun onCreate(savedInstanceState: Bundle?) {
        Di.applicationComponent
                .getNewsDetailsComponentFactory()
                .create(args.news, this)
                .inject(this)
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViews()
        setObservers()
    }

    private fun setViews() {
        layout_edit_news_description_edit_text.doAfterTextChanged {
            viewModel.updateDescriptionCharactersCount(it?.toString())
        }
    }

    private fun setObservers() {
        viewModel.imageSource.observe(viewLifecycleOwner) {
            // fragment_news_details_image_image_view.transitionName = NewsListFragment.SharedElements.getNewsImageTransitionName(news.newsId)
            Glide.with(this@NewsDetailsFragment)
                    .load(it?.getImage())
                    .error(R.drawable.ic_broken_image_placeholder)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .into(fragment_news_details_image_image_view)
        }

        viewModel.title.observe(viewLifecycleOwner) {
            fragment_news_details_title_text_view.apply {
                transitionName = NewsListFragment.SharedElements.getNewsTitleTransitionName(args.news?.newsId)
                text = it
            }

            // Editable fields
            layout_edit_news_title_edit_text.apply {
                setText(it)
                setSelection(it?.length ?: 0)
            }
        }

        viewModel.description.observe(viewLifecycleOwner) {
            fragment_news_details_description_text_view.text = it

            layout_edit_news_description_edit_text.apply {
                setText(it)
                setSelection(it?.length ?: 0)
            }
        }

        viewModel.uiMode.observe(viewLifecycleOwner) { viewMode ->
            val isInEditMode = viewMode == NewsDetailsViewModel.UiMode.EDIT
            fragment_news_details_text_preview_group.isVisible = !isInEditMode
            fragment_news_details_edit_layout.isVisible = isInEditMode
            setMenuItemsVisibility(
                    menu = menu,
                    isInEditMode = isInEditMode
            )
        }

        viewModel.descriptionCharactersCount.observe(viewLifecycleOwner) {
            fragment_news_details_title_characters_count_text_view.text = getString(R.string.format_title_characters_count, it)
        }

        viewModel.event.observe(viewLifecycleOwner) {
            when (it) {
                NewsDetailsViewModel.Event.NewsDeleted -> {
                    showShortToast(R.string.message_deleted)
                    back()
                }
                NewsDetailsViewModel.Event.Error -> showShortToast(R.string.message_error)
                NewsDetailsViewModel.Event.NewsSaved -> showShortToast(R.string.message_news_saved)
            }
        }
    }

    private fun setMenuItemsVisibility(menu: Menu?, isInEditMode: Boolean) {
        menu?.apply {
            setGroupVisible(R.id.menu_news_details_edit_group, isInEditMode)
            findItem(R.id.menu_news_details_edit)?.isVisible = !isInEditMode
            findItem(R.id.menu_news_details_delete)?.isVisible = isInEditMode && viewModel.newsId != null
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        this.menu = menu
        setMenuItemsVisibility(
                menu = menu,
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

    private fun getTitle() = layout_edit_news_title_edit_text.textOrBlank()

    private fun getDescription() = layout_edit_news_description_edit_text.textOrBlank()

    private fun saveNews() {
        hideKeyboard()
        viewModel.saveNews(title = getTitle(), description = getDescription())
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
            ChangeNewsPhotoDialog.Event.Delete -> viewModel.updateImageSource(null)
        }
    }

    private fun pickPhotoFromGallery() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        startActivityForResult(intent, PICK_GALLERY_PHOTO_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            PICK_GALLERY_PHOTO_REQUEST_CODE -> viewModel.updateImageSource(ImageSource.Factory.create(data?.data
                    ?: return))
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onPause() {
        super.onPause()
        viewModel.saveUiState(title = getTitle(), description = getDescription())
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    companion object {
        private const val PICK_GALLERY_PHOTO_REQUEST_CODE = 1
    }
}