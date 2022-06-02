package com.example.mptfood

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mptfood.Models.ContentClass
import com.example.mptfood.Models.Order
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import java.util.*
import kotlin.collections.ArrayList

class OrderAdapter(val context: Context, val orders: ArrayList<Order>,val listCart : ArrayList<ContentClass>): RecyclerView.Adapter<OrderAdapter.ViewHolder>() {


    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {

        var orderNumb = view.findViewById<TextView>(R.id.OrderID)
        var orderStatus = view.findViewById<TextView>(R.id.OrderStatus)
        var orderPrice = view.findViewById<TextView>(R.id.ProductPrice)
        var orderDate = view.findViewById<TextView>(R.id.OrderDate)
        var orderTime = view.findViewById<TextView>(R.id.OrderTime)

        var listOfOrder = view.findViewById<RecyclerView>(R.id.itemsOfOrder)

        fun bindView(order : Order){
            var orderStat = ""
            when(order.StatusID){
                1 -> orderStat = "Создан"
                2 -> orderStat = "Подтвержден"
                3 -> orderStat = "Готовится"
                4 -> orderStat = "Готов"
                5 -> orderStat = "Завершен"
            }
            orderNumb.text = "№ " + order.ID.toString()
            orderStatus.text = "Статус: " + orderStat
            orderPrice.text = "Сумма: " + order.Total.toString() + "₽"
            orderDate.text = order.Date!!.split("T")[0]
            orderTime.text = order.Date!!.split("T")[1].split(".")[0]

            itemView.setOnClickListener {
                if  (order.StatusID == 4){
                    val mDialogView = LayoutInflater.from(context).inflate(R.layout.qr_code_dialog, null)
                    val mBuilder = AlertDialog.Builder(context)
                        .setView(mDialogView)
                        .setTitle("Отправка сообщения")
                    val mAlertDialog = mBuilder.show()
                    val sendQR = mDialogView.findViewById<ImageView>(R.id.send_qr)
                    val closeBtn = mDialogView.findViewById<AppCompatButton>(R.id.close_dialog)
                    sendQR.setImageBitmap(generateQRCode(order.ID.toString()))
                    closeBtn.setOnClickListener {
                        mAlertDialog.dismiss()
                    }
                }
                else if (order.StatusID == 5){
                    Toast.makeText(context, "Заказ №${order.ID} завершен", Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(context, "Заказ №${order.ID} еще не готов", Toast.LENGTH_SHORT).show()
                }
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(context).inflate(R.layout.history_order_card, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(orders[position])
        var list = ArrayList<ContentClass>()
        for (i in 0.. listCart.size-1){
            if (listCart[i].orderId == orders[position].ID){
                list.add(
                    ContentClass(
                        listCart[i].product,
                        listCart[i].quantity,
                        listCart[i].orderId
                    )
                )
            }
        }
        holder.listOfOrder.layoutManager = LinearLayoutManager(context)
        holder.listOfOrder.setHasFixedSize(true)
        var adapter = ProductInOrderAdapter(context.applicationContext, list)
        holder.listOfOrder.adapter = adapter


    }

    override fun getItemCount(): Int = orders.size

    fun onUpdate(newListCart : ArrayList<Order>, newListContent : ArrayList<ContentClass>){
        orders.clear()
        orders.addAll(newListCart)
        listCart.clear()
        listCart.addAll(newListContent)
        notifyDataSetChanged()
    }


    private fun generateQRCode(data : String) : Bitmap {
        val writter = QRCodeWriter()
        try {
            val hints  = Hashtable<EncodeHintType, Any>()
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8")
            val bitMatrix = writter.encode(data, BarcodeFormat.QR_CODE, 512, 512, hints)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width){
                for (y in 0 until height){
                    bmp.setPixel(x,y, if (bitMatrix[x,y]) Color.BLACK else Color.WHITE)
                }
            }
            return bmp
        }catch (e : WriterException){
            Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()
            return R.mipmap.ic_launcher as Bitmap
        }

    }

}