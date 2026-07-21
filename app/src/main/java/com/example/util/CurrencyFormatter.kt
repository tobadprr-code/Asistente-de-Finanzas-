package com.example.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CurrencyFormatter {

    fun format(amount: Double, symbol: String = "$"): String {
        val symbols = DecimalFormatSymbols(Locale.GERMAN).apply {
            groupingSeparator = '.'
            decimalSeparator = ','
        }
        val formatter = DecimalFormat("#,##0", symbols)
        val formattedNumber = formatter.format(amount)
        return "$symbol$formattedNumber"
    }

    fun formatDate(timeMillis: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("es", "ES"))
        return sdf.format(Date(timeMillis))
    }

    fun formatDateShort(timeMillis: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES"))
        return sdf.format(Date(timeMillis))
    }
}
