package com.entropypp.btcpowerlaw.widget

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionRunCallback
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
        val fearAndGreedIndex = prefs.getInt("fearAndGreedIndex", 0)

        provideContent {
            PriceWidgetContent(currentPrice, fairPrice, fearAndGreedIndex)
        }
    }

    @SuppressLint("RestrictedApi")
    @Composable
    private fun PriceWidgetContent(currentPrice: Double, fairPrice: Double, fearAndGreedIndex: Int) {
        val ratioToFair = if (fairPrice > 0) currentPrice / fairPrice else 1.0
        val green = ColorProvider(R.color.green)
        val lime = ColorProvider(R.color.lime)
        val yellow = ColorProvider(R.color.yellow)
        val orange = ColorProvider(R.color.orange)
        val red = ColorProvider(R.color.red)

        val (buyLabel, buyColor) = when {
            ratioToFair <= 0.42 -> "EXTREME BUY [%.2f]".format(ratioToFair) to green
            ratioToFair <= 0.60 -> "STRONG BUY [%.2f]".format(ratioToFair) to lime
            ratioToFair <= 0.75 -> "BUY [%.2f]".format(ratioToFair) to yellow
            ratioToFair <= 1.00 -> "FAIR VALUE [%.2f]".format(ratioToFair) to orange
            else -> "OVERVALUED [%.2f]".format(ratioToFair) to red
        }

        val (fngLabel, fngColor) = when {
            fearAndGreedIndex <= 25 -> "EXTREME FEAR [$fearAndGreedIndex]" to red
            fearAndGreedIndex <= 46 -> "FEAR [$fearAndGreedIndex]" to orange
            fearAndGreedIndex <= 54 -> "NEUTRAL [$fearAndGreedIndex]" to yellow
            fearAndGreedIndex <= 75 -> "GREED [$fearAndGreedIndex]" to lime
            else -> "EXTREME GREED [$fearAndGreedIndex]" to green
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
                .padding(4.dp)
                .clickable(actionRunCallback<RefreshAction>()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

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
            Spacer(modifier = GlanceModifier.height(1.dp))
            Text(
                text = price,
                style = TextStyle(
                    color = ColorProvider(BtcOrange),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            )
            Spacer(modifier = GlanceModifier.height(1.dp))
            Text(
                text = fngLabel,
                style = TextStyle(
                    color = fngColor,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                maxLines = 1
            )

        }
    }
}
