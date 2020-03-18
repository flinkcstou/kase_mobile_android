package kz.kase.terminal.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.FrameLayout
import android.widget.NumberPicker
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.cell_news.view.*
import kotlinx.android.synthetic.main.dialog_new_order.*
import kotlinx.android.synthetic.main.fragment_details.*
import kotlinx.android.synthetic.main.fragment_quote.*
import kz.kase.iris.mqtt.MQTTDataProvider
import kz.kase.terminal.R
import kz.kase.terminal.adapter.QuoteAdapter
import kz.kase.terminal.entities.InstrumentItem
import kz.kase.terminal.entities.string
import kz.kase.terminal.viewmodel.InstrumentViewModel
import java.text.SimpleDateFormat
import java.util.*





class NewOrderDialog() : DialogFragment() {
    val TAG = this.javaClass.simpleName
    private var instrument: InstrumentItem? = null
    private var isMarket = false
    private var isSell = true
    private var priceValue = 0.0
    private var qtyValue = 0
    companion object {
        fun newInstance(item: String, price: Double, qty: Int, directBid: Boolean): NewOrderDialog {
            val fragment = NewOrderDialog()
            val bundle = Bundle()
            bundle.putString("symbol", item)
            bundle.putDouble("price", price)
            bundle.putInt("qty", qty)
            fragment.setArguments(bundle)
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setStyle(STYLE_NO_FRAME, R.style.AppTheme);
    }
    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = kz.kase.terminal.databinding.DialogNewOrderBinding.inflate(inflater, container, false)
        val symbol = arguments?.getString("symbol", "")
        instrument = MQTTDataProvider.share.getInstrment(symbol)
        binding.instrument = instrument

        isCancelable = true
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return binding.root
    }
    fun showExpireSelection(){
        val picker = DatePicker(context)
        val cal = Calendar.getInstance()
        cal.time = Date()
        cal.add(Calendar.DATE, 1)
        picker.minDate = cal.timeInMillis
        cal.add(Calendar.YEAR, 2)
        picker.maxDate = cal.timeInMillis
        val layout = FrameLayout(context!!)
        layout.addView(picker, FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER))

        AlertDialog.Builder(context!!)
                .setTitle(getString(R.string.expire_date))
                .setView(layout)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                    val c = Calendar.getInstance()
                    c.set(picker.year, picker.month, picker.dayOfMonth)
                    expire.setText(dateFormat.format(c.time))
                }
                .setNegativeButton(android.R.string.cancel, null)
                .show()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        priceValue = arguments?.getDouble("price", 0.0)!!
        qtyValue = arguments?.getInt("qty", 0)!!
        price.setText(priceValue.string(2))
        qty.setText(qtyValue.string())
        close.setOnClickListener{dismiss()}
        cancel.setOnClickListener{dismiss()}
        send.setOnClickListener{dismiss()}
        main_back.setOnClickListener{dismiss()}

        limit_left.setOnClickListener{switchType()}
        limit_right.setOnClickListener{switchType()}
        direct_left.setOnClickListener{switchDirect()}
        direct_right.setOnClickListener{switchDirect()}

        price_add.setOnClickListener{changePrice(true)}
        price_sub.setOnClickListener{changePrice(false)}

        qty_add.setOnClickListener{changeQty(true)}
        qty_sub.setOnClickListener{changeQty(false)}

        expire_add.setOnClickListener{changeDate(true)}
        expire_sub.setOnClickListener{changeDate(false)}

        expire.setText("")
        expire.setOnClickListener{showExpireSelection()}
        calcVolume()
    }
    private fun changePrice(isAdd: Boolean){
        if (isAdd){
            priceValue += 0.01
        }else{
            priceValue -= 0.01
        }
        price.setText(priceValue.string(2))
        calcVolume()
    }
    private fun changeQty(isAdd: Boolean){
        if (isAdd){
            qtyValue += 1
        }else{
            qtyValue -= 1
        }
        qty.setText(qtyValue.string())
        calcVolume()
    }
    private fun changeDate(isAdd: Boolean){

    }
    private fun calcVolume(){
        result.text = (priceValue * qtyValue).string(2)
    }
    private fun switchDirect(){
        isSell = !isSell
        if (isSell) {
            block_direct.background = ColorDrawable(context!!.getColor(R.color.colorOrderSell))
            direct.text = getString(R.string.selling)
        }else{
            block_direct.background = ColorDrawable(context!!.getColor(R.color.colorOrderBuy))
            direct.text = getString(R.string.buying)
        }
    }
    private fun switchType(){
        isMarket = !isMarket
        if(isMarket){
            block_price.visibility = View.GONE
            limit_market.text = getString(R.string.market)
        } else {
            block_price.visibility = View.VISIBLE
            limit_market.text = getString(R.string.limit)
        }

    }
}