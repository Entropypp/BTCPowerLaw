package com.entropypp.btcpowerlaw

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Refresh
import androidx.glance.unit.ColorProvider
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.colorResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.entropypp.btcpowerlaw.domain.model.BtcMetrics
import com.entropypp.btcpowerlaw.ui.main.MainUiState
import com.entropypp.btcpowerlaw.ui.main.MainViewModel
import com.entropypp.btcpowerlaw.ui.theme.BTCPowerLawTheme
import com.entropypp.btcpowerlaw.ui.theme.BtcOrange
import com.entropypp.btcpowerlaw.ui.theme.RajdhaniFontFamily
import com.entropypp.btcpowerlaw.util.PowerLawCalculator
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*

class MainActivity : ComponentActivity() {


    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            BTCPowerLawTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    val green = colorResource(R.color.green)
    val lime = colorResource(R.color.lime)
    val yellow = colorResource(R.color.yellow)
    val orange = colorResource(R.color.orange)
    val red = colorResource(R.color.red)
    val white = colorResource(R.color.white)
    val grey = colorResource(R.color.grey)
    val purple = colorResource(R.color.purple)
    val uiState by viewModel.uiState.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val date = Instant.ofEpochMilli(it).atZone(ZoneId.of("UTC")).toLocalDate()
                        viewModel.onDateSelected(date)
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Bitcoin Power Law", fontFamily = RajdhaniFontFamily, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { 
                        viewModel.refreshMetrics()
                        scope.launch {
                            snackbarHostState.showSnackbar("Refreshing data...")
                        }
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = orange
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // Header with Date Picker and Block Height
            val successState = uiState as? MainUiState.Success
            val latestBlockHeight = successState?.metrics?.blockHeight ?: 0L
            val today = LocalDate.now()
            val isFuture = selectedDate.isAfter(today)
            
            val blockDisplay = if (latestBlockHeight == 0L) "..." else {
                if (isFuture) {
                    latestBlockHeight.toString()
                } else {
                    val daysDiff = ChronoUnit.DAYS.between(today, selectedDate)
                    (latestBlockHeight + daysDiff * 144).toString()
                }
            }
            val blockColor = if (isFuture) grey else orange

            Row(
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 0.dp)
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Date Picker Card
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF121212))
                        .border(1.dp, Color.DarkGray, RoundedCornerShape(16.dp))
                        .clickable { showDatePicker = true }
                        .padding( vertical = 8.dp, horizontal = 16.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = Color.LightGray,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "DATE",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.Gray,
                            fontFamily = RajdhaniFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = selectedDate.toString(),
                        color = Color.White,
                        fontFamily = RajdhaniFontFamily,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Block Height Card
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF121212))
                        .border(1.dp, Color.DarkGray, RoundedCornerShape(16.dp))
                        .padding( vertical = 8.dp, horizontal = 16.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "BLOCK HEIGHT",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.Gray,
                        fontFamily = RajdhaniFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = blockDisplay,
                        color = blockColor,
                        fontFamily = RajdhaniFontFamily,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when (val state = uiState) {
                    is MainUiState.Loading -> {
                        CircularProgressIndicator(
                            color = orange,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    is MainUiState.Success -> {
                        MetricsContent(state.metrics, selectedDate)
                    }
                    is MainUiState.Error -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center).padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "Error: ${state.message}", color = Color.Red, fontFamily = RajdhaniFontFamily)
                            Button(onClick = { viewModel.refreshMetrics() }) {
                                Text("Retry", fontFamily = RajdhaniFontFamily)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetricsContent(metrics: BtcMetrics, selectedDate: LocalDate) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US).apply {
        maximumFractionDigits = 0
    }
    
    val today = LocalDate.now()
    val isFuture = selectedDate.isAfter(today)
    val isPast = selectedDate.isBefore(today)
    val green = colorResource(R.color.green)
    val lime = colorResource(R.color.lime)
    val yellow = colorResource(R.color.yellow)
    val orange = colorResource(R.color.orange)
    val red = colorResource(R.color.red)
    val white = colorResource(R.color.white)
    val grey = colorResource(R.color.grey)
    val purple = colorResource(R.color.purple)
    val blue = colorResource(R.color.blue)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 2x2 Grid of Cards
        Row(modifier = Modifier.fillMaxWidth()) {


            val actualPriceValue = metrics.currentPrice
            val actualPriceColor = if (isFuture) grey else orange
            val actualPriceLabel = "ACTUAL PRICE"
            
            MetricCard(
                label = actualPriceLabel,
                value = currencyFormatter.format(actualPriceValue),
                color = actualPriceColor,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            MetricCard(
                label = "ALL TIME HIGH",
                value = currencyFormatter.format(metrics.ath),
                color = yellow,
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(modifier = Modifier.fillMaxWidth()) {
            MetricCard(
                label = "FAIR VALUE",
                value = currencyFormatter.format(metrics.fairPrice),
                color = blue,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            MetricCard(
                label = "FLOOR [0.398X]",
                value = currencyFormatter.format(metrics.fairPrice * 0.398),
                color = green,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            MetricCard(
                label = "CEILING [2.512X]",
                value = currencyFormatter.format(metrics.fairPrice * 2.512),
                color = red,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            val drawdownColor = when {
                isFuture -> grey
                metrics.drawdown <= 5.0 -> green
                metrics.drawdown <= 10.0 -> lime
                metrics.drawdown <= 20.0 -> yellow
                metrics.drawdown <= 30.0 -> orange
                else -> red
            }

            MetricCard(
                label = "DRAWDOWN",
                value = "%.2f".format(metrics.drawdown),
                color = drawdownColor,
                modifier = Modifier.weight(1f),
                prefix = "%"
            )
        }

        Spacer(modifier = Modifier.height(16.dp)) // Increased space before gauge cards

        // Fear & Greed Card calculations
        val index = metrics.fearAndGreedIndex
        val (fngLabel, fngDefaultColor) = when {
            index <= 24 -> "EXTREME FEAR [$index]" to red
            index <= 46 -> "FEAR [$index]" to orange
            index <= 49 -> "NEUTRAL [$index]" to yellow
            index<= 74 -> "GREED [$index]" to lime
            else -> "EXTREME GREED [$index]" to green
        }
        val fngColor = if (isFuture) Color.Gray else fngDefaultColor

        // Price Rating Card (Buy Stars) calculations
        val effectiveFairPrice = if (isFuture) PowerLawCalculator.calculateFairPrice(today) else metrics.fairPrice
        val ratioToFair = if (effectiveFairPrice > 0) metrics.currentPrice / effectiveFairPrice else 1.0

        val (buyLabel, buyDefaultColor) = when {
            ratioToFair <= 0.398 -> "EXTREME BUY [%.2f]".format(ratioToFair) to green
            ratioToFair <= 0.60 -> "STRONG BUY [%.2f]".format(ratioToFair) to lime
            ratioToFair <= 0.75 -> "BUY [%.2f]".format(ratioToFair) to yellow
            ratioToFair <= 1.00 -> "FAIR VALUE [%.2f]".format(ratioToFair) to orange
            else -> "OVERVALUED [%.2f]".format(ratioToFair) to red
        }
        val buyColor = if (isFuture) grey else buyDefaultColor

        // DCA Accumulation Gauge Card
        DCAAccumulationCard(
            ratioToFair = ratioToFair,
            label = buyLabel,
            color = buyColor,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Fear & Greed Gauge Card (Moved to bottom)
        FearAndGreedCard(
            index = metrics.fearAndGreedIndex,
            label = fngLabel,
            color = fngColor,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun FearAndGreedCard(
    index: Int,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    val green = colorResource(R.color.green)
    val lime = colorResource(R.color.lime)
    val yellow = colorResource(R.color.yellow)
    val orange = colorResource(R.color.orange)
    val red = colorResource(R.color.red)
    val white = colorResource(R.color.white)
    val grey = colorResource(R.color.grey)
    val purple = colorResource(R.color.purple)

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF121212))
            .border(1.dp, Color.DarkGray, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // Header Row: Title and Rating Label
        Row(modifier = Modifier.fillMaxWidth().height(26.dp)){
            Text(
                text = "FEAR & GREED",
                modifier = Modifier.weight(1f),
                style = TextStyle(
                    color = Color.Gray,
                    fontSize = 14.sp,
                    fontFamily = RajdhaniFontFamily,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start
                )
            )
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                style = TextStyle(
                    color = color,
                    fontSize = 14.sp,
                    fontFamily = RajdhaniFontFamily,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 1. Indicator Arrow (Linear Mapping 0-100 using sentiment ranges)
        val activeBox = when {
            index <= 25 -> 0
            index <= 46 -> 1
            index <= 54 -> 2
            index <= 75 -> 3
            else -> 4
        }
        val offsetInBox = when (activeBox) {
            0 -> index.toFloat() / 24f
            1 -> (index - 25).toFloat() / (46f - 25f)
            2 -> (index - 47).toFloat() / (49f - 47f)
            3 -> (index - 50).toFloat() / (74f - 50f)
            else -> (index - 75).toFloat() / (100f - 75f)
        }.coerceIn(0f, 1f)

        Row(modifier = Modifier.fillMaxWidth().height(16.dp)) {
            for (i in 0 until 5) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    contentAlignment = if (i == activeBox) {
                        when {
                            offsetInBox < 0.25f -> Alignment.CenterStart
                            offsetInBox > 0.75f -> Alignment.CenterEnd
                            else -> Alignment.Center
                        }
                    } else Alignment.Center
                ) {
                    if (i == activeBox) {
                        Canvas(modifier = Modifier.size(16.dp)) {
                            val path = androidx.compose.ui.graphics.Path().apply {
                                moveTo(size.width / 2, size.height)
                                lineTo(0f, 0f)
                                lineTo(size.width, 0f)
                                close()
                            }
                            drawPath(path, Color(0xFF888888))
                        }
                    }
                }
            }
        }

        // 2. The Color Bar (5 segment Row)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .clip(RoundedCornerShape(4.dp))
        ) {
            val bands = listOf(
                red to if (index in 0..24) "$index" else "0-25",
                orange to if (index in 25..46) "$index" else "26-46",
                yellow to if (index in 47..49) "$index" else "47-54",
                lime to if (index in 50..74) "$index" else "55-75",
                green to if (index in 75..100) "$index" else "76-100"
            )
            bands.forEach { (bandColor, bandLabel) ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(bandColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = bandLabel,
                        style = TextStyle(
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontFamily = RajdhaniFontFamily,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false
                            ),
                            lineHeightStyle = LineHeightStyle(
                                alignment = LineHeightStyle.Alignment.Center,
                                trim = LineHeightStyle.Trim.None
                            )
                        ),
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
fun DCAAccumulationCard(
    ratioToFair: Double,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    val green = colorResource(R.color.green)
    val lime = colorResource(R.color.lime)
    val yellow = colorResource(R.color.yellow)
    val orange = colorResource(R.color.orange)
    val red = colorResource(R.color.red)
    val white = colorResource(R.color.white)
    val grey = colorResource(R.color.grey)
    val purple = colorResource(R.color.purple)

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF121212))
            .border(1.dp, Color.DarkGray, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // Header Row: Title and Rating Label
        Row(modifier = Modifier.fillMaxWidth().height(26.dp)){
            Text(
                text = "FAIR VALUE RATIO",
                modifier = Modifier.weight(1f),
                style = TextStyle(
                    color = Color.Gray,
                    fontSize = 14.sp,
                    fontFamily = RajdhaniFontFamily,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start
                )
            )
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                style = TextStyle(
                    color = color,
                    fontSize = 14.sp,
                    fontFamily = RajdhaniFontFamily,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 1. Indicator Arrow (Piecewise linear mapping for accurate positioning)
        val progress = when {
            ratioToFair >= 1.0 -> 0.2f * ((1.1 - ratioToFair) / 0.1).toFloat()
            ratioToFair >= 0.75 -> 0.2f + 0.2f * ((1.0 - ratioToFair) / 0.25).toFloat()
            ratioToFair >= 0.60 -> 0.4f + 0.2f * ((0.75 - ratioToFair) / 0.15).toFloat()
            ratioToFair >= 0.42 -> 0.6f + 0.2f * ((0.60 - ratioToFair) / 0.18).toFloat()
            else -> 0.8f + 0.2f * ((0.42 - ratioToFair) / 0.12).toFloat()
        }.coerceIn(0f, 1f)

        BoxWithConstraints(modifier = Modifier.fillMaxWidth().height(16.dp)) {
            val fullWidth = maxWidth
            Canvas(
                modifier = Modifier
                    .size(16.dp)
                    .offset(x = (fullWidth * progress) - 8.dp)
            ) {
                val path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(size.width / 2, size.height)
                    lineTo(0f, 0f)
                    lineTo(size.width, 0f)
                    close()
                }
                drawPath(path, Color(0xFF888888))
            }
        }

        // 2. The Color Bar (5 segment Row)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .clip(RoundedCornerShape(4.dp))
        ) {
            val formattedRatio = "%.2f".format(ratioToFair)
            val bands = listOf(
                red to if (ratioToFair > 1.0) formattedRatio else "> 1.0",
                orange to if (ratioToFair <= 1.0 && ratioToFair > 0.75) formattedRatio else "0.75-1.0",
                yellow to if (ratioToFair <= 0.75 && ratioToFair > 0.60) formattedRatio else "0.6-0.75",
                lime to if (ratioToFair <= 0.60 && ratioToFair > 0.42) formattedRatio else "0.42-0.6",
                green to if (ratioToFair <= 0.42) formattedRatio else "< 0.42"
            )
            bands.forEach { (bandColor, bandLabel) ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(bandColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = bandLabel,
                        style = TextStyle(
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontFamily = RajdhaniFontFamily,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false
                            ),
                            lineHeightStyle = LineHeightStyle(
                                alignment = LineHeightStyle.Alignment.Center,
                                trim = LineHeightStyle.Trim.None
                            )
                        ),
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
fun MetricCard(label: String, value: String, color: Color, modifier: Modifier = Modifier, prefix: String = "$") {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF121212))
            .border(1.dp, Color.DarkGray, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = Color.Gray,
            fontFamily = RajdhaniFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
        var text = "$prefix${value.replace("$", "")}"
        if (prefix == "%") {
            text = "${value.replace("%", "")} $prefix"
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.headlineMedium,
            color = color,
            fontFamily = RajdhaniFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun MetricsPreview() {
    BTCPowerLawTheme {
        MetricsContent(
            BtcMetrics(
                currentPrice = 69195.0,
                ath = 126080.0,
                fairPrice = 131104.0,
                drawdown = 45.12,
                topZonePrice = 98327.0,
                floorPrice = 55064.0,
                fearAndGreedIndex = 13,
                fearAndGreedLabel = "Extreme Fear",
                satsPerVb = 15.0,
                blockHeight = 943864,
                buyRating = 4
            ),
            selectedDate = LocalDate.now()
        )
    }
}


