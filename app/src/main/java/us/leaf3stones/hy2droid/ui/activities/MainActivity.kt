package us.leaf3stones.hy2droid.ui.activities

import android.app.Activity
import android.net.VpnService
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import us.leaf3stones.hy2droid.ui.theme.Hy2droidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Hy2droidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier, viewModel: MainActivityViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val vpnRequestLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.startVpnService(context)
            }
        }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Hysteria 2",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.Start)
        )

        val config = state.configData
        BasicHysteriaConfigEdit(
            serverAddress = config.server,
            password = config.password,
            sni = config.sni,
            onServerAddressChanged = viewModel::onServerChanged,
            onPasswordChanged = viewModel::onPasswordChanged,
            onSniChanged = viewModel::onSniChanged,
            onConfigConfirmed = viewModel::onConfigConfirmed,
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            text = if (state.isVpnConnected) "connected" else "disconnected",
            modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
            style = MaterialTheme.typography.titleMedium
        )
        if (state.isVpnConnected) {
            Button(onClick = { viewModel.stopVpnService(context) }) {
                Text(text = "stop vpn")
            }
        } else {
            Button(onClick = {
                val prepIntent = VpnService.prepare(context)
                if (prepIntent != null) {
                    vpnRequestLauncher.launch(prepIntent)
                } else {
                    viewModel.startVpnService(context)
                }
            }) {
                Text(text = "start vpn")
            }
        }

        if (state.shouldShowConfigInvalidRemainder) {
            AlertDialog(
                onDismissRequest = { viewModel.onConfigInvalidRemainderDismissed() },
                confirmButton = {
                    Text(
                        text = "ok",
                        modifier = Modifier.clickable { viewModel.onConfigInvalidRemainderDismissed() },
                        fontSize = 16.sp
                    )
                }, title = {
                    Text(
                        text = "Invalid Config",
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 20.sp
                    )
                }, text = {
                    Text(text = "Configuration data is incomplete. Only the \"sni\" field is optional.")
                })
        }
    }
}

@Composable
fun BasicHysteriaConfigEdit(
    serverAddress: String,
    password: String,
    sni: String,
    onServerAddressChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onSniChanged: (String) -> Unit,
    onConfigConfirmed: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = serverAddress,
            placeholder = {
                Text(text = "server address")
            },
            onValueChange = onServerAddressChanged,
            modifier = Modifier.fillMaxWidth(), maxLines = 1
        )
        OutlinedTextField(
            value = password,
            placeholder = {
                Text(text = "password")
            },
            onValueChange = onPasswordChanged,
            modifier = Modifier.fillMaxWidth(), maxLines = 1
        )
        OutlinedTextField(
            value = sni,
            placeholder = {
                Text(text = "sni")
            },
            onValueChange = onSniChanged,
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1
        )
        Button(
            onClick = onConfigConfirmed,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "save")
        }
    }
}