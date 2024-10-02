package io.github.junrdev.hiddengems.presentation.ui

import android.content.Context
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.github.junrdev.hiddengems.R

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}
