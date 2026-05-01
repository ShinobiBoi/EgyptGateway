package com.besha.egyptguide.features.profile.presenation.screen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.besha.egyptguide.R


@Composable
fun ProfileInfoItemRow(title: String, value: String? = null, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (value != null) {
                Text(text = "$title: ", color = colorResource(R.color.black), fontSize = 15.sp)
                Text(
                    text = value,
                    color = colorResource(R.color.gray),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(end = 4.dp)
                )
            } else
                Text(text = title, color = colorResource(R.color.black), fontSize = 15.sp)


        }
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = null,
            tint = colorResource(R.color.gray),
            modifier = Modifier.size(18.dp)
        )
    }
}