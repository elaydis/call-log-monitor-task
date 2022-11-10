package io.github.elaydis.calllogmonitor.di

import android.app.Activity
import android.app.Service
import android.content.res.Resources
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.internal.NullSafeJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.components.SingletonComponent
import io.github.elaydis.calllogmonitor.data.server.Server
import io.github.elaydis.calllogmonitor.data.server.ServerImp
import io.github.elaydis.calllogmonitor.data.serverservice.ServerServicePresenter
import io.github.elaydis.calllogmonitor.data.serverservice.ServerServicePresenterImp
import io.github.elaydis.calllogmonitor.data.serverservice.ServerServiceService
import io.github.elaydis.calllogmonitor.data.serverservice.ServerServiceView
import io.github.elaydis.calllogmonitor.presentation.MainActivity
import io.github.elaydis.calllogmonitor.presentation.MainPresenter
import io.github.elaydis.calllogmonitor.presentation.MainPresenterImp
import io.github.elaydis.calllogmonitor.presentation.MainView
import io.github.elaydis.calllogmonitor.utils.Clock
import io.github.elaydis.calllogmonitor.utils.ClockImp
import io.github.elaydis.calllogmonitor.utils.TelephonyStateWrapper
import io.github.elaydis.calllogmonitor.utils.TelephonyStateWrapperImp
import java.util.*

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    abstract fun bindServer(serverImp: ServerImp): Server
}

@Module
@InstallIn(ActivityComponent::class)
abstract class ActivityModule {

    @Binds
    abstract fun bindMainPresenter(mainPresenterImp: MainPresenterImp): MainPresenter

    @Binds
    abstract fun bindMainActivity(mainActivity: MainActivity): MainView
}

@Module
@InstallIn(ServiceComponent::class)
abstract class ServiceModule {

    @Binds
    abstract fun bindServerPresenter(serverPresenterImp: ServerServicePresenterImp): ServerServicePresenter

    @Binds
    abstract fun bindServerService(serverService: ServerServiceService): ServerServiceView
}

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Provides
    fun bindMoshi(): Moshi {
        return Moshi.Builder()
            .add(Date::class.java, Rfc3339DateJsonAdapter())
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    fun bindTelephonyStateWrapper(): TelephonyStateWrapper {
        return TelephonyStateWrapperImp()
    }

    @Provides
    fun bindClock(): Clock {
        return ClockImp()
    }
}

@InstallIn(ActivityComponent::class)
@Module
object MainActivityModule {

    @Provides
    fun bindActivity(activity: Activity): MainActivity {
        return activity as MainActivity
    }

    @Provides
    fun bindResources(activity: Activity): Resources {
        return activity.resources
    }
}

@InstallIn(ServiceComponent::class)
@Module
object ServerServiceModule {

    @Provides
    fun bindService(service: Service): ServerServiceService {
        return service as ServerServiceService
    }
}