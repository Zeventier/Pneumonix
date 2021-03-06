package com.bangkit.pneumoniadetector.ui.detail

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.bangkit.pneumoniadetector.data.remote.response.History
import com.bangkit.pneumoniadetector.databinding.ActivityDetailBinding
import com.bangkit.pneumoniadetector.R

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var data: History

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        data = intent.getParcelableExtra<History>(EXTRA_DATA) as History
        setupData(data)


    }

    private fun setupData(data: History){
        binding.textViewNameContent.text = data.name
        binding.textViewAccuracyContent.text = data.probability
        binding.textViewPredictionContent.text = data.prediction
        binding.textViewDescription.text = data.description
        Glide.with(applicationContext)
            .load(data.photoUrl)
            .into(binding.imageViewImage)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detail_activity, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.item_edit -> {
                val intent = Intent(this@DetailActivity, EditDetailActivity::class.java)
                intent.putExtra(EditDetailActivity.EXTRA_CALLED_FROM, FROM_DETAIL_ACTIVITY)
                intent.putExtra(EditDetailActivity.EXTRA_DATA_BEFORE_EDIT, data)
                launcherIntentEditDetail.launch(intent)
                true
            }
            else -> false
        }
    }

    private val launcherIntentEditDetail = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if(it.resultCode == EditDetailActivity.EDIT_DETAIL_RESULT){
            val editedData = it.data?.getParcelableExtra<History>(EditDetailActivity.EXTRA_EDIT_DATA)
            setupData(editedData as History)
        }
    }

    companion object{
        const val EXTRA_DATA = "extra_data"
        const val FROM_DETAIL_ACTIVITY = 71
    }
}