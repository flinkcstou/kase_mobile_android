package kz.kase.terminal.core

import io.reactivex.Flowable
import io.reactivex.subjects.BehaviorSubject

class MainViewModel {
    companion object {
        val share = MainViewModel()
    }
    var currentView = BehaviorSubject.create<Any>()
}