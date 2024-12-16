package com.example.calculator

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var tvDisplay: TextView
    private var currentEquation: String = ""
    private var input = ""
    private var operator = ""
    private var firstOperand = ""
    private lateinit var calculatorApiService: CalculatorApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize API service
        calculatorApiService = CalculatorApiService()

        tvDisplay = findViewById(R.id.tvDisplay)

        val buttons = listOf(
            R.id.btn0 to "0", R.id.btn1 to "1", R.id.btn2 to "2",
            R.id.btn3 to "3", R.id.btn4 to "4", R.id.btn5 to "5",
            R.id.btn6 to "6", R.id.btn7 to "7", R.id.btn8 to "8",
            R.id.btn9 to "9"
        )
        buttons.forEach { (id, value) ->
            findViewById<Button>(id).setOnClickListener { appendNumber(value) }
        }

        findViewById<Button>(R.id.btnDot).setOnClickListener { appendDot() }
        findViewById<Button>(R.id.btnAdd).setOnClickListener { setOperator("+") }
        findViewById<Button>(R.id.btnSubtract).setOnClickListener { setOperator("-") }
        findViewById<Button>(R.id.btnMultiply).setOnClickListener { setOperator("*") }
        findViewById<Button>(R.id.btnDivide).setOnClickListener { setOperator("/") }
        findViewById<Button>(R.id.btnEqual).setOnClickListener { calculateResult() }
        findViewById<Button>(R.id.btnClear).setOnClickListener { clear() }
        findViewById<Button>(R.id.btnBackspace).setOnClickListener { backspace() }
    }

    private fun appendNumber(number: String) {
        input += number
        currentEquation += number
        tvDisplay.text = currentEquation
    }

    private fun appendDot() {
        if (!input.contains(".")) {
            input += "."
            currentEquation += "."
            tvDisplay.text = currentEquation
        }
    }

    private fun setOperator(op: String) {
        if (input.isNotEmpty()) {
            if (firstOperand.isEmpty()) {
                firstOperand = input
            } else if (operator.isNotEmpty()) {
                calculateResult()
            }
            operator = op
            currentEquation += " $op "
            input = ""
            tvDisplay.text = currentEquation
        } else if (firstOperand.isNotEmpty()) {
            operator = op
            currentEquation = currentEquation.dropLastWhile { it.isWhitespace() }
            currentEquation = currentEquation.dropLast(1) + " $op "
            tvDisplay.text = currentEquation
        }
    }

    private fun calculateResult() {
        if (firstOperand.isNotEmpty() && operator.isNotEmpty()) {
            val secondValue = if (input.isNotEmpty()) input.toDouble() else 0.0
            val firstValue = firstOperand.toDouble()

            // Perform the calculation
            val result = when (operator) {
                "+" -> firstValue + secondValue
                "-" -> firstValue - secondValue
                "*" -> firstValue * secondValue
                "/" -> {
                    if (secondValue != 0.0) {
                        firstValue / secondValue
                    } else {
                        tvDisplay.text = "undefined"
                        clear()
                        return
                    }
                }
                else -> firstValue
            }

            // Save calculation to backend
            saveCalculation(
                expression = "$firstValue $operator $secondValue",
                result = result
            )

            tvDisplay.text = result.toString()
            firstOperand = result.toString()
            input = ""
            operator = ""
            currentEquation = result.toString()
        }
    }

    private fun saveCalculation(expression: String, result: Double) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                calculatorApiService.saveCalculation(
                    Calculation(
                        expression = expression,
                        result = result,
                        deviceId = android.provider.Settings.Secure.getString(
                            contentResolver,
                            android.provider.Settings.Secure.ANDROID_ID
                        )
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                // You might want to show an error message to the user
                runOnUiThread {
                    // Handle error (optional)
                }
            }
        }
    }

    private fun clear() {
        input = ""
        operator = ""
        firstOperand = ""
        currentEquation = ""
        tvDisplay.text = "0"
    }

    private fun backspace() {
        if (input.isNotEmpty()) {
            input = input.dropLast(1)
            currentEquation = currentEquation.dropLast(1)
            tvDisplay.text = if (input.isEmpty()) "0" else currentEquation
        }
    }
}