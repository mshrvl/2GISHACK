package com.example.data

import com.example.data.RetrofitClient.createOkHttpClient
import com.example.data.repository.MapRepository
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {
    single { createOkHttpClient() }
    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl("https://giicoo.ru/api/")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    single<MapApi> {
        get<Retrofit>().create(MapApi::class.java)
    }

    factoryOf(::MapRepository)

}
