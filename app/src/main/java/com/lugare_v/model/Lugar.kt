package com.lugare_v.model

import android.os.Parcelable

import kotlinx.android.parcel.Parcelize

@Parcelize

data class Lugar(

    var id: String,

    val nombre:String,

    val correo:String?,

    val telefono:String?,

    val web:String?,

    val latitud:Double?,

    val longitud:Double?,

    val altura:Double?,

    val ruta_audio:String?,

    val ruta_imagen:String?,
) : Parcelable{
    constructor():
            this(",","","",
                "","",0.0
            ,0.0,0.0,""
            ,"")
}
