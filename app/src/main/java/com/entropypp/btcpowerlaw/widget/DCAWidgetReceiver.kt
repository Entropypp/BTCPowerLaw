package com.entropypp.btcpowerlaw.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

class DCAWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = DCAWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        BTCWidgetWorker.enqueue(context)
        BTCWidgetWorker.enqueueImmediate(context)
    }
}
