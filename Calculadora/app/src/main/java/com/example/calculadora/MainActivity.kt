package com.example.calculadora

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Stack

class MainActivity : AppCompatActivity() {
    private lateinit var expression: TextView
    private lateinit var result: TextView
    private lateinit var clear: Button
    private lateinit var backSpace: Button
    private lateinit var pow: Button
    private lateinit var divide: Button
    private lateinit var multiply: Button
    private lateinit var opening: Button
    private lateinit var closing: Button
    private lateinit var plus: Button
    private lateinit var minus: Button
    private lateinit var equal: Button
    private lateinit var one: Button
    private lateinit var two: Button
    private lateinit var three: Button
    private lateinit var four: Button
    private lateinit var five: Button
    private lateinit var six: Button
    private lateinit var seven: Button
    private lateinit var eight: Button
    private lateinit var nine: Button
    private lateinit var zero: Button
    private var acumulativo = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        expression = findViewById(R.id.expression)
        result = findViewById(R.id.result)
        clear = findViewById(R.id.clear)
        zero = findViewById(R.id.zero)
        one = findViewById(R.id.one)
        two = findViewById(R.id.two)
        three = findViewById(R.id.three)
        four = findViewById(R.id.four)
        five = findViewById(R.id.five)
        six = findViewById(R.id.six)
        seven = findViewById(R.id.seven)
        eight = findViewById(R.id.eight)
        nine = findViewById(R.id.nine)
        plus = findViewById(R.id.plus)
        minus = findViewById(R.id.minus)
        divide = findViewById(R.id.divide)
        multiply = findViewById(R.id.multiply)
        opening = findViewById(R.id.opening)
        closing = findViewById(R.id.closing)
        pow = findViewById(R.id.pow)
        backSpace = findViewById(R.id.backSpace)
        equal = findViewById(R.id.equal)

        zero.setOnClickListener { addToExpression("0") }
        one.setOnClickListener { addToExpression("1") }
        two.setOnClickListener { addToExpression("2") }
        three.setOnClickListener { addToExpression("3") }
        four.setOnClickListener { addToExpression("4") }
        five.setOnClickListener { addToExpression("5") }
        six.setOnClickListener { addToExpression("6") }
        seven.setOnClickListener { addToExpression("7") }
        eight.setOnClickListener { addToExpression("8") }
        nine.setOnClickListener { addToExpression("9") }
        plus.setOnClickListener { addToExpression("+") }
        minus.setOnClickListener { addToExpression("-") }
        divide.setOnClickListener { addToExpression("/") }
        multiply.setOnClickListener { addToExpression("*") }
        opening.setOnClickListener { addToExpression("(") }
        closing.setOnClickListener { addToExpression(")") }
        pow.setOnClickListener { addToExpression("^") }
        equal.setOnClickListener {
            if (isValidExpression(acumulativo)) {
                result.text = OperarPostfix(PostFixConversion(acumulativo)).mostrarResultado()
            } else {
                result.text = "Sintaxis inválida"
            }
        }

        clear.setOnClickListener { clearAll() }
        backSpace.setOnClickListener { removeLastFromExpression() }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun removeLastFromExpression() {
        if (acumulativo.isNotEmpty()) {
            acumulativo = acumulativo.dropLast(1)
            expression.text = if (acumulativo.isEmpty()) "0" else acumulativo
        }
    }
    private fun isValidExpression(expression: String): Boolean {
        var balance = 0
        for (char in expression) {
            when (char) {
                '(' -> balance++
                ')' -> balance--
            }
            if (balance < 0) return false
        }
        return balance == 0
    }

    private fun addToExpression(value: String) {
        acumulativo += value
        expression.text = acumulativo
    }

    private fun clearAll() {
        acumulativo = ""
        expression.text = "0"
        result.text = "0"
    }

    fun PostFixConversion(string: String): String {
        var resultado = "" // Variable que almacenará el resultado en notación postfix
        val stack = ArrayDeque<Char>() // Pila para manejar operadores y paréntesis
        var i = 0 // Índice para recorrer la cadena de entrada

        while (i < string.length) {
            val s = string[i] // Carácter actual

            if (s.isDigit()) { // Si el carácter es un dígito
                resultado += s // Agregar el dígito al resultado
                // Manejar números de múltiples dígitos
                while (i + 1 < string.length && string[i + 1].isDigit()) {
                    resultado += string[i + 1] // Agregar dígitos adicionales al resultado
                    i++ // Avanzar el índice
                }
                resultado += " " // Agregar un espacio después del número
            } else if (s == '(') { // Si el carácter es un paréntesis de apertura
                stack.push(s) // Empujar el paréntesis en la pila
            } else if (s == ')') { // Si el carácter es un paréntesis de cierre
                // Desapilar y agregar al resultado hasta encontrar un paréntesis de apertura
                while (stack.isNotEmpty() && stack.peek() != '(') {
                    resultado += "${stack.pop()} "
                }
                if (stack.isNotEmpty()) stack.pop() // Eliminar el paréntesis de apertura
            } else if (notNumeric(s)) { // Si el carácter es un operador
                // Desapilar y agregar al resultado mientras el operador en la cima de la pila tenga mayor o igual precedencia
                while (stack.isNotEmpty() && operatorPrecedence(s) <= operatorPrecedence(stack.peek()!!)) {
                    resultado += "${stack.pop()} "
                }
                stack.push(s) // Empujar el operador en la pila
            }
            i++ // Avanzar el índice
        }

        // Desapilar y agregar al resultado todos los operadores restantes en la pila
        while (stack.isNotEmpty()) {
            if (stack.peek() == '(') return "Error" // Si queda un paréntesis de apertura, hay un error en la expresión
            resultado += "${stack.pop()} "
        }
        return resultado.trim() // Devolver el resultado sin espacios adicionales al final
    }

    // Método para verificar si un carácter no es un dígito
    fun notNumeric(ch: Char): Boolean = when (ch) {
        '+', '-', '*', '/', '(', ')', '^' -> true // Operadores y paréntesis no son numéricos
        else -> false // Cualquier otro carácter se considera numérico
    }

    // Método para determinar la precedencia de un operador
    fun operatorPrecedence(ch: Char): Int = when (ch) {
        '+', '-' -> 1 // Suma y resta tienen la precedencia más baja
        '*', '/' -> 2 // Multiplicación y división tienen precedencia intermedia
        '^' -> 3 // La exponenciación tiene la precedencia más alta
        else -> -1 // Cualquier otro carácter tiene precedencia inválida
    }

    // Funciónes de extensión
    fun <T> ArrayDeque<T>.push(element: T) = addLast(element)
    fun <T> ArrayDeque<T>.pop() = removeLastOrNull()
    fun <T> ArrayDeque<T>.peek() = lastOrNull()

    class OperarPostfix(private val operacion: String) {
        private val caracteres = operacion.trim().split(" ")
        private val stack = Stack<Int>()

        fun mostrarResultado(): String {
            for (char in caracteres) {
                if (char.toIntOrNull() != null) {
                    stack.push(char.toInt())
                } else {
                    val operandoB = stack.pop() // Notar el cambio del orden de los operandos
                    val operandoA = stack.pop()

                    when (char) {
                        "+" -> stack.push(sumar(operandoA, operandoB))
                        "-" -> stack.push(restar(operandoA, operandoB))
                        "*" -> stack.push(multiplicar(operandoA, operandoB))
                        "/" -> stack.push(dividir(operandoA, operandoB))
                        "^" -> stack.push(potencia(operandoA, operandoB))
                    }
                }
            }
            return stack.peek().toString()
        }

        private fun sumar(a: Int, b: Int): Int = a + b
        private fun restar(a: Int, b: Int): Int = a - b
        private fun multiplicar(a: Int, b: Int): Int = a * b
        private fun dividir(a: Int, b: Int): Int = a / b
        private fun potencia(a: Int, b: Int): Int = Math.pow(a.toDouble(), b.toDouble()).toInt()
    }
}
