package io.github.junrdev.hiddengems.presentation.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.junrdev.hiddengems.R
import io.github.junrdev.hiddengems.databinding.FragmentHomeScreenBinding
import io.github.junrdev.hiddengems.presentation.adapter.PlaceListAdapter
import io.github.junrdev.hiddengems.presentation.adapter.ServingListAdapter
import io.github.junrdev.hiddengems.presentation.ui.AppDatastore
import io.github.junrdev.hiddengems.presentation.ui.showToast
import io.github.junrdev.hiddengems.presentation.viewmodel.GemsViewModel
import io.github.junrdev.hiddengems.presentation.viewmodel.ServingsViewModel
import io.github.junrdev.hiddengems.util.Constant
import io.github.junrdev.hiddengems.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


@AndroidEntryPoint
class HomeScreen : Fragment() {

    lateinit var binding: FragmentHomeScreenBinding
    private val gemsViewModel by viewModels<GemsViewModel>()
    private val servingsViewModel by viewModels<ServingsViewModel>()

    @Inject
    lateinit var appDatastore: AppDatastore


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentHomeScreenBinding.inflate(inflater, container, false)
            .also { binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            goToProfile.setOnClickListener { findNavController().navigate(R.id.action_homeScreen_to_account2) }

            CoroutineScope(Dispatchers.Main).launch {

                gemsViewModel.gems.observe(viewLifecycleOwner) { gemsResource ->

                    when (gemsResource) {

                        is Resource.Error -> {
                            loadingTopPicks.stopShimmer()
                        }

                        is Resource.Loading -> {
                            loadingTopPicks.apply { startShimmer(); visibility = View.VISIBLE }
                            topPicks.visibility = View.GONE
                        }

                        is Resource.Success -> {
                            loadingTopPicks.stopShimmer()
                            val places = gemsResource.data
                            println("data $places")
                            places?.let {
                                if (places.isNotEmpty()) {
                                    loadingTopPicks.visibility = View.GONE
                                    topPicks.apply {
                                        visibility = View.VISIBLE

                                        adapter = PlaceListAdapter(requireContext(), places) {
                                            findNavController().navigate(
                                                R.id.action_homeScreen_to_viewGem,
                                                bundleOf(Constant.gem to it)
                                            )
                                        }
                                    }
                                }
                            }

                        }
                    }
                }

                servingsViewModel.servings.observe(viewLifecycleOwner) { servingsResource ->
                    when (servingsResource) {
                        is Resource.Error -> {
                            loadingServings.stopShimmer()
                        }

                        is Resource.Loading -> {
                            loadingServings.apply {
                                visibility = View.VISIBLE
                                startShimmer()
                            }
                            servings.visibility = View.GONE
                        }

                        is Resource.Success -> {
                            loadingServings.stopShimmer()

                            servingsResource.data?.let { servingList ->
                                if (servingList.isNotEmpty()){
                                    loadingServings.visibility = View.GONE
                                    servings.adapter = ServingListAdapter(servingList.toMutableList()) {
                                        findNavController().navigate(
                                            R.id.action_homeScreen_to_searchResults,
                                            bundleOf(Constant.serving to it)
                                        )
                                    }
                                    servings.visibility = View.VISIBLE
                                }
                            }
                        }
                    }
                }

                //refresh feature
                textView2.setOnClickListener {
                    gemsViewModel.getGems()
                    servingsViewModel.getServings()
                }
            }

            editTextText.apply {
                imeOptions = EditorInfo.IME_ACTION_SEARCH

                setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        findNavController().navigate(
                            R.id.action_homeScreen_to_searchResults,
                            bundleOf("name" to text.toString())
                        )
                        true
                    }
                    false
                }
            }


            addGem.setOnClickListener {
                CoroutineScope(Dispatchers.Main).launch {
                    if (appDatastore.isEmailVerified.first()) {
                        findNavController().navigate(R.id.action_homeScreen_to_addGem)
                    } else
                        requireContext().showToast("Ooops\uD83D\uDE1F\uD83D\uDE1F, verify email first.")
                }
            }


            textView2.setOnClickListener {
                findNavController().navigate(R.id.action_homeScreen_to_viewGem)
            }

            requireActivity().onBackPressedDispatcher.addCallback(
                viewLifecycleOwner,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        runBlocking {
                            requireActivity().finish()
                        }
                    }
                })

        }
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.accounttoolbarmenu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.account -> {
                findNavController().navigate(R.id.action_homeScreen_to_account2)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


}