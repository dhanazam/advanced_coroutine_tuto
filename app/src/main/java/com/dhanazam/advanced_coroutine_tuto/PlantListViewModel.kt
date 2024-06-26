package com.dhanazam.advanced_coroutine_tuto

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import com.dhanazam.advanced_coroutine_tuto.GrowZone
import com.dhanazam.advanced_coroutine_tuto.NoGrowZone
import com.dhanazam.advanced_coroutine_tuto.Plant
import com.dhanazam.advanced_coroutine_tuto.PlantRepository
import kotlinx.coroutines.Job
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class PlantListViewModel internal constructor(
    private val plantRepository: PlantRepository
): ViewModel() {

    private val _snackBar = MutableLiveData<String?>()

    val snackbar: LiveData<String?>
        get() = _snackBar

    private val _spinner = MutableLiveData<Boolean>(false)

    val spinner: LiveData<Boolean>
        get() = _spinner

    private val growZone = MutableLiveData<GrowZone>(NoGrowZone)

    val plants: LiveData<List<Plant>> = growZone.switchMap { growZone ->
        Log.d("growZone", growZone.toString() + " " + NoGrowZone.toString())
        if (growZone == NoGrowZone) {
            plantRepository.plants
        } else {
            plantRepository.getPlantsWithGrowZone(growZone)
        }
    }

    val plantsUsingFlow: LiveData<List<Plant>> = plantRepository.plantsFlow.asLiveData()

    init {
        clearGrowZoneNumber()

        // fetch the full plant list
        launchDataLoad { plantRepository.tryUpdateRecentPlantsCache() }
    }

    fun setGrowZoneNumber(num: Int) {
        growZone.value = GrowZone(num)

        launchDataLoad { plantRepository.tryUpdateRecentPlantsCache() }
    }

    fun clearGrowZoneNumber() {
        growZone.value = NoGrowZone

        launchDataLoad { plantRepository.tryUpdateRecentPlantsCache() }
    }

    fun isFiltered() = growZone.value != NoGrowZone

    fun onSnackBarShown() {
        _snackBar.value = null
    }

    private fun launchDataLoad(block: suspend () -> Unit): Job {
        return viewModelScope.launch {
            try {
                _spinner.value = true
                block()
            } catch (error: Throwable) {
                _snackBar.value = error.message
            } finally {
                _spinner.value = false
            }
        }
    }
}