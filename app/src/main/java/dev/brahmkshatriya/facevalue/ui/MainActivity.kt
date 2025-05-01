package dev.brahmkshatriya.facevalue.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions
import dev.brahmkshatriya.facevalue.databinding.ActivityMainBinding
import dev.brahmkshatriya.facevalue.utils.PermsUtils.checkAppPermissions
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val viewModel by viewModel<ImageViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        DynamicColors.applyToActivityIfAvailable(
            this, DynamicColorsOptions.Builder().build()
        )
        setContentView(binding.root)
        checkAppPermissions { viewModel.refresh(true) }
    }
}