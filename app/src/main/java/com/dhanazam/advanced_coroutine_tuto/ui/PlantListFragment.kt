package com.dhanazam.advanced_coroutine_tuto.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dhanazam.advanced_coroutine_tuto.PlantListViewModel
import com.dhanazam.advanced_coroutine_tuto.PlantRepository
import com.dhanazam.advanced_coroutine_tuto.R
import com.dhanazam.advanced_coroutine_tuto.databinding.FragmentPlantListBinding
import com.dhanazam.advanced_coroutine_tuto.databinding.ListItemPlantBinding
import com.dhanazam.advanced_coroutine_tuto.utils.Injector
import com.google.android.material.snackbar.Snackbar

class PlantListFragment: Fragment() {

    private val viewModel: PlantListViewModel by viewModels {
        Injector.providePlantListViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPlantListBinding.inflate(inflater, container, false)
        context ?: return binding.root

        viewModel.spinner.observe(viewLifecycleOwner) { show ->
            binding.spinner.visibility = if (show) View.VISIBLE else View.GONE
        }

        viewModel.snackbar.observe(viewLifecycleOwner) { text ->
            text?.let {
                Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()
                viewModel.onSnackBarShown()
            }
        }

        val adapter = PlantAdapter()
        binding.plantList.adapter = adapter
        subscribeUi(adapter);

        (requireActivity() as MenuHost).addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_plant_list, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.filter_zone -> {
                        updateData()
                        true
                    }
                    else -> onOptionsItemSelected(menuItem)
                }
            }

        }, viewLifecycleOwner)

        return binding.root
    }

    private fun subscribeUi(adapter: PlantAdapter) {
        viewModel.plants.observe(viewLifecycleOwner) { plants ->
            adapter.submitList(plants)
        }
    }

   private fun updateData() {
       with(viewModel) {
           if (isFiltered()) {
               clearGrowZoneNumber()
           } else {
               setGrowZoneNumber(9)
           }
       }
   }
}

class PlantListViewModelFactory(
    private val repository: PlantRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHEKED_CAST")
    override fun <T: ViewModel> create(modelClass: Class<T>) = PlantListViewModel(repository) as T
}