package com.bangkit.pneumoniadetector.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.bangkit.pneumoniadetector.data.adapter.ResultPagingSource
import com.bangkit.pneumoniadetector.data.remote.response.History
import com.bangkit.pneumoniadetector.data.remote.response.ResultItem
import com.bangkit.pneumoniadetector.data.remote.retrofit.ApiConfig

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = ""
    }
    val text: LiveData<String> = _text

    private val webApiService = ApiConfig.getApiService()

    //Limiting item showed in rvRecent by only 3 item
    private fun resultsData(): LiveData<PagingData<History>>{
        return Pager(
            config = PagingConfig(
                pageSize = 5,
                initialLoadSize = 5,
            ),
            pagingSourceFactory = {
                ResultPagingSource(webApiService, 1)
            }
        ).liveData
    }

    val data:LiveData<PagingData<History>> = resultsData().cachedIn(viewModelScope)

    // Dummy data
    private val listDataTemp: List<ResultItem> =
        listOf(ResultItem("asId", "asName", "asPneumoniaType", "asAccuracy99.99%", "as31FEB","asIniDeskripsi", "https://media.suara.com/pictures/653x366/2020/12/08/91579-david-gadgetin.jpg"))
    val pagingTemp: PagingData<ResultItem> = PagingData.from(listDataTemp)
}