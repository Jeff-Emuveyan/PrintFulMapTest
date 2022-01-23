package com.example.users.ui


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.users.R
import com.example.users.data.model.Error
import com.example.users.data.model.Loading
import com.example.users.data.model.Success
import com.example.users.data.model.User
import com.example.users.databinding.BubbleBinding
import com.example.users.databinding.UsersFragmentBinding
import com.example.users.util.LatLngInterpolator
import com.example.users.util.animateMarker
import com.example.users.util.getAddress
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UsersFragment : Fragment(), OnMapReadyCallback {

    companion object {
        fun newInstance() = UsersFragment()
    }

    private val viewModel: UsersViewModel by viewModels()
    private var _binding: UsersFragmentBinding? = null
    private val binding get() = _binding!!
    private val markers = HashMap<String?, User>()
    private var _bubbleViewBinding: BubbleBinding? = null
    val bubbleBinding get() = _bubbleViewBinding!!

    private var googleMap: GoogleMap? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = UsersFragmentBinding.inflate(inflater, container, false)
        _bubbleViewBinding = BubbleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initUi()
        observeData()
        fetchData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _bubbleViewBinding = null
    }

    override fun onMapReady(mMap: GoogleMap) {
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        googleMap = mMap
        googleMap?.setInfoWindowAdapter(CustomInfoWindowAdapter())
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
            viewModel.dataFetchState.collect { state ->
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
        markers[marker?.id] = user
        viewModel.setUserMarker(user.id, marker)
    }

    private fun updateUserOnMap(user: User) {
        val latitude = user.currentPosition?.latitude ?: 0.0
        val longitude = user.currentPosition?.longitude ?: 0.0
        val location = LatLng(latitude, longitude)
        animateMarker(user.marker, location, LatLngInterpolator.LinearFixed())

        markers[user.marker?.id]?.apply { currentPosition = location }
    }

    enum class UIState {
        LOADING, SUCCESS, FAILED
    }

    inner class CustomInfoWindowAdapter : GoogleMap.InfoWindowAdapter {

        override fun getInfoContents(p0: Marker): View? {
            return null
        }

        override fun getInfoWindow(marker: Marker): View {
            val user = markers[marker.id]
            val latitude = user?.currentPosition?.latitude ?: 0.0
            val longitude = user?.currentPosition?.longitude ?: 0.0
            bubbleBinding.run {
              tvName.text = user?.name ?: ""
              tvAddress.text = getAddress(requireContext(), latitude, longitude)
              Picasso.get().load(user?.profileImage).placeholder(R.drawable.person)
                .error(R.drawable.ic_broken_image).into(imageView)
            }
            return bubbleBinding.root
        }
    }
}