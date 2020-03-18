package kz.kase.terminal.viewmodel

import android.util.Log
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject
import kz.kase.terminal.entities.InstrumentPack
import kz.kase.terminal.other.QuoteItem
import kz.kase.terminal.provider.StorageProvider
import kz.kase.terminal.other.TableType

class InstrumentViewModel() {
    private val TAG = this.javaClass.simpleName

    private val dispose = CompositeDisposable()
    private val tablesInUse = arrayOf(TableType.FAVORITE_SHARES, TableType.FAVORITE_BONDS, TableType.INDEX_MAIN , TableType.SHARE_MAIN , TableType.CURRENCY_MAIN , TableType.SHARES_PAGE , TableType.BONDS_PAGE)

    var isFavoriteShareArc = false
    var isFavoriteBoundsArc = false
    var isPageShareArc = false
    var isPageBondsArc = false


    companion object {
        val share = InstrumentViewModel()
    }
    //in
    public var subjectUpdate = PublishSubject.create<InstrumentPack>()
    public var subjectUpdateFavShares = PublishSubject.create<Boolean>()
    public var subjectUpdateFavBounds = PublishSubject.create<Boolean>()
    public var subjectUpdateTable = PublishSubject.create<TableType>()
    public var subjectTapEvent = PublishSubject.create<String>()
    public var subjectNewsTapEvent = PublishSubject.create<String>()
    public var subjectQuoteTapEvent = PublishSubject.create<QuoteItem>()
    public var subjectDealTapEvent = PublishSubject.create<String>()
    public var subjectFavoriteInstrum = PublishSubject.create<Boolean>()
    //out
    public var subjectRequest = PublishSubject.create<InstrumentPack>()
    public var subjectResponse = PublishSubject.create<InstrumentPack>()


    init {
        dispose.add(subjectUpdateFavShares.subscribeBy( onNext = { isArc ->
            isFavoriteShareArc = isArc
            var type = TableType.REALTIME
            if (isArc){
                type = TableType.SHARES_PAGE_ARC
            }
            val list = StorageProvider.tableDict[TableType.FAVORITE_SHARES]!!
            subjectRequest.onNext(InstrumentPack(type, list))
        }))
        dispose.add(subjectUpdateFavBounds.subscribeBy( onNext = { isArc ->
            isFavoriteBoundsArc = isArc
            var type = TableType.REALTIME
            if (isArc){
                type = TableType.SHARES_PAGE_ARC
            }
            val list = StorageProvider.tableDict[TableType.FAVORITE_BONDS]!!
            subjectRequest.onNext(InstrumentPack(type, list))
        }))


        dispose.add(subjectUpdate.subscribeBy(
                onNext = {result ->
                    Log.d(TAG, "subjectUpdate.subscribeBy:" + result.type)
                    //if (tablesInUse.contains())
                    when(result.type){
                        TableType.INIT -> {
                            Log.d(TAG, "INIT Loading completed")
                        }
                        TableType.REALTIME ->{
                            Log.d(TAG, "REALTIME size: " + result.instruments?.size)
                            for (table in tablesInUse) {
                                val list = StorageProvider.tableDict[table]!!
                                val array = result.instruments!!.filter { list.contains(it.symbol) }
                                if (array.isNotEmpty()) {
                                    subjectRequest.onNext(InstrumentPack(table, list))
                                }
                            }
                        }
                        else -> {
                            Log.d(TAG, "subjectResponse: " + result.type)
                            subjectResponse.onNext(result)
                        }
                    }

                },
                onError = {
                    Log.e(TAG, it.message)
                }
        ))
        dispose.add(subjectUpdateTable.subscribeBy(
                onNext = {table ->
                    Log.d(TAG, "Update table:" + table.toString())
                    val list = StorageProvider.tableDict[table]!!
                    subjectRequest.onNext(InstrumentPack(table, list))
                },
                onError = {
                    Log.e(TAG, it.message)
                }
        ))

    }
}