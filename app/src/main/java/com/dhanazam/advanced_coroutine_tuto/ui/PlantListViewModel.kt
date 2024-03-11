package com.dhanazam.advanced_coroutine_tuto.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dhanazam.advanced_coroutine_tuto.GrowZone
import com.dhanazam.advanced_coroutine_tuto.NoGrowZone
import com.dhanazam.advanced_coroutine_tuto.PlantRepository

class PlantListViewModel internal constructor(
    private val plantRepository: PlantRepository
): ViewModel() {

    private val _snackbar = MutableLiveData<String?>()

    val snackbar: LiveData<String?>
        get() = _snackbar

    private val _spinner = MutableLiveData<Boolean>(false)

    val spinner: LiveData<Boolean>
        get() = _spinner

    private val growZone = MutableLiveData<GrowZone>(NoGrowZone)


}