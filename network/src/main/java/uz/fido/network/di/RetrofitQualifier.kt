package uz.fido.network.di

import javax.inject.Qualifier

/**
 * Created by Husniddin Muhammad Amin on 02.05.2023
 * Tashkent, Uzbekistan.
 */

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MyIdRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BaseRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SocketRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SwapKeyRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MyIdOkhttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BaseOkhttpClient