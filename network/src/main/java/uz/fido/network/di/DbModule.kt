package uz.fido.network.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uz.fido.network.domain.model.payment.location.LocalPayment
import uz.fido.network.domain.model.payment.location.LocalPaymentRoom
import uz.fido.network.room.LocalPaymentDao
import uz.fido.network.room.LocalPaymentDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DbModule {

    @Provides
    @Singleton
    fun provide(@ApplicationContext context: Context) = Room.databaseBuilder(
        context, LocalPaymentDatabase::class.java, "local.db")
        .build()

    @Provides
    @Singleton
    fun provideDao(db: LocalPaymentDatabase) = db.localPaymentDao()

    @Provides
    @Singleton
    fun provideTypeDao(db: LocalPaymentDatabase) = db.localPaymentTypeDao()

    @Provides
    fun provideEntity() = LocalPayment()
}