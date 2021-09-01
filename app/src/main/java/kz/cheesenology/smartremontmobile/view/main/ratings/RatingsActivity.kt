package kz.cheesenology.smartremontmobile.view.main.ratings

import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_ratings.*
import kz.cheesenology.smartremontmobile.R
import kz.cheesenology.smartremontmobile.SmartRemontApplication
import kz.cheesenology.smartremontmobile.model.ratings.RatingListAdapterModel
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import kotlin.properties.Delegates

class RatingsActivity : MvpAppCompatActivity(), RatingView {

    @Inject
    lateinit var presenter: RatingsPresenter

    @InjectPresenter
    lateinit var moxyPresenter: RatingsPresenter

    @ProvidePresenter
    fun providePresenter() = presenter

    private var adapter: RatingsAdapter by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        SmartRemontApplication[this@RatingsActivity].databaseComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ratings)

        title = "Рейтинг"

        presenter.setIntentData(intent!!.getIntExtra("remont_id",0))

        if (supportActionBar != null)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        adapter = RatingsAdapter()
        rvRatings.adapter = adapter
        rvRatings.layoutManager = LinearLayoutManager(baseContext)
        rvRatings.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )

        btnSaveRatings.setOnClickListener {
            presenter.checkRatings(adapter.data)
        }
    }

    override fun showToast(s: String) {
        Toast.makeText(this@RatingsActivity, s, Toast.LENGTH_SHORT).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun successPutRatings(s: String) {
        showToast(s)
        onBackPressed()
    }

    override fun setViewData(list: MutableList<RatingListAdapterModel>) {
        adapter.data = list
    }
}
