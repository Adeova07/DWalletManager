package com.genus.backenddannis

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.genus.backenddannis.data.AppDatabase
import com.genus.backenddannis.data.entity.Transfer
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class TransferActivity : AppCompatActivity() {

    private lateinit var tanggalEditText: TextInputEditText
    private lateinit var jamEditText: TextInputEditText
    private lateinit var jumlahEditText: TextInputEditText
    private lateinit var kategori1Spinner: Spinner
    private lateinit var kategori2Spinner: Spinner
    private lateinit var simpanButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfer)

        tanggalEditText = findViewById(R.id.tanggalID)
        jamEditText = findViewById(R.id.jamID)
        jumlahEditText = findViewById(R.id.jumlahID)
        kategori1Spinner = findViewById(R.id.kategori1_Id)
        kategori2Spinner = findViewById(R.id.kategori2_id)
        simpanButton = findViewById(R.id.SimpanPendapatan)

        val database = AppDatabase.getDatabase(applicationContext)
        val kategoriDao = database.kategoriDao()

        // Mengambil data kategori dari database dalam coroutine
        GlobalScope.launch(Dispatchers.IO) {
            val kategoriList = kategoriDao.getKategoriAddList()

            withContext(Dispatchers.Main) {
                // Inisialisasi adapter untuk spinner kategori
                val kategoriAdapter = ArrayAdapter(this@TransferActivity, android.R.layout.simple_spinner_item, kategoriList)
                kategoriAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                // Set adapter ke spinner kategori
                kategori1Spinner.adapter = kategoriAdapter
                kategori2Spinner.adapter = kategoriAdapter
            }
        }

        // Mendapatkan tanggal saat ini dalam format yang diinginkan
        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        tanggalEditText.setText(currentDate)
        jamEditText.setText(currentTime)

        // Setup listener untuk TextInputEditText tanggalID
        tanggalEditText.setOnClickListener {
            showDatePickerDialog(tanggalEditText)
        }

        // Setup listener untuk TextInputEditText jamID
        jamEditText.setOnClickListener {
            showTimePickerDialog(jamEditText)
        }

        // Setup listener untuk tombol Simpan
        simpanButton.setOnClickListener {
            val tanggal = tanggalEditText.text.toString()
            val jam = jamEditText.text.toString()
            val jumlah = jumlahEditText.text.toString()
            val kategori1 = kategori1Spinner.selectedItem.toString()
            val kategori2 = kategori2Spinner.selectedItem.toString()

            // Validasi input
            if (tanggal.isEmpty() || jam.isEmpty() || jumlah.isEmpty() || kategori1.isEmpty() || kategori2.isEmpty()) {
                Toast.makeText(this, "Harap lengkapi semua kolom", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Gabungkan tanggal dan jam menjadi satu string
            val dateTime = "$tanggal $jam"

            // Simpan transfer ke database
            try {
                val transfer = Transfer(
                    tanggalT = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).parse(dateTime),
                    jumlahT = jumlah.toDouble(),
                    deskripsiT = "Transfer from $kategori1 to $kategori2",
                    kategoriDari = kategori1,
                    kategoriKepada = kategori2
                )
                saveTransferToDatabase(transfer)
            } catch (e: ParseException) {
                Toast.makeText(this, "Gagal menyimpan transfer: Format tanggal/jam salah", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Gagal menyimpan transfer: Format jumlah tidak valid", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    private fun showDatePickerDialog(textInputEditText: TextInputEditText) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)
                val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate.time)
                textInputEditText.setText(formattedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun showTimePickerDialog(textInputEditText: TextInputEditText) {
        val calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(
            this,
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                val selectedTime = Calendar.getInstance()
                selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                selectedTime.set(Calendar.MINUTE, minute)
                val formattedTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(selectedTime.time)
                textInputEditText.setText(formattedTime)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true // 24-hour format
        )
        timePickerDialog.show()
    }

    private fun saveTransferToDatabase(transfer: Transfer) {
        GlobalScope.launch(Dispatchers.IO) {
            val database = AppDatabase.getDatabase(applicationContext)
            val transferDao = database.transferDao()

            // Validasi bahwa kategoriDari dan kategoriKepada tidak sama
            if (transfer.kategoriDari == transfer.kategoriKepada) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@TransferActivity, "Kategori tidak boleh sama", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }

            try {
                // Simpan transfer ke database
                transferDao.insert(transfer)

                // Kembali ke WalletMActivity setelah berhasil menyimpan transfer
                withContext(Dispatchers.Main) {
                    val intent = Intent(this@TransferActivity, WalletMActivity::class.java)
                    startActivity(intent)
                    finish() // Selesai aktivitas TransferActivity agar tidak kembali ke sini saat menekan tombol back
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@TransferActivity, "Gagal menyimpan transfer", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
