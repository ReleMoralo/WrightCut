package za.co.wrightcut

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import za.co.wrightcut.data.local.BookingEntity
import za.co.wrightcut.data.repository.WrightCutRepository

class MyBookingsActivity : AppCompatActivity() {
    private lateinit var repository: WrightCutRepository

    private lateinit var upcomingCard: LinearLayout
    private lateinit var noBookingsText: TextView
    private lateinit var upcomingTitle: TextView
    private lateinit var upcomingSubtitle: TextView
    private lateinit var upcomingStatus: TextView
    private var currentBooking: BookingEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_bookings)

        repository = WrightCutRepository(this)

        upcomingCard = findViewById(R.id.upcomingCard)
        noBookingsText = findViewById(R.id.noBookingsText)
        upcomingTitle = findViewById(R.id.upcomingTitle)
        upcomingSubtitle = findViewById(R.id.upcomingSubtitle)
        upcomingStatus = findViewById(R.id.upcomingStatus)

        loadBookings()

        findViewById<Button>(R.id.rescheduleBtn).setOnClickListener {
            val intent = Intent(this, BookAppointmentActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.cancelBtn).setOnClickListener {
            currentBooking?.let { booking ->
                lifecycleScope.launch {
                    repository.cancelBooking(booking.bookingId)
                    Toast.makeText(this@MyBookingsActivity, "Booking Cancelled!", Toast.LENGTH_SHORT).show()
                    loadBookings()
                }
            }
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_bookings
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { startActivity(Intent(this, MainActivity::class.java)); finish(); true }
                R.id.nav_services -> { startActivity(Intent(this, ServicesActivity::class.java)); finish(); true }
                R.id.nav_gallery -> { startActivity(Intent(this, GalleryActivity::class.java)); finish(); true }
                R.id.nav_bookings -> true
                R.id.nav_settings -> { startActivity(Intent(this, SettingsActivity::class.java)); finish(); true }
                else -> false
            }
        }
    }

    private fun loadBookings() {
        lifecycleScope.launch {
            val bookings = repository.getBookings()
            val upcoming = bookings.firstOrNull { it.status == "Confirmed" }
            if (upcoming != null) {
                currentBooking = upcoming
                upcomingCard.visibility = View.VISIBLE
                noBookingsText.visibility = View.GONE
                upcomingTitle.text = upcoming.service
                upcomingSubtitle.text = "with ${upcoming.barber} · ${upcoming.date} at ${upcoming.time} · R${upcoming.price.toInt()}"
                upcomingStatus.text = upcoming.status
            } else {
                currentBooking = null
                upcomingCard.visibility = View.GONE
                noBookingsText.visibility = View.VISIBLE
            }
        }
    }
}
