package com.nickcorban.weathermvvm.ui.weather.Future.list

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager

import com.nickcorban.weathermvvm.R
import com.nickcorban.weathermvvm.ui.base.ScopedFragment
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import data.database.LocalDateConverter
import data.database.unitlocalized.future.list.UnitSpecificSimpleFutureWeatherEntry
import kotlinx.android.synthetic.main.future_list_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import org.threeten.bp.LocalDate

class FutureListFragment : ScopedFragment(), KodeinAware {

    override val kodein by closestKodein()
    private val viewModelFactory: FutureWeatherListViewModelFactory by instance()

    private lateinit var viewModel: FutureListWeatherViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.future_list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(FutureListWeatherViewModel::class.java)
        bindUI()
    }

    private fun bindUI() = launch(Dispatchers.Main){
        val futureWeatherEntries = viewModel.weatherEntries.await()
        val weatherLocation = viewModel.weatherLocation.await()

        weatherLocation.observe(this@FutureListFragment, Observer{
            location -> if (location == null) return@Observer
            updateLocation(location.name)
        })

        futureWeatherEntries.observe(this@FutureListFragment, Observer { weatherEntries ->
            if (weatherEntries == null) return@Observer

            group_loading.visibility = View.GONE

            updateToNextWeek()
            initRecyclerView(weatherEntries.toFutureWeatherItems())
        })
    }

    private fun updateLocation(location : String) {
        (activity as? AppCompatActivity)?.supportActionBar?.title = location
    }

    private fun updateToNextWeek() {
        (activity as? AppCompatActivity)?.supportActionBar?.subtitle = "Следующая неделя"
    }

    private fun List<UnitSpecificSimpleFutureWeatherEntry>.toFutureWeatherItems() : List<FutureWeatherItem> {
        return this.map {
            FutureWeatherItem(it)
        }
    }
    private fun initRecyclerView(items : List<FutureWeatherItem>) {

        val groupAdapter = GroupAdapter<ViewHolder>().apply{
            addAll(items)
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@FutureListFragment.context)
            adapter = groupAdapter
        }
        groupAdapter.setOnItemClickListener{ item, view ->
            (item as? FutureWeatherItem)?.let {
                showWeatherDetail(it.weatherEntry.date, view)
            }
        }

    }

    private fun showWeatherDetail(date : LocalDate, view : View) {

        val dateString = LocalDateConverter.dateToString(date)!!
        val actionDetail = FutureListFragmentDirections.actionDetail(dateString)
        Navigation.findNavController(view).navigate(actionDetail)
    }
}
