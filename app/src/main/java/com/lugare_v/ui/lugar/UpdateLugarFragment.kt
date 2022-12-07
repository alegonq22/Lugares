package com.lugare_v.ui.lugar



import android.app.AlertDialog
import android.content.Intent
//import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.FileObserver.ACCESS
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.lugare_v.Manifest
import com.lugare_v.R
import com.lugare_v.databinding.FragmentUpdateLugarBinding
import com.lugare_v.model.Lugar
import com.lugare_v.viewmodel.LugarViewModel

class UpdateLugarFragment : Fragment() {
    //se recupera un argumento pasad
    private val args by navArgs<UpdateLugarFragmentArgs>()

    private var _binding: FragmentUpdateLugarBinding? = null
    private val binding get() = _binding!!
    private lateinit var lugarViewModel: LugarViewModel

    private  lateinit var mediaPlayer: MediaPlayer


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        lugarViewModel = ViewModelProvider(this).get(LugarViewModel::class.java)

        _binding = FragmentUpdateLugarBinding.inflate(inflater, container, false)

        //se pasan los valores a los campos de pantalla
        binding.etNombre.setText(args.lugar.nombre)
        binding.etCorreoLugar.setText(args.lugar.correo)
        binding.etTelefono.setText(args.lugar.telefono)
        binding.etWeb.setText(args.lugar.web)
        binding.tvLatitud.text =args.lugar.latitud.toString()
        binding.tvLongitud.text =args.lugar.longitud.toString()
        binding.tvAltura.text =args.lugar.altura.toString()

        binding.btUpdateLugar.setOnClickListener{updateLugar()}
        binding.btDeleteLugar.setOnClickListener{deleteLugar()}
        binding.btEmail.setOnClickListener{escribirCorreo()}
        binding.btPhone.setOnClickListener{llamarLugar()}
        binding.btWhatsapp.setOnClickListener{enviarWhastApp()}
        binding.btWeb.setOnClickListener{verWeb()}
        binding.btLocation.setOnClickListener{verMapa()}


        if(args.lugar.ruta_audio?.isNotEmpty()==true){
            //activa el boton para escuchar el boton paara escuchar el audio

            mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(args.lugar.ruta_audio)
            mediaPlayer.prepare()
            binding.btPlay.isEnabled = true
        }else{
            binding.btPlay.isEnabled = false
        }
        binding.btPlay.setOnClickListener{mediaPlayer.start()}

        if(args.lugar.ruta_imagen?.isNotEmpty()==true){
           Glide.with(requireContext()).load(args.lugar.ruta_imagen)
               .fitCenter()
               .into(binding.imagen)
        }

        return binding.root
    }

    private fun escribirCorreo() {
        val valor = binding.etCorreoLugar.text.toString()
        if (valor.isNotEmpty()){
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "message/rfc882"
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(valor))
            intent.putExtra(Intent.EXTRA_SUBJECT,
            getString(R.string.msg_saludos)+" "+binding.etNombre.text)
            intent.putExtra(Intent.EXTRA_TEXT,getString(R.string.msg_mensaje_correo))
            startActivity(intent)
        }else{
            Toast.makeText(requireContext(),
            getString(R.string.msg_datos),Toast.LENGTH_LONG)
        }
    }

    private fun llamarLugar() {
        val valor = binding.etTelefono.text.toString()
        if (valor.isNotEmpty()){
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse( "tel:$valor")
                if (requireActivity().
                    checkSelfPermission(android.Manifest.permission.CALL_PHONE) //PREGUNTARLE AL PROFE SI ESTO ESTA BIEN
                    != PackageManager.PERMISSION_GRANTED){

                    requireActivity().requestPermissions(arrayOf(android.Manifest.permission.CALL_PHONE),105)
                }
        }else{
            Toast.makeText(requireContext(),
                getString(R.string.msg_datos),Toast.LENGTH_LONG)
        }

    }

    private fun enviarWhastApp() {

        val valor = binding.etTelefono.text.toString()
        if (valor.isNotEmpty()){
            val intent = Intent(Intent.ACTION_VIEW)
            val uri = "whatsapp://send?phone=506$valor&text="+getString(R.string.msg_saludos)
            intent.setPackage("com.whatsapp")
            intent.data = Uri.parse(uri)

            startActivity(intent)

        }else{
            Toast.makeText(requireContext(),
                getString(R.string.msg_datos),Toast.LENGTH_LONG)
        }

    }

    private fun verWeb() {
        val valor = binding.etWeb.text.toString()
        if (valor.isNotEmpty()){

            val uri = "http://$valor"
            val intent = Intent(Intent.ACTION_VIEW,Uri.parse(uri))
            intent.data = Uri.parse(uri)

            startActivity(intent)

        }else{
            Toast.makeText(requireContext(),
                getString(R.string.msg_datos),Toast.LENGTH_LONG).show()
        }

    }

    private fun verMapa() {
        val latitud = binding.tvLatitud.text.toString().toDouble()
        val longitud = binding.tvLongitud.text.toString().toDouble()
        //val latitud = binding.tvLatitud.text.toString().toDouble()
        if (latitud.isFinite() && longitud.isFinite()){

            val uri = "geo:$latitud,$longitud?z18"
            val intent = Intent(Intent.ACTION_VIEW,Uri.parse(uri))


            startActivity(intent)

        }else{
            Toast.makeText(requireContext(),
                getString(R.string.msg_datos),Toast.LENGTH_LONG).show()
        }
    }









    private fun deleteLugar() {
        val alerta = AlertDialog.Builder(requireContext())
        alerta.setTitle(R.string.bt_delete_lugar)
        alerta.setMessage(getString(R.string.msg_pregunta_eliminar)+"${args.lugar.nombre}?")
        alerta.setPositiveButton(getString(R.string.msg_si)){_,_ ->
            lugarViewModel.deleteLugar(args.lugar)
            Toast.makeText(requireContext(),getString(R.string.msg_lugar_deleted),Toast.LENGTH_LONG).show()
            findNavController().navigate(R.id.action_updateLugarFragment_to_nav_lugar)
        }
        alerta.setNegativeButton(getString(R.string.msg_no)){_,_ ->}
        alerta.create().show()
    }


    private fun updateLugar() {
        val nombre = binding.etNombre.text.toString()
        val correo = binding.etCorreoLugar.text.toString()
        val telefono = binding.etTelefono.text.toString()
        val web = binding.etWeb.text.toString()

        if (nombre.isNotEmpty()){ //al menos tenemos un nombre
            val lugar= Lugar(args.lugar.id,nombre,correo,telefono,web,
                args.lugar.latitud,
                args.lugar.longitud,
                args.lugar.altura,
                args.lugar.ruta_audio,
                args.lugar.ruta_imagen)

            lugarViewModel.saveLugar(lugar)
            Toast.makeText(requireContext(),getText(R.string.msg_lugar_added),
            Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_updateLugarFragment_to_nav_lugar)

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