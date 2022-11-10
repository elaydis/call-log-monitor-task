package io.github.elaydis.calllogmonitor.presentation

import android.Manifest.permission.READ_CALL_LOG
import android.Manifest.permission.READ_CONTACTS
import android.Manifest.permission.READ_PHONE_STATE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import dagger.hilt.android.AndroidEntryPoint
import io.github.elaydis.calllogmonitor.R
import io.github.elaydis.calllogmonitor.data.serverservice.ACTION_START_SERVER
import io.github.elaydis.calllogmonitor.data.serverservice.ServerServiceService
import io.github.elaydis.calllogmonitor.databinding.ActivityMainBinding
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), MainView {

    @Inject
    lateinit var mainPresenter: MainPresenter

    private lateinit var binding: ActivityMainBinding

    private val callLogRecyclerViewAdapter = CallLogRecyclerViewAdapter()
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsGrantedMap: Map<String, Boolean> ->
        val missingPermissions = permissionsGrantedMap.filterValues { !it }
        if (missingPermissions.isNotEmpty()) {
            mainPresenter.permissionsNotGranted()
        } else {
            mainPresenter.permissionsGranted()
        }
    }
    private val requiredPermissions = mutableListOf(
        READ_CALL_LOG,
        READ_PHONE_STATE,
        READ_CONTACTS
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpCallLogRecyclerView()
        mainPresenter.viewOnCreate()
        binding.startStopButton.setOnClickListener {
            mainPresenter.startStopButtonClicked()
        }
    }

    override fun onDestroy() {
        mainPresenter.viewOnDestroy()
        super.onDestroy()
    }

    private fun setUpCallLogRecyclerView() {
        binding.callLogRecyclerView.adapter = callLogRecyclerViewAdapter
    }

    override fun startServerService() {
        val intent = Intent(this, ServerServiceService::class.java).apply {
            action = ACTION_START_SERVER
        }
        startService(intent)
    }

    override fun stopServerService() {
        val intent = Intent(this, ServerServiceService::class.java)
        stopService(intent)
    }

    override fun showServerRunningText() {
        binding.serverStatusTextView.setText(R.string.server_running_status_text)
    }

    override fun showServerNotRunningText() {
        binding.serverStatusTextView.setText(R.string.server_not_running_status_text)
    }

    override fun updateIpAddressText(text: String) {
        binding.ipAddressTextview.text = text
    }

    override fun showStartServerButtonText() {
        binding.startStopButton.setText(R.string.start_server_button_text)
    }

    override fun showStopServerButtonText() {
        binding.startStopButton.setText(R.string.stop_server_button_text)
    }

    override fun updateStartStopButtonState(enabled: Boolean) {
        binding.startStopButton.isEnabled = enabled
    }

    override fun addNewCallLogEntry(logEntryModel: LogEntryModel) {
        callLogRecyclerViewAdapter.addLogEntryModel(logEntryModel)
    }

    override fun requestPermissions() {
        val missingPermissions = mutableListOf<String>()
        requiredPermissions.forEach {
            if (ContextCompat.checkSelfPermission(this, it) != PERMISSION_GRANTED) {
                missingPermissions.add(it)
            }
        }
        if (missingPermissions.isEmpty()) {
            mainPresenter.permissionsGranted()
        } else {
            requestPermissionLauncher.launch(missingPermissions.toTypedArray())
        }
    }

    override fun showMissingPermissionsDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.missing_permissions_dialog_title))
            .setMessage(getString(R.string.missing_permissions_dialog_message))
            .setNegativeButton(getString(R.string.cancel)) { di, _ -> di.cancel() }
            .setPositiveButton(getString(R.string.go_to_settings)) { _, _ -> mainPresenter.missingPermissionsDialogPositiveButtonClicked() }
            .show()
    }

    override fun openSettings() {
        val intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }
}