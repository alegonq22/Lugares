package com.lugare_v.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.ktx.Firebase

import com.lugare_v.model.Lugar


class LugarDao {

    ///valores para la estructura de Firestore Cloud
    private val coleccion1 = "lugaresApp"
    private val usuario = Firebase.auth.currentUser?.email.toString()
    private val coleccion2 = "MisLugares"


    ///objeto para la conexion para la base de datos en la nuve
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    init {
        ///inicializa la conexion con firestore
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
    }

    //CRUD = create read update delete

    ///se recibe un objeto lugar, se valida si id tiene un valor, si lo tiene
    ///se actualiza y si no se crea
    fun saveLugar(lugar: Lugar) {
        val documento: DocumentReference
        if (lugar.id.isEmpty()) {
            documento = firestore
                .collection(coleccion1)
                .document(usuario)
                .collection(coleccion2)
                .document()
            lugar.id = documento.id
        } else {

            documento = firestore
                .collection(coleccion1)
                .document(usuario)
                .collection(coleccion2)
                .document(lugar.id)

        }

        documento.set(lugar)
            .addOnSuccessListener {
                Log.d("saveLugar", "lugar agregado/actualizado")
            }
            .addOnCanceledListener {
                Log.e("saveLugar", "lugar NO agregado/actualizado")
            }
    }


    fun deleteLugar(lugar: Lugar) {
        if (lugar.id.isEmpty()) {
            firestore
                .collection(coleccion1)
                .document(usuario)
                .collection(coleccion2)
                .document(lugar.id)
                .delete()
                .addOnSuccessListener {
                    Log.d("daleteLugar", "lugar eliminado")
                }
                .addOnCanceledListener {
                    Log.e("daleteLugar", "lugar NO eliminado")
                }
        }
    }


    fun getLugares(): MutableLiveData<List<Lugar>> {
        val listaLugares = MutableLiveData<List<Lugar>>()
        firestore
            .collection(coleccion1)
            .document(usuario)
            .collection(coleccion2)
            .addSnapshotListener { instantanea, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                if (instantanea != null) {
                    val lista= ArrayList<Lugar>()

                    instantanea.documents.forEach{
                        val lugar = it.toObject(Lugar::class.java)
                        if(lugar!=null){
                            lista.add(lugar)
                        }
                    }
                    listaLugares.value= lista
                }
            }
        return listaLugares
    }
}


