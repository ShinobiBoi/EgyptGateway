package com.besha.egyptguide.features.leaderboard.presenation.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.besha.egyptguide.R
import com.besha.egyptguide.features.leaderboard.data.model.LeaderboardListItem
import com.besha.egyptguide.features.leaderboard.presenation.viewmodel.LeaderboardActions
import com.besha.egyptguide.features.leaderboard.presenation.viewmodel.LeaderboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(userId: String, onBackClick: () -> Unit = {}) {
    val viewModel: LeaderboardViewModel = hiltViewModel()
    val state by viewModel.viewStates.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.executeAction(LeaderboardActions.GetLeaderboard)
        viewModel.executeAction(LeaderboardActions.GetCurrentRank)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Leaderboard",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.leaderboard.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = colorResource(R.color.blue)
                    )
                }
                state.leaderboard.errorThrowable != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Couldn't load leaderboard", color = Color.Gray)
                        Button(
                            onClick = { viewModel.executeAction(LeaderboardActions.GetLeaderboard) },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Retry")
                        }
                    }
                }
                state.leaderboard.data != null -> {
                    val users = state.leaderboard.data!!
                    val currentUser = state.currentRank.data

                    Column(modifier = Modifier.fillMaxSize()) {
                        Box(modifier = Modifier.weight(1f)) {
                            LeaderboardContent(users, userId)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Bottom sticky bar for current user
                        currentUser?.let {
                            CurrentUserStickyRow(it)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LeaderboardContent(users: List<LeaderboardListItem>, currentUserId: String) {
    val topThree = users.take(3)
    val remainingUsers = users.drop(3)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (topThree.isNotEmpty()) {
            item {
                PodiumSection(topThree, currentUserId)
            }
        }

        itemsIndexed(remainingUsers) { index, user ->
            LeaderboardRow(
                user = user,
                rank = index + 4,
                isCurrentUser = user.user_id == currentUserId
            )
        }
    }
}

@Composable
fun PodiumSection(topThree: List<LeaderboardListItem>, currentUserId: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        // 2nd Place
        if (topThree.size >= 2) {
            PodiumItem(
                user = topThree[1],
                rank = 2,
                color = Color(0xFFC0C0C0),
                height = 100.dp,
                isCurrentUser = topThree[1].user_id == currentUserId
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))

        // 1st Place
        if (topThree.isNotEmpty()) {
            PodiumItem(
                user = topThree[0],
                rank = 1,
                color = Color(0xFFFFD700),
                height = 140.dp,
                isCurrentUser = topThree[0].user_id == currentUserId
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 3rd Place
        if (topThree.size >= 3) {
            PodiumItem(
                user = topThree[2],
                rank = 3,
                color = Color(0xFFCD7F32),
                height = 80.dp,
                isCurrentUser = topThree[2].user_id == currentUserId
            )
        }
    }
}

@Composable
fun PodiumItem(
    user: LeaderboardListItem,
    rank: Int,
    color: Color,
    height: androidx.compose.ui.unit.Dp,
    isCurrentUser: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Surface(
                modifier = Modifier
                    .size(if (rank == 1) 80.dp else 64.dp)
                    .border(
                        width = if (isCurrentUser) 3.dp else 2.dp,
                        color = if (isCurrentUser) colorResource(R.color.blue) else color,
                        shape = CircleShape
                    ),
                shape = CircleShape,
                color = color.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {


                    val profilePic = user.photo_url


                    if (!profilePic.isNullOrEmpty())

                        if (profilePic.startsWith("http"))
                                AsyncImage(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .size(40.dp),
                                    model = user.photo_url.toUri(),
                                    contentDescription = "profile pic",
                                    contentScale = ContentScale.Crop,
                                )
                        else
                            AsyncImage(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .size(40.dp),
                                model = "http://127.0.0.1:8000${user.photo_url}".toUri(),
                                contentDescription = "profile pic",
                                contentScale = ContentScale.Crop,
                            )
                    else {
                        Text(
                            text = user.name?.take(1)?.uppercase() ?: "?",
                            fontSize = if (rank == 1) 32.sp else 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = color
                        )
                    }
                }
            }
            
            if (rank == 1) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier
                        .size(28.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 6.dp, y = (-6).dp)
                )
            }
        }

        Text(
            text = user.name?.split(" ")?.firstOrNull() ?: "",
            fontSize = 14.sp,
            fontWeight = if (isCurrentUser) FontWeight.ExtraBold else FontWeight.Medium,
            color = if (isCurrentUser) colorResource(R.color.blue) else colorResource(R.color.black)
        )

        Card(
            modifier = Modifier
                .width(if (rank == 1) 90.dp else 80.dp)
                .height(height)
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            colors = CardDefaults.cardColors(containerColor = color)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "#$rank",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp
                )
                Text(
                    text = "${user.points} pts",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun LeaderboardRow(user: LeaderboardListItem, rank: Int, isCurrentUser: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isCurrentUser) 2.dp else 0.dp,
                color = if (isCurrentUser) colorResource(R.color.blue) else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isCurrentUser) 4.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = rank.toString(),
                modifier = Modifier.width(32.dp),
                fontWeight = FontWeight.Bold,
                color = if (isCurrentUser) colorResource(R.color.blue) else Color.Gray,
                fontSize = 16.sp
            )

            val profilePic = user.photo_url


            if (!profilePic.isNullOrEmpty())

                if (profilePic.startsWith("http"))
                    AsyncImage(
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(40.dp),
                        model = user.photo_url.toUri(),
                        contentDescription = "profile pic",
                        contentScale = ContentScale.Crop,
                    )
                else
                    AsyncImage(
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(40.dp),
                        model = "http://127.0.0.1:8000${user.photo_url}".toUri(),
                        contentDescription = "profile pic",
                        contentScale = ContentScale.Crop,
                    )
            else {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(colorResource(R.color.blue).copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.name?.take(1)?.uppercase() ?: "?",
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.blue)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isCurrentUser) "${user.name} (You)" else user.name ?: "Unknown",
                    fontWeight = if (isCurrentUser) FontWeight.Bold else FontWeight.Medium,
                    color = colorResource(R.color.black),
                    fontSize = 16.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${user.points}",
                    fontWeight = FontWeight.ExtraBold,
                    color = colorResource(R.color.black),
                    fontSize = 16.sp
                )
                Text(
                    text = "pts",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun CurrentUserStickyRow(user: LeaderboardListItem) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(24.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
        color = colorResource(R.color.blue),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = user.rank?.toString() ?: "?",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = "Your Current Rank",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                    Text(
                        text = user.name ?: "You",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${user.points}",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp
                )
                Text(
                    text = "Total Points",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }
        }
    }
}
