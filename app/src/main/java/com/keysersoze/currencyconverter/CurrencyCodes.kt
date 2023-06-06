package com.keysersoze.currencyconverter

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class CurrencyCodes : AppCompatActivity() {

    private lateinit var listView: ListView

    private lateinit var adapter: ArrayAdapter<String>

    private val shortForms = arrayOf("AED", "AFN", "ALL", "AUD", "BRL", "CHF", "CNY", "EUR",
    "GBP", "INR", "JPY", "KWD", "MXN", "MYR", "NPR", "NZD", "OMR", "PKR", "RUB", "SGD", "USD")

    private val fullForms = arrayOf("Arab Emirates Dirham", "Afghan Afghani", "Albanian Lek",
    "Australian Dollar", "Brazilian Real", "Swiss Franc", "Chinese Yuan", "Euro", "Pound Sterling",
    "Indian Rupee", "Japanese Yen", "Kuwaiti Dinar", "Mexican Nuevo Peso", "Malaysian Ringgit",
    "Nepalese Rupee", "New Zealand Dollar", "Omani Rial", "Pakistan Rupee", "Russian Ruble",
    "Singapore Dollar", "United States Dollar")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_currency_codes)

        listView = findViewById(R.id.currencyLV)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, shortForms)

        listView.adapter = adapter

        listView.setOnItemClickListener { parent, view, position, id ->
            val fullForm = fullForms[position]

            Toast.makeText(this, fullForm, Toast.LENGTH_SHORT).show()
        }
    }
}