package com.example.game.presentation.gameslist.screen
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.game.R
import com.example.game.presentation.components.NetworkBanner
import com.example.game.presentation.gameslist.viewmodel.GamesListViewModel
import com.example.game.presentation.gameslist.components.*
import com.example.game.utils.NetworkObserver

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamesListScreen(
    onGameClick: (Int) -> Unit,
    networkObserver: NetworkObserver,
    viewModel: GamesListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val isOnline by networkObserver.isOnline.collectAsState(initial = true)

    LaunchedEffect(listState) {
        snapshotFlow {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = listState.layoutInfo.totalItemsCount
            lastVisible >= totalItems - 5 && totalItems > 0
        }.collect { shouldLoadMore ->
            if (shouldLoadMore) {
                viewModel.loadNextPage()
            }
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SportsEsports,
                            contentDescription = stringResource(R.string.cd_gamepad_icon),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(stringResource(R.string.screen_games_list))
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {

            NetworkBanner(isOnline = isOnline)

            GamesSearchBar(
                query = uiState.searchQuery,
                onQueryChange = viewModel::onSearchQueryChanged
            )

            GenreChipsRow(
                genres = uiState.genres,
                selectedGenre = uiState.selectedGenre,
                onGenreSelected = viewModel::selectGenre
            )

            Spacer(modifier = Modifier.height(8.dp))

            when {
                uiState.isInitialLoading -> {
                    ShimmerLoadingList(modifier = Modifier.fillMaxSize())
                }

                uiState.initialError != null -> {
                    ErrorView(
                        message = uiState.initialError!!,
                        onRetry = viewModel::retry,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                uiState.isContentEmpty -> {
                    EmptyView(
                        message = stringResource(R.string.empty_games_title),
                        subtitle = stringResource(R.string.empty_games_subtitle)
                    )
                }

                uiState.isSearchEmpty -> {
                    EmptyView(
                        message = stringResource(R.string.search_no_results_title, uiState.searchQuery),
                        subtitle = stringResource(R.string.search_no_results_subtitle)
                    )
                }

                else -> {
                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = uiState.filteredGames,
                            key = { it.id }
                        ) { game ->
                            GameCard(
                                game = game,
                                onClick = { onGameClick(game.id) }
                            )
                        }

                        if (uiState.isPaginationLoading) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(32.dp),
                                        strokeWidth = 3.dp
                                    )
                                }
                            }
                        }

                        if (uiState.paginationError != null) {
                            item {
                                TextButton(
                                    onClick = viewModel::retryPagination,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(stringResource(R.string.action_retry_pagination))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}