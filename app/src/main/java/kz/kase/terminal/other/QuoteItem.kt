package kz.kase.terminal.other

class QuoteItem(var qtyBid : Int? = null, var qtyAsk : Int? = null, var price : Double) {

    val qty : Int
        get(){
            if (qtyBid != null)
                return qtyBid!!
            else if (qtyAsk != null)
                return qtyAsk!!
            return 0
        }
}