package com.entropypp.btcpowerlaw.data.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitClient {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private fun createRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    val coinGeckoApi: CoinGeckoApi by lazy {
        createRetrofit("https://api.coingecko.com/").create(CoinGeckoApi::class.java)
    }

    val fearAndGreedApi: FearAndGreedApi by lazy {
        createRetrofit("https://api.alternative.me/").create(FearAndGreedApi::class.java)
    }

    val mempoolApi: MempoolApi by lazy {
        createRetrofit("https://mempool.space/").create(MempoolApi::class.java)
    }
}

