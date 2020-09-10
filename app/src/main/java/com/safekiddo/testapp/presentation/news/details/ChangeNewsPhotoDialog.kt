package com.safekiddo.testapp.presentation.news.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.safekiddo.testapp.R
import kotlinx.android.synthetic.main.dialog_change_news_photo.*
import org.greenrobot.eventbus.EventBus

class ChangeNewsPhotoDialog : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_change_news_photo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViews()
    }

    private fun setViews() {
        dialog_change_news_photo_from_gallery_button.setOnClickListener {
            postEvent(Event.PickFromGallery)
        }

        dialog_change_news_photo_delete_button.setOnClickListener {
            postEvent(Event.Delete)
        }
    }

    private fun postEvent(event: Event) {
        EventBus.getDefault().postSticky(event)
        findNavController().popBackStack()
    }

    sealed class Event {
        object PickFromGallery : Event()
        object Delete : Event()
    }
}