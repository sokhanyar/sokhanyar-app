package ir.saltech.sokhanyar.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.io.IOException
import okhttp3.OkHttpClient
import okhttp3.Request

sealed class ConnectionState {
	data object Available : ConnectionState()
	data object Unavailable : ConnectionState()
}

/**
 * Network utility to get current state of internet connection
 */


private fun getCurrentConnectivityState(context: Context): ConnectionState {
	if (isNetworkAvailable(context)) {
		repeat(3) {
			try {
				val client = OkHttpClient()
				val request = Request.Builder()
					.url("https://google.com")
					.get()
					.build()
				val response = client.newCall(request).execute()
				if (response.isSuccessful)
					return ConnectionState.Available
			} catch (ioExcp: IOException) {
				Log.e("CONNECTIVITY_CHECKER", "Unable to connect to internet: ${ioExcp.message}")
			}
		}
	}
	return ConnectionState.Unavailable
}

@ExperimentalCoroutinesApi
fun Context.observeConnectivityAsFlow() = callbackFlow {
	val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

	val callback = NetworkCallback { connectionState -> trySend(connectionState) }

	val networkRequest = NetworkRequest.Builder()
		.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
		.build()

	connectivityManager.registerNetworkCallback(networkRequest, callback)


	// Set current state
	val currentState = getCurrentConnectivityState(this@observeConnectivityAsFlow)
	trySend(currentState)

	// Remove callback when not used
	awaitClose {
		// Remove listeners
		connectivityManager.unregisterNetworkCallback(callback)
	}
}

fun NetworkCallback(callback: (ConnectionState) -> Unit): ConnectivityManager.NetworkCallback {
	return object : ConnectivityManager.NetworkCallback() {
		override fun onAvailable(network: Network) {
			callback(ConnectionState.Available)
		}

		override fun onLost(network: Network) {
			callback(ConnectionState.Unavailable)
		}
	}
}

@ExperimentalCoroutinesApi
@Composable
fun connectivityState(): State<ConnectionState> {
	val context = LocalContext.current

	// Creates a State<ConnectionState> with current connectivity state as initial value
	return produceState(initialValue = if (isNetworkAvailable(context)) ConnectionState.Available else ConnectionState.Unavailable) {
		// In a coroutine, can make suspend calls
		context.observeConnectivityAsFlow().collect { value = it }
	}
}
