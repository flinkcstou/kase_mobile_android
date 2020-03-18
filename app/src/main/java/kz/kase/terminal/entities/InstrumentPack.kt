package kz.kase.terminal.entities

import kz.kase.terminal.other.TableType

class InstrumentPack (var type: TableType, var symbols : List<String>){
    var instruments : List<InstrumentItem>? = null
    //var requestArc = 7 days 30 days....
}