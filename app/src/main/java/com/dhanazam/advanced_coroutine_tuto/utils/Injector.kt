package com.dhanazam.advanced_coroutine_tuto.utils

import android.content.Context
import com.dhanazam.advanced_coroutine_tuto.NetworkService
import com.dhanazam.advanced_coroutine_tuto.PlantRepository
import com.dhanazam.advanced_coroutine_tuto.ui.PlantListViewModelFactory

interface ViewModelFactoryProvider {
    fun providePlantListViewModelFactory(context: Context): PlantListViewModelFactory
}

val Injector: ViewModelFactoryProvider
    get() = currentInjector

private object DefaultViewModelProvider: ViewModelFactoryProvider {

    private fun getPlantRepository(context: Context): PlantRepository {
        return PlantRepository.getInstance(
            plantDao(context),
            plantService()
        )
    }

    private fun plantService() = NetworkService()

    private fun plantDao(context: Context) =
        AppDatabase.getInstance(context.applicationContext).plantDao()


    override fun providePlantListViewModelFactory(context: Context): PlantListViewModelFactory {
        val repository = getPlantRepository(context)
        return PlantListViewModelFactory(repository)
    }
}

@Volatile private var currentInjector: ViewModelFactoryProvider =
    DefaultViewModelProvider