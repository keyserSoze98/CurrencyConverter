package com.keysersoze.currencyconverter

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private lateinit var toggle: ActionBarDrawerToggle

    private val BASE_URL = "https://api.exchangerate-api.com/v4/"

    private lateinit var spinnerSourceCurrency: Spinner
    private lateinit var editAmount: EditText
    private lateinit var spinnerTargetCurrency: Spinner
    private lateinit var btnConvert: Button
    private lateinit var tvResult: TextView

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val currencyApi = retrofit.create(CurrencyApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true);

        navView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.home -> startActivity(Intent(this, MainActivity::class.java))

                R.id.about -> startActivity(Intent(this, About::class.java))

                R.id.exit -> {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Exit")
                        .setMessage("Do you want to close the application?")
                        .setCancelable(false)
                        .setPositiveButton("Yes") { _, _ ->
                            exitProcess(1)
                        }
                        .setNegativeButton("No") {dialog, _ ->
                            dialog.dismiss()
                        }
                    val customDialog = builder.create()
                    customDialog.show()
                    customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
                    customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
                }
            }
            true
        }

        spinnerSourceCurrency = findViewById(R.id.spinnerSourceCurrency)
        editAmount = findViewById(R.id.editAmount)
        spinnerTargetCurrency = findViewById(R.id.spinnerTargetCurrency)
        btnConvert = findViewById(R.id.btnConvert)
        tvResult = findViewById(R.id.tvResult)

        val currenciesArray = resources.getStringArray(R.array.currencies_array)
        val sortedCurrencies = currenciesArray.sorted()

        val sourceAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            sortedCurrencies
        )
        sourceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        spinnerSourceCurrency.adapter = sourceAdapter

        val targetAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            sortedCurrencies
        )
        targetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        spinnerTargetCurrency.adapter = targetAdapter

        btnConvert.setOnClickListener {
            val sourceCurrency = spinnerSourceCurrency.selectedItem.toString()
            val targetCurrency = spinnerTargetCurrency.selectedItem.toString()
            val amountText = editAmount.text.toString()
            if (amountText.isEmpty()) {
                editAmount.error = "Please enter an amount"
                return@setOnClickListener
            }

            if (!isValidAmount(amountText)) {
                editAmount.error = "Please enter a valid amount"
                return@setOnClickListener
            }

            if (sourceCurrency == targetCurrency) {
                Toast.makeText(
                    this,
                    "Source and target currencies cannot be the same",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val amount = amountText.toDouble()
            convertCurrency(amount, sourceCurrency, targetCurrency)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun isValidAmount(amountText: String): Boolean {
        return try {
            amountText.toDouble()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }

    private fun convertCurrency(amount: Double, sourceCurrency: String, targetCurrency: String) {
        val call = currencyApi.getCurrencyData(sourceCurrency)
        call.enqueue(object: Callback<Currency> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<Currency>, response: Response<Currency>) {
                if (response.isSuccessful) {
                    val currency = response.body()
                    val sourceRate = currency?.rates?.get(sourceCurrency)
                    val targetRate = currency?.rates?.get(targetCurrency)
                    if (sourceRate != null && targetRate != null) {
                        val convertedAmount = amount * (targetRate / sourceRate)
                        tvResult.text = "Converted Amount: $convertedAmount"
                    } else {
                        tvResult.text = "Invalid currency code"
                    }
                }
            }

            @SuppressLint("SetTextI18n")
            override fun onFailure(call: Call<Currency>, t: Throwable) {
                tvResult.text = "Error fetching currency data"
            }
        })
    }
}