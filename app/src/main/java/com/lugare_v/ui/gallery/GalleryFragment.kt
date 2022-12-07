package com.lugare_v.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.LatLng
import com.lugare_v.databinding.FragmentGalleryBinding
import com.lugare_v.model.Lugar
import com.lugare_v.viewmodel.GalleryViewModel
import com.lugare_v.viewmodel.LugarViewModel

class GalleryFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    ///Este objeto sera para interactuar con el mapa en la vista
    private lateinit var googleMap: GoogleMap
    private var mapReady = false

    //se toman los datos de los lugares desde el viewModel
    private lateinit var lugarViewModel: LugarViewModel

    // esta es una funcion especial que se ejecuta al crear el activity
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //se solicita la actualizacion del mapa
        binding.map.onCreate(savedInstanceState)
        binding.map.onResume()
        binding.map.getMapAsync(this)

    }

    //cuando el mapa esta listo...
    override fun onMapReady(map: GoogleMap) {
        map.let{
            googleMap = it
            mapReady = true
            ///se instruyee al mapa paraque se actualice
            lugarViewModel.getLugares.observe(viewLifecycleOwner){
                lugares->updateMap(lugares)
                //ubicaGPS
            }
        }
    }

    private fun updateMap(lugares: List<Lugar>) {
        if(mapReady){
            lugares.forEach { lugar ->
                if (lugar.latitud?.isFinite()==true
                    && lugar.longitud?.isFinite()==true){
                    val marca = LatLng(lugar.latitud,lugar.longitud)
                    googleMap.addMarker(MarkerOptions().position(marca))
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val lugarViewModel =
            ViewModelProvider(this)[LugarViewModel::class.java]

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)


        //AIzaSyACMpaGXAT7tNnK1-PSLhWEW47DitByovo
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}