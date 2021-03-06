package pe.devpicon.android.marvelheroes.presentation.home

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import pe.androidperu.marvelheroes.R
import pe.androidperu.marvelheroes.databinding.ActivityMainBinding
import pe.devpicon.android.marvelheroes.app.MarvelHeroesApp
import pe.devpicon.android.marvelheroes.presentation.MainViewModelFactory

/**
 * El activity principal
 */
@ExperimentalCoroutinesApi
@FlowPreview
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private val searchAdapter by lazy {
        SearchAdapter(::onSelectResultHero)
    }
    private val comicListAdapter by lazy {
        ComicListAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val appContainer = (application as MarvelHeroesApp).appContainer
        mainViewModel = MainViewModelFactory(appContainer.viewStateMapper, appContainer.repository).create()

        initUI()
        initObservers()
    }

    private fun initUI() {
        initRecyclerView()
        initSearchBar()
    }

    private fun initRecyclerView() {
        binding.characterListRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        binding.characterListRecyclerView.adapter = comicListAdapter
    }

    private fun initObservers() {
        mainViewModel.heroes.observe(this, Observer {
            searchAdapter.setData(it)
        })

        mainViewModel.character.observe(this, Observer {
            Toast.makeText(this, "hero ${it.size}", Toast.LENGTH_SHORT).show()
            if (it.isNotEmpty()) {
                displayHeroData(it[0])
            }
        })

        mainViewModel.comics.observe(this, Observer {
            comicListAdapter.updateData(it)
        })
    }

    private fun displayHeroData(heroViewState: HeroViewState) = with(heroViewState) {
        binding.tvHeroName.text = name
        binding.tvHeroDescription.text = description
        Picasso.get().load(thumbnailUrl)
                .error(R.drawable.broken_shield).into(binding.ivHeroThumbnail)
    }

    private fun initSearchBar() {
        with(binding) {
            lvSearchResultList.adapter = searchAdapter

            svSearchHero.isActivated = true
            svSearchHero.onActionViewExpanded()
            svSearchHero.isIconified
            svSearchHero.clearFocus()

            svSearchHero.setOnQueryTextListener(object : OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    mainViewModel.queryChannel.offer(newText)
                    return false
                }
            })
        }
    }

    private fun onSelectResultHero(searchViewState: SearchViewState) {
        mainViewModel.queryChannel.offer("")
        mainViewModel.fetchCharacterAndComic(searchViewState.id)
    }
}
