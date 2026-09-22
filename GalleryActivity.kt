package za.co.wrightcut

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import za.co.wrightcut.data.repository.WrightCutRepository

class GalleryActivity : AppCompatActivity() {
    private lateinit var repository: WrightCutRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        repository = WrightCutRepository(this)

        lifecycleScope.launch {
            repository.getGallery()
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_gallery
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { startActivity(Intent(this, MainActivity::class.java)); finish(); true }
                R.id.nav_services -> { startActivity(Intent(this, ServicesActivity::class.java)); finish(); true }
                R.id.nav_gallery -> true
                R.id.nav_bookings -> { startActivity(Intent(this, MyBookingsActivity::class.java)); finish(); true }
                R.id.nav_settings -> { startActivity(Intent(this, SettingsActivity::class.java)); finish(); true }
                else -> false
            }
        }
    }
}
