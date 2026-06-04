package com.ferosburn.project_management.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ferosburn.project_management.models.ApiResponse
import com.ferosburn.project_management.network.NetworkResult

class AppPagingSource<T : Any>(
    private val fetch: suspend (page: Int, limit: Int) -> NetworkResult<ApiResponse<List<T>>>
) : PagingSource<Int, T>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val currentPage = params.key ?: 1
        val limit = params.loadSize

        return when (val response = fetch(currentPage, limit)) {
            is NetworkResult.Error -> LoadResult.Error(Exception(response.message))
            is NetworkResult.Success -> {
                val data = response.data.data ?: emptyList()
                val pagination = response.data.pagination

                LoadResult.Page(
                    data = data,
                    prevKey = if (currentPage == 1) null else currentPage - 1,
                    nextKey = if (pagination != null && currentPage < pagination.totalPage) currentPage + 1 else null,
                )
            }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return state.anchorPosition?.let { anchorPosition: Int ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}