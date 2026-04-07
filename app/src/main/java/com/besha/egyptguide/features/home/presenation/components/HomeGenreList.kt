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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.besha.egyptguide.features.home.data.constants.GenreType


@Composable
fun HomeGenreList(
    modifier: Modifier = Modifier,
    selectedGenre: GenreType = GenreType.HOTELS,
    onCardClick: (GenreType) -> Unit
) {



    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {

        val genres = listOf(GenreType.HOTELS, GenreType.RESTAURANT, GenreType.CAFE, GenreType.MALL)

        genres.forEach {  genre ->
            val isSelected = selectedGenre == genre

            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(84.dp)
                    .clickable {
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
