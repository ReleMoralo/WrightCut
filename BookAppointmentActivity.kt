package za.co.wrightcut

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import za.co.wrightcut.data.local.ServiceEntity
import za.co.wrightcut.data.repository.WrightCutRepository

class BookAppointmentActivity : AppCompatActivity() {
    private lateinit var repository: WrightCutRepository

    private var selectedBarberId = 1
    private var selectedServiceId = 1
    private var selectedServicePrice = 100.0
    private var selectedDate = "2026-07-18"
    private var selectedTime = "12:00"

    private var availableServices: List<ServiceEntity> = emptyList()

    private lateinit var barberKgCard: LinearLayout
    private lateinit var barberBoikhutsoCard: LinearLayout
    private lateinit var barberKgName: TextView
    private lateinit var barberBoikhutsoName: TextView
    private lateinit var confirmBtn: Button

    private lateinit var dateViews: List<TextView>
    private lateinit var timeViews: List<TextView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_appointment)

        repository = WrightCutRepository(this)

        val passedServiceId = intent.getIntExtra("EXTRA_SERVICE_ID", -1)
        val passedServiceName = intent.getStringExtra("EXTRA_SERVICE_NAME")
        val passedServicePrice = intent.getDoubleExtra("EXTRA_SERVICE_PRICE", -1.0)

        barberKgCard = findViewById(R.id.barberKgCard)
        barberBoikhutsoCard = findViewById(R.id.barberBoikhutsoCard)
        barberKgName = findViewById(R.id.barberKgName)
        barberBoikhutsoName = findViewById(R.id.barberBoikhutsoName)
        confirmBtn = findViewById(R.id.confirmBookingBtn)

        val serviceSpinner = findViewById<Spinner>(R.id.serviceSpinner)

        dateViews = listOf(
            findViewById(R.id.date08),
            findViewById(R.id.date09),
            findViewById(R.id.date10),
            findViewById(R.id.date11),
            findViewById(R.id.date12),
            findViewById(R.id.date13)
        )

        timeViews = listOf(
            findViewById(R.id.time0900),
            findViewById(R.id.time1030),
            findViewById(R.id.time1200),
            findViewById(R.id.time1330),
            findViewById(R.id.time1430),
            findViewById(R.id.time1500)
        )

        // Setup Barber selection
        barberKgCard.setOnClickListener {
            selectedBarberId = 1
            updateBarberUi()
        }

        barberBoikhutsoCard.setOnClickListener {
            selectedBarberId = 2
            updateBarberUi()
        }

        // Setup Date selection
        dateViews.forEachIndexed { index, view ->
            view.setOnClickListener {
                selectedDate = when (index) {
                    0 -> "2026-07-08"
                    1 -> "2026-07-09"
                    2 -> "2026-07-10"
                    3 -> "2026-07-11"
                    4 -> "2026-07-12"
                    else -> "2026-07-13"
                }
                updateDateUi(view)
            }
        }

        // Setup Time selection
        timeViews.forEach { view ->
            view.setOnClickListener {
                selectedTime = view.text.toString()
                updateTimeUi(view)
            }
        }

        // Load Services into Spinner
        lifecycleScope.launch {
            availableServices = repository.getServices()
            val serviceNames = availableServices.map { "${it.name} – R${it.price.toInt()}" }
            val adapter = ArrayAdapter(this@BookAppointmentActivity, android.R.layout.simple_spinner_item, serviceNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            serviceSpinner.adapter = adapter

            if (passedServiceId != -1) {
                val foundIndex = availableServices.indexOfFirst { it.id == passedServiceId }
                if (foundIndex != -1) {
                    serviceSpinner.setSelection(foundIndex)
                }
            }

            serviceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (position in availableServices.indices) {
                        val s = availableServices[position]
                        selectedServiceId = s.id
                        selectedServicePrice = s.price
                        updateConfirmButton()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        confirmBtn.setOnClickListener {
            lifecycleScope.launch {
                val result = repository.createBooking(
                    barberId = selectedBarberId,
                    serviceId = selectedServiceId,
                    date = selectedDate,
                    time = selectedTime
                )
                if (result.isSuccess) {
                    Toast.makeText(this@BookAppointmentActivity, "Booking Created Successfully!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@BookAppointmentActivity, MyBookingsActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@BookAppointmentActivity, "Error creating booking", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateBarberUi() {
        if (selectedBarberId == 1) {
            barberKgCard.setBackgroundResource(R.drawable.bg_gold_button)
            barberKgName.setTextColor(resources.getColor(R.color.bg_dark, theme))
            barberBoikhutsoCard.setBackgroundResource(R.drawable.bg_dark_card)
            barberBoikhutsoName.setTextColor(resources.getColor(R.color.text_primary, theme))
        } else {
            barberBoikhutsoCard.setBackgroundResource(R.drawable.bg_gold_button)
            barberBoikhutsoName.setTextColor(resources.getColor(R.color.bg_dark, theme))
            barberKgCard.setBackgroundResource(R.drawable.bg_dark_card)
            barberKgName.setTextColor(resources.getColor(R.color.text_primary, theme))
        }
    }

    private fun updateDateUi(selectedView: TextView) {
        dateViews.forEach {
            it.setBackgroundResource(R.drawable.bg_dark_card)
            it.setTextColor(resources.getColor(R.color.text_primary, theme))
        }
        selectedView.setBackgroundResource(R.drawable.bg_gold_button)
        selectedView.setTextColor(resources.getColor(R.color.bg_dark, theme))
    }

    private fun updateTimeUi(selectedView: TextView) {
        timeViews.forEach {
            it.setBackgroundResource(R.drawable.bg_dark_card)
            it.setTextColor(resources.getColor(R.color.text_primary, theme))
        }
        selectedView.setBackgroundResource(R.drawable.bg_gold_button)
        selectedView.setTextColor(resources.getColor(R.color.bg_dark, theme))
    }

    private fun updateConfirmButton() {
        confirmBtn.text = "CONFIRM BOOKING – R${selectedServicePrice.toInt()}"
    }
}
