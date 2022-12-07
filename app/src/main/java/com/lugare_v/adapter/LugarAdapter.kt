package com.lugare_v.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lugare_v.databinding.FragmentAddLugarBinding
import com.lugare_v.databinding.LugarFilaBinding
import com.lugare_v.model.Lugar
import com.lugare_v.ui.lugar.LugarFragmentDirections

class LugarAdapter : RecyclerView.Adapter<LugarAdapter.LugarViewHolder>() {

    // las Lista de lugares a dibujar
    private var listaLugares = emptyList<Lugar>()

            //contenedor de vistas "Cajjitas" en memoria
    inner class LugarViewHolder(private val itemBinding: LugarFilaBinding)
        : RecyclerView.ViewHolder(itemBinding.root){
            fun dibuja(lugar: Lugar){
                itemBinding.tvNombre.text = lugar.nombre
                itemBinding.tvCorreo.text = lugar.correo
                itemBinding.tvTelefono.text = lugar.telefono
                Glide.with(itemBinding.root.context).load(lugar.ruta_imagen)
                    .circleCrop().into(itemBinding.imageView2)

                itemBinding.vistaFila.setOnClickListener{
                    val action = LugarFragmentDirections
                        .actionNavLugarToUpdateLugarFragment(lugar)
                    itemView.findNavController().navigate(action)
                }


            }
        }
        //crea una "cajota" una vista del tipo lugarfila...

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LugarViewHolder {
            val itemBinding = LugarFilaBinding.
            inflate(LayoutInflater.from(parent.context)
            ,parent
            ,false)

            return LugarViewHolder(itemBinding)
        }

        // con una "cajita" creada... se pasa a dibujar los datos del lugar

        override fun onBindViewHolder(holder: LugarViewHolder, position: Int) {
            val lugarActual = listaLugares[position]
            holder.dibuja(lugarActual)
        }

        override fun getItemCount(): Int {
            return listaLugares.size
        }


        fun setLugares(lugares: List<Lugar>){
            listaLugares = lugares
            notifyDataSetChanged()  //se nnotofica que
                                    // el conjunto de datos
                                    //cambio y se redibuja toda a lista
        }
    }