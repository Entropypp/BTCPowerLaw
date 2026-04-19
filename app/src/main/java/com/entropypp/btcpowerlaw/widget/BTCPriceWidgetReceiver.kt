package com.entropypp.btcpowerlaw.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

class BTCPriceWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = BTCPriceWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        BTCWidgetWorker.enqueue(context)
        BTCWidgetWorker.enqueueImmediate(context)
    }
}
