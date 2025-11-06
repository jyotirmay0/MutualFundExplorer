package com.example.fundexplorer.presentation.Screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fundexplorer.data.NavData
import com.example.fundexplorer.data.TimeRange
import com.example.fundexplorer.presentation.viewmodel.DetailEvent
import com.example.fundexplorer.presentation.viewmodel.DetailState
import com.example.fundexplorer.presentation.viewmodel.DetailViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: DetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val state = viewModel.state.value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fund Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                state.error.isNotBlank() -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Oops! Something went wrong",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.onEvent(DetailEvent.Refresh) }) {
                            Text("Retry")
                        }
                    }
                }
                state.fundDetail != null -> {
                    DetailContent(
                        state = state,
                        onTimeRangeSelected = {
                            viewModel.onEvent(DetailEvent.TimeRangeSelected(it))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DetailContent(
    state: DetailState,
    onTimeRangeSelected: (TimeRange) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Header Card
        state.fundDetail?.let { fund ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = fund.schemeName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        InfoItem(
                            label = "Fund House",
                            value = fund.fundHouse ?: "N/A"
                        )
                        InfoItem(
                            label = "Category",
                            value = fund.schemeType ?: "N/A"
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (state.filteredNavData.isNotEmpty()) {
                        val latestNav = state.filteredNavData.first()
                        Text(
                            text = "Current NAV",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "₹${latestNav.nav}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "As on ${latestNav.date}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }


        Text(
            text = "NAV Performance",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyRow(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(TimeRange.values()) { timeRange ->
                FilterChip(
                    selected = timeRange == state.selectedTimeRange,
                    onClick = { onTimeRangeSelected(timeRange) },
                    label = { Text(timeRange.displayName) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))


        if (state.filteredNavData.isNotEmpty()) {
            NavChart(
                navData = state.filteredNavData,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(16.dp)
            )
        }


        if (state.filteredNavData.isNotEmpty()) {
            StatisticsCard(navData = state.filteredNavData)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun InfoItem(
    label: String,
    value: String
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun NavChart(
    navData: List<NavData>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            val navValues = navData.mapNotNull { it.nav.toDoubleOrNull() }

            if (navValues.isNotEmpty()) {
                val minNav = navValues.minOrNull() ?: 0.0
                val maxNav = navValues.maxOrNull() ?: 0.0
                val change = ((navValues.last() - navValues.first()) / navValues.first()) * 100

                Text(
                    text = "NAV Trend",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    StatItem("Min", "₹%.2f".format(minNav))
                    StatItem("Max", "₹%.2f".format(maxNav))
                    StatItem(
                        "Change",
                        "%+.2f%%".format(change),
                        color = if (change >= 0) Color(0xFF4CAF50) else Color(0xFFF44336)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))


                NavLineChart(
                    navValues = navValues,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
            }
        }
    }
}

@Composable
fun NavLineChart(
    navValues: List<Double>,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surfaceVariant

    Canvas(modifier = modifier) {
        if (navValues.isEmpty()) return@Canvas

        val width = size.width
        val height = size.height
        val padding = 40f

        val minValue = navValues.minOrNull() ?: 0.0
        val maxValue = navValues.maxOrNull() ?: 0.0
        val range = maxValue - minValue

        if (range == 0.0) return@Canvas

        val xStep = (width - 2 * padding) / (navValues.size - 1).coerceAtLeast(1)


        val gridLines = 5
        for (i in 0..gridLines) {
            val y = padding + (height - 2 * padding) * i / gridLines
            drawLine(
                color = surfaceColor.copy(alpha = 0.3f),
                start = androidx.compose.ui.geometry.Offset(padding, y),
                end = androidx.compose.ui.geometry.Offset(width - padding, y),
                strokeWidth = 1f
            )
        }


        val path = androidx.compose.ui.graphics.Path()
        val points = mutableListOf<androidx.compose.ui.geometry.Offset>()

        navValues.forEachIndexed { index, value ->
            val x = padding + index * xStep
            val y = height - padding - ((value - minValue) / range * (height - 2 * padding)).toFloat()

            points.add(androidx.compose.ui.geometry.Offset(x, y))

            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }


        val fillPath = androidx.compose.ui.graphics.Path().apply {
            addPath(path)
            lineTo(width - padding, height - padding)
            lineTo(padding, height - padding)
            close()
        }

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    primaryColor.copy(alpha = 0.3f),
                    primaryColor.copy(alpha = 0.05f)
                ),
                startY = 0f,
                endY = height
            )
        )


        drawPath(
            path = path,
            color = primaryColor,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = 3f,
                cap = androidx.compose.ui.graphics.StrokeCap.Round,
                join = androidx.compose.ui.graphics.StrokeJoin.Round
            )
        )


        points.forEach { point ->
            drawCircle(
                color = primaryColor,
                radius = 4f,
                center = point
            )
            drawCircle(
                color = Color.White,
                radius = 2f,
                center = point
            )
        }
    }
}

@Composable
fun StatItem(
    label: String,
    value: String,
    color: Color? = null
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color ?: MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun StatisticsCard(
    navData: List<NavData>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Performance Statistics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            val navValues = navData.mapNotNull { it.nav.toDoubleOrNull() }
            if (navValues.isNotEmpty()) {
                val startNav = navValues.first()
                val endNav = navValues.last()
                val minNav = navValues.minOrNull() ?: 0.0
                val maxNav = navValues.maxOrNull() ?: 0.0
                val avgNav = navValues.average()
                val returns = ((endNav - startNav) / startNav) * 100

                StatRow("Starting NAV", "₹%.2f".format(startNav))
                StatRow("Current NAV", "₹%.2f".format(endNav))
                StatRow("Lowest NAV", "₹%.2f".format(minNav))
                StatRow("Highest NAV", "₹%.2f".format(maxNav))
                StatRow("Average NAV", "₹%.2f".format(avgNav))
                StatRow(
                    "Returns",
                    "%+.2f%%".format(returns),
                    valueColor = if (returns >= 0) Color(0xFF4CAF50) else Color(0xFFF44336)
                )
            }
        }
    }
}

@Composable
fun StatRow(
    label: String,
    value: String,
    valueColor: Color? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = valueColor ?: MaterialTheme.colorScheme.onSurface
        )
    }
}

