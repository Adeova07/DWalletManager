package com.genus.backenddannis.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.genus.backenddannis.R
import com.genus.backenddannis.data.entity.Pendapatan
import com.genus.backenddannis.data.entity.Pengeluaran
import com.genus.backenddannis.data.entity.Transfer
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class CombineAdapter(var transactionList: MutableList<Any>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var lastDateDisplayed: Date? = null
    private val dateFormat = SimpleDateFormat("d", Locale.getDefault())
    private val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recvutama, parent, false)
        return when (viewType) {
            TYPE_PENDAPATAN -> PendapatanViewHolder(view)
            TYPE_PENGELUARAN -> PengeluaranViewHolder(view)
            TYPE_TRANSFER -> TransferViewHolder(view)
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PendapatanViewHolder -> holder.bind(transactionList[position] as Pendapatan)
            is PengeluaranViewHolder -> holder.bind(transactionList[position] as Pengeluaran)
            is TransferViewHolder -> holder.bind(transactionList[position] as Transfer)
        }
    }

    override fun getItemCount(): Int = transactionList.size

    fun removeItemAt(position: Int) {
        transactionList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun filterByMonthAndYear(month: Int, year: Int) {
        transactionList = transactionList.filter { transaction ->
            if (transaction is Pengeluaran) {
                val cal = Calendar.getInstance()
                cal.time = transaction.tanggal
                cal.get(Calendar.MONTH) + 1 == month && cal.get(Calendar.YEAR) == year
            } else {
                true
            }
        }.toMutableList()
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (transactionList[position]) {
            is Pendapatan -> TYPE_PENDAPATAN
            is Pengeluaran -> TYPE_PENGELUARAN
            is Transfer -> TYPE_TRANSFER
            else -> throw IllegalArgumentException("Invalid transaction type")
        }
    }

    inner class PendapatanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val kategoriTextView: TextView = itemView.findViewById(R.id.deskripsiID)
        private val deskripsiTextView: TextView = itemView.findViewById(R.id.kategoriID)
        private val balanceTextView: TextView = itemView.findViewById(R.id.Balance)
        private val bulanDanTahun: TextView = itemView.findViewById(R.id.tv_date_today)
        private val timeWaktu: TextView = itemView.findViewById(R.id.jamid)
        private val tanggalTextView: TextView = itemView.findViewById(R.id.menampilkanDay)
        private val cardTanggal: CardView = itemView.findViewById(R.id.cardtanggal)
        private val piggyIcon: ImageView = itemView.findViewById(R.id.piggyicon)

        fun bind(pendapatan: Pendapatan) {
            kategoriTextView.text = pendapatan.kategoriP
            deskripsiTextView.text = pendapatan.deskripsiP
            balanceTextView.text = "+${NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(pendapatan.jumlahP.toDouble())}"
            balanceTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.menu_icon_color))

            piggyIcon.setImageResource(R.drawable.piggy_bank)

            pendapatan.tanggalP?.let { tanggal ->
                val formattedDate = dateFormat.format(tanggal)
                if (lastDateDisplayed != null && dateFormat.format(lastDateDisplayed) == formattedDate) {
                    cardTanggal.visibility = View.GONE
                } else {
                    cardTanggal.visibility = View.VISIBLE
                    lastDateDisplayed = tanggal
                }

                bulanDanTahun.text = monthYearFormat.format(tanggal)
                timeWaktu.text = timeFormat.format(tanggal)
                tanggalTextView.text = formattedDate
            }
        }
    }

    inner class PengeluaranViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val kategoriTextView: TextView = itemView.findViewById(R.id.deskripsiID)
        private val deskripsiTextView: TextView = itemView.findViewById(R.id.kategoriID)
        private val balanceTextView: TextView = itemView.findViewById(R.id.Balance)
        private val bulanDanTahun: TextView = itemView.findViewById(R.id.tv_date_today)
        private val timeWaktu: TextView = itemView.findViewById(R.id.jamid)
        private val tanggalTextView: TextView = itemView.findViewById(R.id.menampilkanDay)
        private val cardTanggal: CardView = itemView.findViewById(R.id.cardtanggal)
        private val piggyIcon: ImageView = itemView.findViewById(R.id.piggyicon)

        fun bind(pengeluaran: Pengeluaran) {
            kategoriTextView.text = pengeluaran.kategori
            deskripsiTextView.text = pengeluaran.deskripsi
            balanceTextView.text = "-${NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(pengeluaran.jumlah.toDouble())}"
            balanceTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.redFlag))

            piggyIcon.setImageResource(R.drawable.iconpiggycrash)

            pengeluaran.tanggal?.let { tanggal ->
                val formattedDate = dateFormat.format(tanggal)
                if (lastDateDisplayed != null && dateFormat.format(lastDateDisplayed) == formattedDate) {
                    cardTanggal.visibility = View.GONE
                } else {
                    cardTanggal.visibility = View.VISIBLE
                    lastDateDisplayed = tanggal
                }

                bulanDanTahun.text = monthYearFormat.format(tanggal)
                timeWaktu.text = timeFormat.format(tanggal)
                tanggalTextView.text = formattedDate
            }
        }
    }

    inner class TransferViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val kategoriTextView: TextView = itemView.findViewById(R.id.deskripsiID)
        private val deskripsiTextView: TextView = itemView.findViewById(R.id.kategoriID)
        private val balanceTextView: TextView = itemView.findViewById(R.id.Balance)
        private val bulanDanTahun: TextView = itemView.findViewById(R.id.tv_date_today)
        private val timeWaktu: TextView = itemView.findViewById(R.id.jamid)
        private val tanggalTextView: TextView = itemView.findViewById(R.id.menampilkanDay)
        private val cardTanggal: CardView = itemView.findViewById(R.id.cardtanggal)
        private val piggyIcon: ImageView = itemView.findViewById(R.id.piggyicon)

        fun bind(transfer: Transfer) {
            kategoriTextView.text = transfer.kategoriDari
            deskripsiTextView.text = transfer.kategoriKepada
            balanceTextView.text = "${NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(transfer.jumlahT.toDouble())}"
            balanceTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.blue))

            piggyIcon.setImageResource(R.drawable.baseline_swap_vert_24)

            transfer.tanggalT?.let { tanggal ->
                val formattedDate = dateFormat.format(tanggal)
                if (lastDateDisplayed != null && dateFormat.format(lastDateDisplayed) == formattedDate) {
                    cardTanggal.visibility = View.GONE
                } else {
                    cardTanggal.visibility = View.VISIBLE
                    lastDateDisplayed = tanggal
                }

                bulanDanTahun.text = monthYearFormat.format(tanggal)
                timeWaktu.text = timeFormat.format(tanggal)
                tanggalTextView.text = formattedDate
            }
        }
    }


    companion object {
        private const val TYPE_PENDAPATAN = 1
        private const val TYPE_PENGELUARAN = 2
        private const val TYPE_TRANSFER = 3
    }
}
