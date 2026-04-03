package com.besha.egyptguide.features.calendar

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.ContentHeightMode
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.daysOfWeek
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreen() {
    val currentMonth = remember { YearMonth.now() }
    val state = rememberCalendarState(
        startMonth = currentMonth.minusMonths(12),
        endMonth = currentMonth.plusMonths(12),
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = DayOfWeek.SUNDAY
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        // Header (Month + Year)
        CalendarHeader(state)

        // Week Days Row
        DaysOfWeekTitle(daysOfWeek())

        // Calendar Grid
        HorizontalCalendar(
            modifier = Modifier.weight(1f), // 👈 important
            state = state,
            contentHeightMode = ContentHeightMode.Fill,
            dayContent = { day ->
                DayCell(day, state)
            }
        )
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarHeader(state: CalendarState) {
    val month = state.firstVisibleMonth.yearMonth

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF3F51B5))
            .padding(vertical = 32.dp, horizontal = 16.dp)
    ) {
        Column {
            Text(
                text = month.month.name.take(3),
                color = Color.White,
                style = MaterialTheme.typography.headlineLarge
            )
            Text(
                text = month.year.toString(),
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}


@Composable
fun DaysOfWeekTitle(days: List<DayOfWeek>) {
    Row(modifier = Modifier.fillMaxWidth()) {
        days.forEach { day ->
            Text(
                text = day.name.take(3),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DayCell(day: CalendarDay,state: CalendarState) {
    val isSelected = day.date == LocalDate.now()
    val isCurrentMonth = day.date.month == state.firstVisibleMonth.yearMonth.month

    Box(
        modifier = Modifier
            .fillMaxSize() // 👈 important with Fill mode
            .padding(2.dp),

        contentAlignment = Alignment.TopCenter
    ) {

        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color.Blue, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day.date.dayOfMonth.toString(),
                    color = Color.White
                )
            }
        } else {
            Text(
                text = day.date.dayOfMonth.toString(),
                color = if (isCurrentMonth) Color.Black else Color.Gray
            )
        }
    }
}

