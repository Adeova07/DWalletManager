package com.genus.backenddannis

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.genus.backenddannis.data.AppDatabase
import com.genus.backenddannis.data.entity.User
import com.genus.backenddannis.data.entity.Kategori
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Memeriksa apakah ada pengguna yang sudah login
        val userDao = AppDatabase.getDatabase(this).userDao()
        val user = userDao.getAll().firstOrNull()

        // Jika sudah ada pengguna yang login, langsung menuju UtamaActivity
        if (user != null && user.uid != null && user.uid!! > 0) {
            startUtamaActivity()
            return
        }

        setContentView(R.layout.activity_main)

        val btnSave = findViewById<Button>(R.id.btn_save)
        val usernameInputLayout = findViewById<TextInputLayout>(R.id.User_Name)
        val usernameEditText = usernameInputLayout.editText

        val categoryInputLayout = findViewById<TextInputLayout>(R.id.First_Category)
        val categoryEditText = categoryInputLayout.editText

        btnSave.setOnClickListener {
            val userName = usernameEditText?.text.toString().trim()
            val category = categoryEditText?.text.toString().trim()

            if (userName.isNotEmpty() && category.isNotEmpty()) {
                lifecycleScope.launch {
                    saveUserAndCategoryToDatabase(userName, category)
                    startUtamaActivity()
                }
            } else {
                Toast.makeText(this@MainActivity, "Please enter both username and category", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun saveUserAndCategoryToDatabase(userName: String, category: String) {
        withContext(Dispatchers.IO) {
            val userDao = AppDatabase.getDatabase(this@MainActivity).userDao()
            val kategoriDao = AppDatabase.getDatabase(this@MainActivity).kategoriDao()

            val user = User(userName = userName)
            userDao.insertAll(user)

            val kategori = Kategori(KategoriAdd = category)
            kategoriDao.insertAll(kategori)
        }
    }

    private fun startUtamaActivity() {
        val intent = Intent(this, UtamaActivity::class.java)
        startActivity(intent)
        finish() // Menutup MainActivity agar tidak dapat kembali dengan menekan tombol back
    }
}
