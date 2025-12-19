package com.example.otigoapp.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // EMULATOR İÇİN ADRES: http://10.0.2.2:8080/
    // GERÇEK TELEFON İÇİN: http://192.168.1.35:8080/ (Bilgisayarının IP'si)
    private const val BASE_URL = "http://192.168.1.114:9090/"
    val api: OtigoApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OtigoApiService::class.java)
    }
}