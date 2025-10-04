package com.example.map.di

import com.example.map.screen.MapViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val mapModule = module {
    viewModelOf(::MapViewModel)
}