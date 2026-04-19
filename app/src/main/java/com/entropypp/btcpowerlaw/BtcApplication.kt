package com.entropypp.btcpowerlaw

import android.app.Application
import com.entropypp.btcpowerlaw.widget.BTCWidgetWorker

class BtcApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        BTCWidgetWorker.enqueue(this)
        BTCWidgetWorker.enqueueImmediate(this)
    }
}

