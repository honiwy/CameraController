package com.system.cameracontroller

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : AppCompatActivity() {
    var type = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        type = CameraApplication.instance.getSharedPreferences("app_data", Context.MODE_PRIVATE).getBoolean("type", false)
        setCameraStatus()
    }

    private fun setCameraStatus() {
        val dc = getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val myAdmin = ComponentName(this, MyAdmin::class.java)
        if(!type) {
            if (dc.isAdminActive(myAdmin)) {
                type = !type
                dc.setCameraDisabled(myAdmin, type)
                showToast((if (type) "Disable" else "Enable") + " camera")
                CameraApplication.instance.getSharedPreferences("app_data", Context.MODE_PRIVATE)
                    .edit().putBoolean("type", type).apply()
                setAppDisplayComponent(type)
                finish()
            } else {
                type = false
                val str = "Please active device administrator to disable camera permission"
                showToast(str)

                val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, myAdmin)
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, str)

                resultLauncher.launch(intent)
            }
        }
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            setCameraStatus()
        }else {
            showToast("Cannot acquire permission, try again")
            finish()
        }
    }

    private fun showToast(msg: String?) {
        Toast.makeText(this@MainActivity, msg, Toast.LENGTH_LONG).show()
    }


    private fun setAppDisplayComponent(type: Boolean) {
        val icon1 = ComponentName(this, "$packageName.MainActivity")
        val icon2 = ComponentName(this, "$packageName.Main2")
        if (type) {
            enableComponent(icon2)
            disableComponent(icon1)
        } else {
            enableComponent(icon1)
            disableComponent(icon2)
        }
    }

    private fun enableComponent(componentName: ComponentName) {
        packageManager.setComponentEnabledSetting(
            componentName,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    private fun disableComponent(componentName: ComponentName) {
        packageManager.setComponentEnabledSetting(
            componentName,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
    }
}