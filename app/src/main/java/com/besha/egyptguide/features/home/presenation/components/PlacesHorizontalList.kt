package com.besha.egyptguide.features.home.presenation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.besha.egyptguide.appcore.data.model.MyPlace
import com.besha.egyptguide.appcore.mvi.CommonViewState
import com.google.android.gms.maps.model.LatLng


@Composable
fun PlacesHorizontalList(places: CommonViewState<List<MyPlace>>,location: LatLng?) {

    Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {


        if (places.isLoading  ||  location == null) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(32.dp)
            )
        }
        else
            places.data?.let { places ->
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(end = 16.dp)
                ) {
                    items(places) { place ->
                        NearbyPlaceCard(
                            place = place,
                            modifier = Modifier.width(300.dp)
                        )
                    }
                }
            }
    }


}