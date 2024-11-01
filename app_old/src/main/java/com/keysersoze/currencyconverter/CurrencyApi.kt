package com.keysersoze.currencyconverter

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface CurrencyApi {
    @GET("latest/{baseCurrency}")
    fun getCurrencyData(@Path("baseCurrency") baseCurrency: String): Call<Currency>
}