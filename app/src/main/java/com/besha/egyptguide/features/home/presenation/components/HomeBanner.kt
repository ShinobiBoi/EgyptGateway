package com.besha.egyptguide.features.home.presenation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.besha.egyptguide.R
import com.besha.egyptguide.appcore.data.model.MyPlace
import com.besha.egyptguide.features.monuments.data.dto.MonumentDto
import com.besha.egyptguide.ui.theme.Typography
import kotlinx.coroutines.delay


@Composable
fun HomeBanner(
    modifier: Modifier = Modifier,
    monuments: List<MonumentDto>,
    pagerState: PagerState,
    screenHeight: Dp = 200.dp,
    onClick: () -> Unit
) {

    // Auto-slide logic
    LaunchedEffect(Unit) {
        while (true) {
            val currentPage = pagerState.currentPage
            delay(5000)
            if (monuments.isNotEmpty() && currentPage == pagerState.currentPage) {
                val nextPage = (pagerState.currentPage + 1) % monuments.size
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }


    // ---- The pager with overlay text ----
    HorizontalPager(
        state = pagerState,
        modifier = modifier.clickable(
            onClick = {
                onClick()
            }
        )
    ) { page ->
        val place = monuments[page]



        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(screenHeight * 0.52f)
        ) {
            // Movie image
            AsyncImage(
                model = place.images?.get(0) ?:"",
                contentDescription = place.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Gradient overlay (for better text visibility)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black),
                        )
                    )
            )

            // Text overlay (title + tag)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, end = 16.dp, bottom = 100.dp)
            ) {
                Text(
                    text = place.name ?: "",
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Clip,
                    style = Typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.basicMarquee()
                )



                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {

                    Icon(
                        painter = painterResource(id = R.drawable.location_ic),
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(17.dp)
                    )

                    Text(
                        text = place.city ?: "",
                        color = Color.LightGray,
                        maxLines = 1,
                        overflow = TextOverflow.Clip,
                        style = Typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.basicMarquee()
                    )
                }
            }
        }
    }
}
