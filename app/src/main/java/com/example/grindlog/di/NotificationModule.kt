package com.example.grindlog.di

import com.example.grindlog.data.notification.NotificationSchedulerImpl
import com.example.grindlog.domain.notification.NotificationScheduler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationModule {

    @Binds
    @Singleton
    abstract fun bindNotificationScheduler(
        notificationSchedulerImpl: NotificationSchedulerImpl
    ): NotificationScheduler
}
