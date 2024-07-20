package com.genus.backenddannis

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.genus.backenddannis.data.adapter.CombineAdapter
import com.genus.backenddannis.data.entity.Pendapatan
import com.genus.backenddannis.data.entity.Pengeluaran
import com.genus.backenddannis.data.entity.Transfer

class DetailSaldo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_saldo)

        val kategoriDompet = intent.getStringExtra("KATEGORI_DOMPET")
        val saldo = intent.getStringExtra("SALDO")

        val kategoriDompetTextView: TextView = findViewById(R.id.kategoriDompetTextView)
        val saldoTextView: TextView = findViewById(R.id.saldoTextView)
        val pendapatanNomTransaksi: TextView = findViewById(R.id.PendapatanNomTransaksi)
        val pengeluaranNomTransaksi: TextView = findViewById(R.id.PengeluaranNomTransaksi)
        val transferNomTransaksi: TextView = findViewById(R.id.TransferNomTransaksi)
        val recyclerView: RecyclerView = findViewById(R.id.recvDetail)

        kategoriDompetTextView.text = kategoriDompet ?: "Tidak ada kategori"
        saldoTextView.text = "Rp. ${saldo ?: "0"}"

        // Terima data pendapatan, pengeluaran, dan transfer dari Intent
        val pendapatanList = intent.getSerializableExtra("PENDAPATAN_LIST") as List<Pendapatan>
        val pengeluaranList = intent.getSerializableExtra("PENGELUARAN_LIST") as List<Pengeluaran>
        val transferKeluarList = intent.getSerializableExtra("TRANSFER_KELUAR_LIST") as? List<Transfer> ?: emptyList()
        val transferMasukList = intent.getSerializableExtra("TRANSFER_MASUK_LIST") as? List<Transfer> ?: emptyList()

        // Gabungkan pendapatan, pengeluaran, dan transfer yang sudah difilter ke dalam satu daftar transaksi
        val transactionList: MutableList<Any> = mutableListOf()
        transactionList.addAll(pendapatanList)
        transactionList.addAll(pengeluaranList)
        transactionList.addAll(transferKeluarList)
        transactionList.addAll(transferMasukList)

        // Tampilkan jumlah transaksi
        pendapatanNomTransaksi.text = "${pendapatanList.size} Transaksi"
        pengeluaranNomTransaksi.text = "${pengeluaranList.size} Transaksi"
        transferNomTransaksi.text = "${transferKeluarList.size + transferMasukList.size} Transaksi"

        val adapter = CombineAdapter(transactionList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val back = findViewById<ImageButton>(R.id.backtostatistik)
        back.setOnClickListener {
            val kembali = Intent(this, WalletMActivity::class.java)
            startActivity(kembali)
            finish()
        }
    }
}
