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
import com.entropypp.btcpowerlaw.ui.theme.BtcOrange
import java.text.NumberFormat
import java.util.Locale

class BTCPriceWidget : GlanceAppWidget() {
    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val prefs = context.getSharedPreferences("btc_widget_prefs", Context.MODE_PRIVATE)
        val currentPrice = prefs.getFloat("currentPrice", 0f).toDouble()

        provideContent {
            PriceWidgetContent(currentPrice)
        }
    }

    @SuppressLint("RestrictedApi")
    @Composable
    private fun PriceWidgetContent(currentPrice: Double) {
        val orange = ColorProvider(BtcOrange)
        val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US).apply {
            maximumFractionDigits = 0
        }
        val price = currencyFormatter.format(currentPrice)
        val size = LocalSize.current
        val width = size.width.value
        val height = size.height.value

        val charWidthFactor = 0.70f // safe for bold currency text in dp

        val priceFontSize = (width / (price.length * charWidthFactor))
            .coerceAtMost(height * 0.90f)
            .sp
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(Color(0xFF121212))
                .cornerRadius(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = price,
                style = TextStyle(
                    color = orange,
                    fontSize = priceFontSize,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}
