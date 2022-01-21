package com.example.users.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.users.R
import com.example.users.data.model.Loading
import com.example.users.data.model.Error
import com.example.users.data.model.Success
import com.example.users.data.model.User
import com.example.users.databinding.UsersFragmentBinding
import com.example.users.util.LatLngInterpolator
import com.example.users.util.animateMarker
import com.example.users.util.getAddress
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class UsersFragment : Fragment(), OnMapReadyCallback {

    companion object {
        fun newInstance() = UsersFragment()
    }

    private lateinit var viewModel: UsersViewModel
    private var _binding: UsersFragmentBinding? = null
    private val binding get() = _binding!!

    private var googleMap: GoogleMap? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = UsersFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[UsersViewModel::class.java]
        initUi()
        fetchData()
        observeData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(mMap: GoogleMap) {
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        googleMap = mMap
    }

    private fun initUi() {
        setUpMap()
        binding.swipeRefreshLayout.setOnRefreshListener {
            lifecycleScope.launch{
                fetchData()
            }
        }
    }

    private fun setUpMap() {
        val supportMapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        supportMapFragment?.getMapAsync(this)
    }

    private fun setUpUiState(uiState: UIState) {
        binding.run {
            when(uiState) {
                UIState.LOADING -> {
                    infoLayout.visibility = View.VISIBLE
                    tvInfo.text = getText(R.string.loading)
                    swipeRefreshLayout.isRefreshing = true
                    tvInfo.setOnClickListener {}
                }
                UIState.SUCCESS -> {
                    infoLayout.visibility = View.INVISIBLE
                    swipeRefreshLayout.isRefreshing = false
                    tvInfo.setOnClickListener {}
                }
                UIState.FAILED -> {
                    infoLayout.visibility = View.VISIBLE
                    tvInfo.text = getText(R.string.retry)
                    swipeRefreshLayout.isRefreshing = false
                    tvInfo.setOnClickListener { fetchData() }
                }
            }
        }
    }

    private fun fetchData() {
        setUpUiState(UIState.LOADING)
        viewModel.getUsers()
    }

    private fun observeData() {
        lifecycleScope.launch {
            viewModel.userList.collect { state ->
                when(state) {
                    is Loading -> { setUpUiState(UIState.LOADING) }
                    is Error -> { setUpUiState(UIState.FAILED) }
                    is Success -> {
                        setUpUiState(UIState.SUCCESS)
                        googleMap?.let { displayUsers(requireContext(), it, state.users) }
                    }
                }
            }
        }
    }

    private fun displayUsers(context: Context, googleMap: GoogleMap, users: MutableList<User>) {
        users.forEach { addOrUpdateUserOnMap(context, googleMap, it) }
    }

    private fun addOrUpdateUserOnMap(context: Context, googleMap: GoogleMap, user: User) {
        if (user.previousPosition == null) {
            addUserOnMap(context, googleMap, user)
        } else {
            updateUserOnMap(user)
        }
    }

    private fun addUserOnMap(context: Context, googleMap: GoogleMap, user: User) {
        val latitude = user.currentPosition?.latitude ?: 0.0
        val longitude = user.currentPosition?.longitude ?: 0.0
        val marker = googleMap.addMarker(
            MarkerOptions()
                .position(LatLng(latitude, longitude))
                .title(user.name)
                .snippet("Address: ${getAddress(context, latitude,longitude)}")
        )
    }

    private fun updateUserOnMap(user: User) {
        val latitude = user.currentPosition?.latitude ?: 0.0
        val longitude = user.currentPosition?.longitude ?: 0.0
        animateMarker(user.marker, LatLng(latitude, longitude), LatLngInterpolator.LinearFixed())
    }

    enum class UIState {
        LOADING, SUCCESS, FAILED
    }
}