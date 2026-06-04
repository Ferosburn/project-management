package com.ferosburn.project_management.component.pagination

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems

@Composable
fun <T : Any> PaginatedLazyColumn(
    lazyPagingItems: LazyPagingItems<T>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(20.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(12.dp),
    header: (@Composable () -> Unit)? = null,
    itemKey: ((T) -> Any)? = null,
    itemContent: @Composable (T) -> Unit
) {
    when (val refreshState = lazyPagingItems.loadState.refresh) {
        is LoadState.Error -> Box(
            modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) { Text(text = refreshState.error.message ?: "Failed to load data") }

        LoadState.Loading -> Box(
            modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) { CircularProgressIndicator() }

        is LoadState.NotLoading -> LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = contentPadding,
            verticalArrangement = verticalArrangement
        ) {
            header?.let { item { it() } }
            items(lazyPagingItems.itemCount, if (itemKey != null) { index ->
                lazyPagingItems[index]?.let { itemKey(it) } ?: index
            } else null) { index ->
                val item = lazyPagingItems[index]
                if (item != null) itemContent(item)
            }
            when (val appendState = lazyPagingItems.loadState.append) {
                is LoadState.Error -> item {
                    Box(
                        Modifier.fillMaxWidth().padding(20.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = appendState.error.message ?: "Failed to load data")
                    }
                }

                LoadState.Loading -> item {
                    Box(
                        Modifier.fillMaxWidth().padding(20.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is LoadState.NotLoading -> {}
            }
        }
    }
}