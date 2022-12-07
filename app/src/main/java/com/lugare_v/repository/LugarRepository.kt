package com.lugare_v.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lugare_v.data.LugarDao
import com.lugare_v.model.Lugar


class LugarRepository(private val lugarDao: LugarDao) {
   fun saveLugar(lugar: Lugar){
        lugarDao.saveLugar(lugar)
    }

   fun deleteLugar(lugar: Lugar){
        lugarDao.deleteLugar(lugar)
    }

    fun getLugares() : MutableLiveData<List<Lugar>> = lugarDao.getLugares()




}