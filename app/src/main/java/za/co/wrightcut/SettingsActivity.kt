package za.co.wrightcut

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import za.co.wrightcut.data.network.AuthTokenManager
import za.co.wrightcut.data.repository.WrightCutRepository

class SettingsActivity : AppCompatActivity() {
    private lateinit var repository: WrightCutRepository
    private lateinit var tokenManager: AuthTokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        repository = WrightCutRepository(this)
        tokenManager = AuthTokenManager(this)

        val settingsAvatarLetter = findViewById<TextView>(R.id.settingsAvatarLetter)
        val settingsProfileName = findViewById<TextView>(R.id.settingsProfileName)
        val settingsProfileEmail = findViewById<TextView>(R.id.settingsProfileEmail)

        lifecycleScope.launch {
            val user = repository.getUserProfile()
            settingsProfileName.text = user.fullName
            settingsProfileEmail.text = user.email
            settingsAvatarLetter.text = user.fullName.firstOrNull()?.toString()?.uppercase() ?: "U"
        }

        findViewById<RelativeLayout>(R.id.aboutContactRow).setOnClickListener {
            startActivity(Intent(this, ContactActivity::class.java))
        }

        findViewById<Button>(R.id.logoutBtn).setOnClickListener {
            tokenManager.clearToken()
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_settings
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { startActivity(Intent(this, MainActivity::class.java)); finish(); true }
                R.id.nav_services -> { startActivity(Intent(this, ServicesActivity::class.java)); finish(); true }
                R.id.nav_gallery -> { startActivity(Intent(this, GalleryActivity::class.java)); finish(); true }
                R.id.nav_bookings -> { startActivity(Intent(this, MyBookingsActivity::class.java)); finish(); true }
                R.id.nav_settings -> true
                else -> false
            }
        }
    }
}
