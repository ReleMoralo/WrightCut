package za.co.wrightcut

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import za.co.wrightcut.data.local.ServiceEntity
import za.co.wrightcut.data.repository.WrightCutRepository

class ServicesActivity : AppCompatActivity() {
    private lateinit var repository: WrightCutRepository
    private var serviceEntities: List<ServiceEntity> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_services)

        repository = WrightCutRepository(this)

        val listView = findViewById<ListView>(R.id.servicesListView)

        lifecycleScope.launch {
            serviceEntities = repository.getServices()
            val serviceDisplayList = serviceEntities.map { "${it.name} · R${it.price.toInt()}" }
            listView.adapter = ArrayAdapter(this@ServicesActivity, android.R.layout.simple_list_item_1, serviceDisplayList)
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            if (position in serviceEntities.indices) {
                val selectedService = serviceEntities[position]
                val intent = Intent(this, BookAppointmentActivity::class.java).apply {
                    putExtra("EXTRA_SERVICE_ID", selectedService.id)
                    putExtra("EXTRA_SERVICE_NAME", selectedService.name)
                    putExtra("EXTRA_SERVICE_PRICE", selectedService.price)
                }
                startActivity(intent)
            }
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_services
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { startActivity(Intent(this, MainActivity::class.java)); finish(); true }
                R.id.nav_services -> true
                R.id.nav_gallery -> { startActivity(Intent(this, GalleryActivity::class.java)); finish(); true }
                R.id.nav_bookings -> { startActivity(Intent(this, MyBookingsActivity::class.java)); finish(); true }
                R.id.nav_settings -> { startActivity(Intent(this, SettingsActivity::class.java)); finish(); true }
                else -> false
            }
        }
    }
}
