package kz.kase.terminal.entities

import kz.kase.iris.model.IrisApiBase
import kz.kase.iris.mqtt.IrisApiUtils
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*


//
//  RowValue.swift
//  KASE
//
//  Created by Andrey Kolpachkov on 17/04/2019.
//  Copyright © 2019 АО "Казахстанская фондовая биржа". All rights reserved.
//
class RowValue {
    private var _round : Int = 2
    private var _type: NumberType
    private var _intValue: Int
    private var _doubleValue: Double
    private var _stringValue: String

    enum class NumberType {
        int,
        double,
        string
    }
    constructor(value: Int){
        _type = NumberType.int
        _intValue = value
        _stringValue = value.string()
        _doubleValue = value.toDouble()
    }
    constructor(value: Double){
        _type = NumberType.double
        _intValue = value.toInt()
        _doubleValue = value
        _stringValue = value.string(_round)
    }
    constructor(value: Double, roundBy: Int){
        _round = roundBy
        _type = NumberType.double
        _intValue = value.toInt()
        _doubleValue = value
        _stringValue = value.string(_round)
    }
    constructor(value: String){
        _type = NumberType.string
        _intValue = 0
        _doubleValue = 0.0
        _stringValue = value
    }
    constructor(value: IrisApiBase.Decimal){
        _round = value.scale
        _type = NumberType.double
        _doubleValue = IrisApiUtils.fromDecimal(value).toDouble()
        _intValue = _doubleValue.toInt()
        _stringValue = _doubleValue.string(_round)
    }

//    @BindingAdapter("android:text")
//    fun setText(view: TextView, last: String) {
//        view.text = this.string
//    }


    val string: String
    get() {
        when (_type) {
            NumberType.int -> return _intValue.string()
            NumberType.double -> return _doubleValue.string(_round)
            NumberType.string -> return _stringValue
        }
    }
    val valueType: NumberType
        get() {
            return _type
        }
    val intValue: Int
        get() {
            return _intValue
        }
    val doubleValue: Double
        get() {
            return _doubleValue
        }
    val stringValue: String
        get() {
            return _stringValue
        }
}
fun Int.string(): String{
    val df = DecimalFormat("#,##0.00")
    df.decimalFormatSymbols = DecimalFormatSymbols(Locale.getDefault())
    df.minimumFractionDigits = 0;
    df.maximumFractionDigits = 0;
    return df.format(this);
}
fun Double.string(roundBy: Int): String{
    val df = DecimalFormat("#,##0.00")
    df.decimalFormatSymbols = DecimalFormatSymbols(Locale.getDefault())
    df.minimumFractionDigits = roundBy;
    df.maximumFractionDigits = roundBy;
    return df.format(this);
}
