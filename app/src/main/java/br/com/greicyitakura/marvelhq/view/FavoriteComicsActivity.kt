package com.example.marvelhq.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import br.com.greicyitakura.marvelhq.R
import com.example.marvelhq.database.Comic
import com.example.marvelhq.database.ComicsDatabase
import com.example.marvelhq.model.Result
import com.example.marvelhq.view.adapter.FavoriteComicsAdapter
import com.example.marvelhq.viewModel.FavoriteViewModel
import java.io.Serializable

class FavoriteComicsActivity : AppCompatActivity() {

    private val dataBase by lazy {
        Room.databaseBuilder(
            applicationContext,
            ComicsDatabase::class.java,
            "database"
        ).build()
    }

    private val viewModel by lazy { ViewModelProvider(this).get(FavoriteViewModel::class.java) }
    private val recycler by lazy { findViewById<RecyclerView>(R.id.recycler_fav)}
    private val progressBar by lazy { findViewById<ProgressBar>(R.id.loading_favs) }
    lateinit var adapter: FavoriteComicsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite_comics)

        viewModel.database = dataBase
        viewModel.getFavComics()

        showProgressBar()
        showErrorMessage()
        initRecycler()
    }

    private fun initRecycler() {

        adapter = FavoriteComicsAdapter(){ comic->
            navigateToComicDetails(comic)
        }

        recycler.adapter = adapter
        val layoutManager = GridLayoutManager(this, 3)
        recycler.layoutManager = layoutManager


        viewModel.favComicsLiveData.observe(this) { comics ->
            adapter.addFavComics(comics)
        }
    }

    private fun navigateToComicDetails(comic: Result) {
        val intent = Intent(this, ComicDetailsActivity::class.java)
        intent.putExtra("comic", comic as Serializable)
        startActivity(intent)
    }

    private fun showErrorMessage() {
        viewModel.errorMessage.observe(this) {
            it?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showProgressBar() {

        viewModel.loading.observe(this) {
            if (it) {
                progressBar.visibility = VISIBLE
            } else {
                progressBar.visibility = GONE
            }
        }
    }
}
