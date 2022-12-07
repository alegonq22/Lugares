package com.lugare_v.ui.lugar

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
//import android.Manifest
import android.os.FileObserver.ACCESS
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.lugare_v.Manifest
import com.lugare_v.R
import com.lugare_v.databinding.FragmentAddLugarBinding
import com.lugare_v.model.Lugar
import com.lugare_v.utiles.AudioUtiles
import com.lugare_v.viewmodel.LugarViewModel
import com.lugares_v.utiles.ImagenUtiles

class AddLugarFragment : Fragment() {
    private var _binding: FragmentAddLugarBinding? = null
    private val binding get() = _binding!!
    private lateinit var lugarViewModel: LugarViewModel
    private lateinit var audioUtiles:AudioUtiles

    private lateinit var tomarFotoActivity: ActivityResultLauncher<Intent>
    private lateinit var imagenUtiles: ImagenUtiles

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        lugarViewModel = ViewModelProvider(this).get(LugarViewModel::class.java)

        _binding = FragmentAddLugarBinding.inflate(inflater, container, false)

        binding.btAddLugar.setOnClickListener{
            binding.progressBar.visibility = ProgressBar.VISIBLE
            binding.msgMensaje.text = getString(R.string.msg_subiendo_audio)
            binding.msgMensaje.visibility = TextView.VISIBLE

            subeAudio()
        }


        activeGPS()

        audioUtiles = AudioUtiles(
            requireActivity(),
            requireContext(),
            binding.btAccion,
            binding.btPlay,
            binding.btDelete,
            getString(R.string.msg_graba_audio),
            getString(R.string.msg_detener_audio)
        )
        tomarFotoActivity = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()){
            result ->
            if(result.resultCode==Activity.RESULT_OK){
                imagenUtiles.actualizaFoto()
            }

        }

        imagenUtiles = ImagenUtiles(
            requireContext(),
            binding.btPhoto,
            binding.btRotaL,
            binding.btRotaR,
            binding.imagen,
            tomarFotoActivity)

        return binding.root
    }

    private fun subeAudio() {
        val audioFile = audioUtiles.audioFile
        if (audioFile.exists() && audioFile.isFile && audioFile.canRead()){
            val ruta = Uri.fromFile(audioFile) //la ruta del archivo local...
            val rutaNube = "lugaresApp/${Firebase.auth.currentUser?.email}/audios/${audioFile.name}"

            val referencia: StorageReference = Firebase.storage.reference.child(rutaNube)

            referencia.putFile(ruta).addOnSuccessListener{
                referencia.downloadUrl.addOnSuccessListener {
                    val rutaPublicaAudio = it.toString()
                    subeImagen(rutaPublicaAudio)
                }
            }
                .addOnFailureListener{
                    subeImagen("")
                }
        }else{
            subeImagen("")
        }

    }

    private fun subeImagen(rutaPublicaAudio: String) {
        binding.msgMensaje.text = getString(R.string.msg_subiendo_imagen)
        val imagenFile = imagenUtiles.imagenFile
        if (imagenFile.exists() && imagenFile.isFile && imagenFile.canRead()){
            val ruta = Uri.fromFile(imagenFile) //la ruta del archivo local...
            val rutaNube = "lugaresApp/${Firebase.auth.currentUser?.email}/imagenes/${imagenFile.name}"

            val referencia: StorageReference = Firebase.storage.reference.child(rutaNube)

            referencia.putFile(ruta).addOnSuccessListener{
                referencia.downloadUrl.addOnSuccessListener {
                    val rutaPublicaImagen = it.toString()
                    subeLugar(rutaPublicaAudio,rutaPublicaImagen)
                }
            }
                .addOnFailureListener{
                    subeLugar(rutaPublicaAudio,"")
                }
        }else{
            subeLugar(rutaPublicaAudio,"")
        }
    }

    private fun activeGPS(){
        if(requireActivity().
            checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
                requireActivity().
                checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED){

            requireActivity().requestPermissions(arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION),105)
        }else{
        val fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
            fusedLocationClient.lastLocation.addOnSuccessListener {
                    location: Location? ->
                if(location!=null){
                    binding.tvLatitud.text = "${location.latitude}"
                    binding.tvLongitud.text = "${location.longitude}"
                    binding.tvAltura.text = "${location.altitude}"

                }else{
                    binding.tvLatitud.text = "0.0"
                    binding.tvLongitud.text = "0.0"
                    binding.tvAltura.text = "0.0"
                }

            }
        }
    }



    private fun subeLugar(rutaAudio: String,rutaImagen:String) {
        val nombre = binding.etNombre.text.toString()
        val correo = binding.etCorreoLugar.text.toString()
        val telefono = binding.etTelefono.text.toString()
        val web = binding.etWeb.text.toString()
        val latitud= binding.tvLatitud.text.toString().toDouble()
        val longitud= binding.tvLongitud.text.toString().toDouble()
        val altura= binding.tvAltura.text.toString().toDouble()

        if (nombre.isNotEmpty()){ //al menos tenemos un nombre
            val lugar= Lugar("",nombre,correo,telefono,web,
                latitud,longitud,altura, rutaAudio,rutaImagen)

            lugarViewModel.saveLugar(lugar)
            Toast.makeText(requireContext(),getText(R.string.msg_lugar_added),
            Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_addLugarFragment_to_nav_lugar)
        }else {
            Toast.makeText(requireContext(),getText(R.string.msg_datos),
            Toast.LENGTH_LONG).show()

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}