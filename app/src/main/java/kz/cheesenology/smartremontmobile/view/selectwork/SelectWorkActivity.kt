package kz.cheesenology.smartremontmobile.view.selectwork

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_select_work.*
import kz.cheesenology.smartremontmobile.R
import kz.cheesenology.smartremontmobile.util.PrefUtils
import kz.cheesenology.smartremontmobile.view.main.remontlist.RemontListActivity
import kz.cheesenology.smartremontmobile.view.request.requestlist.RequestListActivity

class SelectWorkActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_work)


        if (PrefUtils.prefs.getString("okk_id", "")?.toInt() == 1) {
            btnRequestAccept.visibility = View.VISIBLE
        } else {
            btnRequestAccept.visibility = View.INVISIBLE
        }

        btnRequestAccept.setOnClickListener {
            startActivity(Intent(this@SelectWorkActivity, RequestListActivity::class.java))
        }

        btnRemontAccept.setOnClickListener {
            startActivity(Intent(this@SelectWorkActivity, RemontListActivity::class.java))
        }
    }
}