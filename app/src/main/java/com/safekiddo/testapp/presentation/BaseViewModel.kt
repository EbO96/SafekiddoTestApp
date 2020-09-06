package com.safekiddo.testapp.presentation

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseViewModel : ViewModel() {

    private val compositeDisposable by lazy { CompositeDisposable() }

    fun Disposable.addToDisposables(): Disposable = this.also(compositeDisposable::add)

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}