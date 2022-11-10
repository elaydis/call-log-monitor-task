package io.github.elaydis.calllogmonitor.utils

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

fun <T> Observable<T>.scheduleIO() = this
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

fun Disposable.disposedBy(compositeDisposable: CompositeDisposable) {
    compositeDisposable.add(this)
}

fun Long.formatToHoursMinutesAndSeconds(): String {
    val hours: Long = TimeUnit.MILLISECONDS.toHours(this)
    val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(this) % 60
    val seconds: Long = TimeUnit.MILLISECONDS.toSeconds(this) % 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}