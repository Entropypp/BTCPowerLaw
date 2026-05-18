package com.entropypp.btcpowerlaw.widget

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.text.TextAlign
import androidx.glance.unit.ColorProvider
import com.entropypp.btcpowerlaw.R
import com.entropypp.btcpowerlaw.ui.theme.BtcOrange
import java.text.NumberFormat
import java.util.Locale

class BTCPriceWidget : GlanceAppWidget() {
    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val prefs = context.getSharedPreferences("btc_widget_prefs", Context.MODE_PRIVATE)
        val currentPrice = prefs.getLong("currentPrice", 0L).toDouble()
        val fairPrice = prefs.getLong("fairPrice", 0L).toDouble()

        provideContent {
            PriceWidgetContent(currentPrice, fairPrice)
        }
    }

    @SuppressLint("RestrictedApi")
    @Composable
    private fun PriceWidgetContent(currentPrice: Double, fairPrice: Double) {
        val ratioToFair = if (fairPrice > 0) currentPrice / fairPrice else 1.0
        val green = ColorProvider(R.color.green)
        val lime = ColorProvider(R.color.lime)
        val yellow = ColorProvider(R.color.yellow)
        val orange = ColorProvider(R.color.orange)
        val red = ColorProvider(R.color.red)

        val (buyLabel, buyColor) = when {
            ratioToFair <= 0.42 -> "EXTREME BUY [5X]" to green
            ratioToFair <= 0.60 -> "STRONG BUY [4X]" to lime
            ratioToFair <= 0.75 -> "BUY [3X]" to yellow
            ratioToFair <= 1.00 -> "FAIR VALUE [2X]" to orange
            ratioToFair <= 1.50 -> "OVERVALUED [1X]" to red
            else -> "OVERBOUGHT [1X]" to red
        }

        val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US).apply {
            maximumFractionDigits = 0
        }
        val price = currencyFormatter.format(currentPrice)

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(Color(0xFF121212))
                .cornerRadius(12.dp)
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = price,
                style = TextStyle(
                    color = ColorProvider(BtcOrange),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            )
            Spacer(modifier = GlanceModifier.height(2.dp))
            Text(
                text = buyLabel,
                style = TextStyle(
                    color = buyColor,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                maxLines = 1
            )
        }
    }
}
