package be.odisee.veilingplatform.userInterface.ui

import AuthManager
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import be.odisee.veilingplatform.userInterface.ui.screens.ItemDetailScreen
import be.odisee.veilingplatform.userInterface.ui.screens.ItemsListScreen
import be.odisee.veilingplatform.userInterface.ui.screens.LoginScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import be.odisee.veilingplatform.userInterface.ui.viewmodel.ItemsViewModel
import be.odisee.veilingplatform.data.AuctionRepository
import be.odisee.veilingplatform.network.AuctionApiServiceFactory
import be.odisee.veilingplatform.userInterface.ui.screens.NewItemScreen
import be.odisee.veilingplatform.userInterface.ui.viewmodel.ItemsViewModelFactory
import be.odisee.veilingplatform.userInterface.ui.viewmodel.UserAuthViewModelFactory
import be.odisee.veilingplatform.userInterface.ui.viewmodel.UserAuthenticationViewModel

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val authManager = AuthManager(context)
    val apiService = AuctionApiServiceFactory.create()
    val repository = AuctionRepository(apiService, authManager)
    val itemsViewModel: ItemsViewModel = viewModel(factory = ItemsViewModelFactory(repository))
    val userAuthViewModel: UserAuthenticationViewModel =
        viewModel(factory = UserAuthViewModelFactory(authManager, apiService))

    val userAccountType = authManager.getUserAccountType()

    NavHost(navController = navController, startDestination = "itemsList") {
        composable("itemsList") {
            ItemsListScreen(navController = navController, viewModel = itemsViewModel, authManager = authManager)
        }
        composable("itemDetail/{itemId}") { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId")?.toIntOrNull()
            val item = itemsViewModel.items.value.getOrNull()?.find { it.id == itemId }
            ItemDetailScreen(
                navController = navController,
                item = item,
                viewModel = itemsViewModel,
                authManager = authManager
            )
        }
        composable("login") {
            LoginScreen(
                navController = navController,
                userAuthViewModel = userAuthViewModel
            )
        }

        composable("newItem") {
                NewItemScreen(
                    viewModel = itemsViewModel,
                    navController = navController,
                    userAccountType = userAccountType
                )
            }

        }
    }

