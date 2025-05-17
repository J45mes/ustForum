/*  ui/alerts/AlertScreen.kt  */
package com.example.hkustforum            // ← keep in its own package

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.hkustforum.*             // HKUST colours + BottomNavigationRedesign
import com.example.hkustforum.ui.alerts.AlertsViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh              // ← NEW
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState // ← NEW

/* ─────────────────────────────────────────────────────────── */
/*  Screen                                                    */
/* ─────────────────────────────────────────────────────────── */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertScreen(
    navController: NavController,
    vm: AlertsViewModel = hiltViewModel()
) {
    /* ------------ UI state ------------ */
    val alerts       by vm.alerts
    val refreshing    = vm.isRefreshing                     // ← NEW
    val swipeState    = rememberSwipeRefreshState(refreshing) // ← NEW
    val filters       = listOf("All", "Likes", "Comments")
    var selectedFilter by remember { mutableStateOf(filters.first()) }
    var selectedTab    by remember { mutableStateOf("Alerts") }

    /* ---- newest first, then group for section headers ---- */
    val grouped = remember(alerts) {
        alerts
            .sortedByDescending { it.createdAt }            // ← NEW (latest on top)
            .groupBy { dto ->
                DateTimeUtils.formatRelative(dto.createdAt).let { rel ->
                    when {
                        rel.contains("just now")||rel.contains("m ago") || rel.contains("h ago") -> "Today"
                        rel == "yesterday"                             -> "Yesterday"
                        else                                           -> "Earlier"
                    }
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Alerts") },
                navigationIcon = {
                    IconButton({ navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton({ /* TODO settings */ }) {
                        Icon(Icons.Default.Settings, null, tint = HKUSTPrimary)
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationRedesign(
                selectedTab  = selectedTab,
                onTabSelected = { tab ->
                    selectedTab = tab
                    navController.navigate(tab) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        restoreState = true
                    }
                }
            )
        }
    ) { p ->
        /* ------------ pull-to-refresh wrapper ------------ */
        SwipeRefresh(                                       // ← NEW
            state     = swipeState,
            onRefresh = { vm.refresh() }
        ) {
            Column(
                Modifier
                    .padding(p)
                    .fillMaxSize()
            ) {
                /* -------- filter chips -------- */
                Row(
                    Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 12.dp)
                ) {
                    Spacer(Modifier.width(15.dp))
                    filters.forEach { f ->
                        FilterChip(
                            text       = f,
                            isSelected = f == selectedFilter,
                            onClick    = { selectedFilter = f },
                            modifier   = Modifier.padding(end = 10.dp)
                        )
                    }
                }

                /* -------- alert list -------- */
                Column(
                    Modifier
                        .verticalScroll(rememberScrollState())
                        .fillMaxWidth()
                ) {
                    grouped
                        .toSortedMap(compareBy {             // Today → Yesterday → Earlier
                            when (it) { "Today" -> 0;"just now" ->0; "Yesterday" -> 1; else -> 2 }
                        })
                        .forEach { (section, list) ->

                            val rows = list
                                .filter { dto ->
                                    when (selectedFilter) {
                                        "All"      -> true
                                        "Likes"    -> dto.type == "LIKE"
                                        "Comments" -> dto.type == "COMMENT"
                                        else       -> true
                                    }
                                }
                                /* still newest-first because we sorted before grouping */
                                .map { dto ->
                                    AlertUiModel(
                                        type     = when (dto.type) {
                                            "LIKE"    -> AlertType.LIKE
                                            "COMMENT" -> AlertType.COMMENT
                                            "MENTION" -> AlertType.MENTION
                                            else      -> AlertType.ANNOUNCEMENT
                                        },
                                        source   = dto.source,
                                        time     = DateTimeUtils.formatRelative(dto.createdAt),
                                        message  = dto.snippet,
                                        context  = "",
                                        isUnread = dto.isUnread,
                                        onClick  = { vm.markSeen(dto.id) }
                                    )
                                }

                            if (rows.isNotEmpty()) {
                                AlertGroup(section, rows)
                                Spacer(Modifier.height(15.dp))
                            }
                        }
                }
            }
        }
    }
}

/* ───────────────────────── */
/*  Filter chip              */
/* ───────────────────────── */

@Composable
private fun FilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .background(
                if (isSelected) HKUSTPrimary.copy(alpha = 0.1f)
                else Color.Transparent
            )
            .padding(horizontal = 15.dp, vertical = 6.dp)
    ) {
        Text(
            text       = text,
            color      = if (isSelected) HKUSTPrimary else TextSecondaryColor,
            fontWeight = FontWeight.SemiBold,
            fontSize   = 14.sp
        )
    }
}

/* ───────────────────────── */
/*  Alert row & helpers      */
/* ───────────────────────── */

private val UnreadBackground = Color(0xFFEEF4FF)   // keep local

data class AlertUiModel(
    val type    : AlertType,
    val source  : String,
    val time    : String,
    val message : String,
    val context : String,
    val isUnread: Boolean,
    val onClick : () -> Unit
)

@Composable
private fun AlertGroup(title: String, alerts: List<AlertUiModel>) {
    Column {
        Text(
            text = title,
            color = TextSecondaryColor,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
        )
        Column(Modifier.border(1.dp, BorderColor)) {
            alerts.forEach {
                AlertRow(it)
                Divider(thickness = 1.dp, color = BorderColor)
            }
        }
    }
}

@Composable
private fun AlertRow(alert: AlertUiModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = alert.onClick)
            .background(if (alert.isUnread) UnreadBackground else BackgroundColor)
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        /* icon */
        Box(
            modifier = Modifier
                .size(45.dp)
                .clip(CircleShape)
                .background(alert.type.backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(alert.type.icon, null, tint = alert.type.iconColor)
        }

        Spacer(Modifier.width(15.dp))

        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(alert.source, style = MaterialTheme.typography.bodyMedium)
                if (alert.isUnread) {
                    Spacer(Modifier.width(6.dp))
                    Box(
                        Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(HKUSTPrimary)
                    )
                }
                Spacer(Modifier.weight(1f))
                Text(alert.time, style = MaterialTheme.typography.labelSmall, color = TextSecondaryColor)
            }
            Text(alert.message, style = MaterialTheme.typography.bodySmall, color = TextSecondaryColor)
            if (alert.context.isNotBlank()) {
                Text(
                    alert.context,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextLightColor,
                    maxLines = 2
                )
            }
        }
    }
}

enum class AlertType(
    val icon: ImageVector,
    val iconColor: Color,
    val backgroundColor: Color
) {
    MENTION(
        Icons.Default.Person,
        HKUSTPrimary,
        HKUSTPrimary.copy(alpha = 0.1f)
    ),
    LIKE(
        Icons.Default.Favorite,
        HKUSTAccent,
        HKUSTAccent.copy(alpha = 0.1f)
    ),
    COMMENT(
        Icons.Default.Menu,
        HKUSTHighlight,
        HKUSTHighlight.copy(alpha = 0.1f)
    ),
    ANNOUNCEMENT(
        Icons.Default.Notifications,
        Color(0xFF29954A),
        Color(0xFF29954A).copy(alpha = 0.1f)
    )
}
