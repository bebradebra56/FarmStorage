package com.farmerinven.apsola.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.farmerinven.apsola.ui.theme.*
import com.farmerinven.apsola.ui.viewmodel.AnalyticsViewModel
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
        // Total Items Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Inventory2,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = PrimaryGold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.totalItems.toString(),
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGold
                    )
                    Text(
                        text = "Total Items",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }

        // Status Distribution
        item {
            Text(
                "Status Distribution",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            if (uiState.totalItems > 0) {
                StatusPieChart(
                    workingCount = uiState.workingCount,
                    needsRepairCount = uiState.needsRepairCount,
                    outOfStockCount = uiState.outOfStockCount
                )
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No data available",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }

        // Status Breakdown
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Working",
                    count = uiState.workingCount,
                    color = StatusGreen,
                    icon = Icons.Default.CheckCircle,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Needs Repair",
                    count = uiState.needsRepairCount,
                    color = SecondaryOrange,
                    icon = Icons.Default.Build,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Out of Stock",
                    count = uiState.outOfStockCount,
                    color = StatusRed,
                    icon = Icons.Default.Warning,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Category Breakdown
        if (uiState.categoryStats.isNotEmpty()) {
            item {
                Text(
                    "Category Breakdown",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(uiState.categoryStats) { categoryStats ->
                CategoryStatCard(
                    name = categoryStats.category.name,
                    itemCount = categoryStats.itemCount,
                    color = try {
                        Color(categoryStats.category.colorHex.toColorInt())
                    } catch (e: Exception) {
                        PrimaryGold
                    },
                    total = uiState.totalItems
                )
            }
        }
        }
    }
}

@Composable
fun StatusPieChart(
    workingCount: Int,
    needsRepairCount: Int,
    outOfStockCount: Int
) {
    val total = workingCount + needsRepairCount + outOfStockCount
    if (total == 0) return

    val workingAngle = (workingCount.toFloat() / total) * 360f
    val needsRepairAngle = (needsRepairCount.toFloat() / total) * 360f
    val outOfStockAngle = (outOfStockCount.toFloat() / total) * 360f

    var animationPlayed by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "pie_chart_animation"
    )

    LaunchedEffect(Unit) {
        animationPlayed = true
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Canvas(
                modifier = Modifier.size(200.dp)
            ) {
                val canvasSize = size.minDimension
                val radius = canvasSize / 2
                val strokeWidth = 40.dp.toPx()

                // Working (Green)
                drawArc(
                    color = StatusGreen,
                    startAngle = -90f,
                    sweepAngle = workingAngle * animatedProgress,
                    useCenter = false,
                    topLeft = Offset(
                        (size.width - canvasSize) / 2,
                        (size.height - canvasSize) / 2
                    ),
                    size = Size(canvasSize, canvasSize),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // Needs Repair (Orange)
                drawArc(
                    color = SecondaryOrange,
                    startAngle = -90f + workingAngle * animatedProgress,
                    sweepAngle = needsRepairAngle * animatedProgress,
                    useCenter = false,
                    topLeft = Offset(
                        (size.width - canvasSize) / 2,
                        (size.height - canvasSize) / 2
                    ),
                    size = Size(canvasSize, canvasSize),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // Out of Stock (Red)
                drawArc(
                    color = StatusRed,
                    startAngle = -90f + (workingAngle + needsRepairAngle) * animatedProgress,
                    sweepAngle = outOfStockAngle * animatedProgress,
                    useCenter = false,
                    topLeft = Offset(
                        (size.width - canvasSize) / 2,
                        (size.height - canvasSize) / 2
                    ),
                    size = Size(canvasSize, canvasSize),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Legend
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                LegendItem(
                    color = StatusGreen,
                    label = "Working",
                    count = workingCount,
                    percentage = (workingCount.toFloat() / total * 100).toInt()
                )
                LegendItem(
                    color = SecondaryOrange,
                    label = "Needs Repair",
                    count = needsRepairCount,
                    percentage = (needsRepairCount.toFloat() / total * 100).toInt()
                )
                LegendItem(
                    color = StatusRed,
                    label = "Out of Stock",
                    count = outOfStockCount,
                    percentage = (outOfStockCount.toFloat() / total * 100).toInt()
                )
            }
        }
    }
}

@Composable
fun LegendItem(
    color: Color,
    label: String,
    count: Int,
    percentage: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Text(
            text = "$count ($percentage%)",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun StatCard(
    title: String,
    count: Int,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun CategoryStatCard(
    name: String,
    itemCount: Int,
    color: Color,
    total: Int
) {
    val percentage = if (total > 0) (itemCount.toFloat() / total * 100).toInt() else 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(color),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = name.firstOrNull()?.uppercase() ?: "?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Column {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "$itemCount items ($percentage%)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}
