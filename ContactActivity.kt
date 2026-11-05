package za.co.wrightcut

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ContactActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)

        findViewById<TextView>(R.id.kgPhone).setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:0815145535"))
            startActivity(intent)
        }

        findViewById<TextView>(R.id.boikhutsoPhone).setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:0822786885"))
            startActivity(intent)
        }
    }
}
