package com.besha.egyptguide.features.home.data.constants

import com.besha.egyptguide.R
import com.google.android.libraries.places.api.model.PlaceTypes

enum class GenreType(
    val value: Int,
    val title: String,
    val icon: Int,
    val placeTypes: String
) {

    HOTELS(0,"Hotels", R.drawable.hotels_ic, "hotel"),
    RESTAURANT(1,"Restaurant", R.drawable.restaurant_ic, PlaceTypes.RESTAURANT),
    CAFE(2,"Cafe", R.drawable.cafe_ic, PlaceTypes.CAFE),
    MALL(3,"Mall", R.drawable.mall_ic, PlaceTypes.SHOPPING_MALL),
    GAS_STATION(4,"Gas station", R.drawable.gas_ic, PlaceTypes.GAS_STATION),
    HOSPITAL(5,"Hospital", R.drawable.hospital_ic, PlaceTypes.HOSPITAL),
    CHURCH(6,"Church", R.drawable.church_ic, PlaceTypes.CHURCH),
    MOSQUE(7,"Mosque", R.drawable.mosque_ic, PlaceTypes.MOSQUE);


    companion object {
        operator fun invoke(value: Int): GenreType = GenreType.entries.first { it.value == value }
    }

}
