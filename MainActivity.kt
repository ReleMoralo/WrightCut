package za.co.wrightcut

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import za.co.wrightcut.data.repository.WrightCutRepository

class MainActivity : AppCompatActivity() {
    private lateinit var repository: WrightCutRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        repository = WrightCutRepository(this)

        val homeUserName = findViewById<TextView>(R.id.homeUserName)
        val homeAvatarLetter = findViewById<TextView>(R.id.homeAvatarLetter)
        val homeLoyaltyText = findViewById<TextView>(R.id.homeLoyaltyText)

        lifecycleScope.launch {
            val user = repository.getUserProfile()
            val firstName = user.fullName.split(" ").firstOrNull() ?: "Lerato"
            homeUserName.text = "Hi, $firstName"
            homeAvatarLetter.text = firstName.firstOrNull()?.toString()?.uppercase() ?: "L"

            val bookings = repository.getBookings()
            val count = bookings.size.coerceAtMost(5)
            homeLoyaltyText.text = "$count / 5 cuts"
        }

        // Hero CTA
        findViewById<Button>(R.id.homeBookNowBtn).setOnClickListener {
            startActivity(Intent(this, BookAppointmentActivity::class.java))
        }

        // Popular Service Cards
        findViewById<LinearLayout>(R.id.popServiceFade).setOnClickListener {
            val intent = Intent(this, BookAppointmentActivity::class.java).apply {
                putExtra("EXTRA_SERVICE_ID", 1)
                putExtra("EXTRA_SERVICE_NAME", "Fade Only")
                putExtra("EXTRA_SERVICE_PRICE", 100.0)
            }
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.popServiceChiskop).setOnClickListener {
            val intent = Intent(this, BookAppointmentActivity::class.java).apply {
                putExtra("EXTRA_SERVICE_ID", 3)
                putExtra("EXTRA_SERVICE_NAME", "Chiskop (Clipper)")
                putExtra("EXTRA_SERVICE_PRICE", 80.0)
            }
            startActivity(intent)
        }

        // Barber Chips
        findViewById<TextView>(R.id.chipBarberKg).setOnClickListener {
            val intent = Intent(this, BookAppointmentActivity::class.java)
            startActivity(intent)
        }

        findViewById<TextView>(R.id.chipBarberBoikhutso).setOnClickListener {
            val intent = Intent(this, BookAppointmentActivity::class.java)
            startActivity(intent)
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_home
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_services -> { startActivity(Intent(this, ServicesActivity::class.java)); finish(); true }
                R.id.nav_gallery -> { startActivity(Intent(this, GalleryActivity::class.java)); finish(); true }
                R.id.nav_bookings -> { startActivity(Intent(this, MyBookingsActivity::class.java)); finish(); true }
                R.id.nav_settings -> { startActivity(Intent(this, SettingsActivity::class.java)); finish(); true }
                else -> false
            }
        }
    }
}
