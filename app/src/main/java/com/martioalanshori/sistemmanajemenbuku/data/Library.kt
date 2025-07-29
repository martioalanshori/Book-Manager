package com.martioalanshori.sistemmanajemenbuku.data

import com.google.android.gms.maps.model.LatLng

data class Library(
    val id: Int,
    val name: String,
    val address: String,
    val phone: String,
    val email: String,
    val website: String,
    val description: String,
    val location: LatLng,
    val openingHours: String,
    val facilities: List<String>
) 