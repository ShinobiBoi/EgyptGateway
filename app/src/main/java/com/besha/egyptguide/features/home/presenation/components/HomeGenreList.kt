package com.besha.egyptguide.features.home.presenation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.besha.egyptguide.R
import com.besha.egyptguide.features.maps.presentaion.screen.GenreIconListItem
import com.google.android.libraries.places.api.model.PlaceTypes


@Composable
fun HomeGenreList(
    modifier: Modifier = Modifier,
    onCardClick: (GenreIconListItem) -> Unit
) {

    val genreList = listOf(
        GenreIconListItem("Hotels", R.drawable.hotels_ic, "hotels"),
        GenreIconListItem("Restaurant", R.drawable.restaurant_ic, PlaceTypes.RESTAURANT),
        GenreIconListItem("Cafe", R.drawable.cafe_ic, PlaceTypes.CAFE),
        GenreIconListItem("Mall", R.drawable.mall_ic, PlaceTypes.SHOPPING_MALL),
    )

    var selectedIndex by remember { mutableStateOf(0) }

    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {

        genreList.forEachIndexed { index, genre ->

            val isSelected = index == selectedIndex

            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(84.dp)
                    .clickable {
                        selectedIndex = index
                        onCardClick(genre)
                    },
                shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected)
                        Color.White
                    else
                        Color.Transparent
                )
            ) {

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Icon(
                        painter = painterResource(id = genre.icon),
                        contentDescription = genre.title,
                        modifier = Modifier.size(24.dp),
                        tint = if (isSelected) Color.Black else Color.LightGray
                    )

                    Text(
                        text = genre.title,
                        color = if (isSelected) Color.Black else Color.LightGray
                    )
                }
            }
        }
    }
}
