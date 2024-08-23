package com.example.conversordeunidades

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.*

class ConversorUnidadesActivity : AppCompatActivity() {

    private var selectedView: CardView? = null

    private val listaMasa= listOf("Tonelada","Kilogramos","Libra","Gramos","Onza")
    private val listaLongitud= listOf("Kilometro","Metro","Centimetro","Pie","Pulgada")
    private val listaTiempo= listOf("Día","Hora","Minuto","Segundo","Milisegundo")
    private val listaTemperatura= listOf("Kelvin","Fahrenheit","Celsius")

    private lateinit var vistaMasa:CardView
    private lateinit var vistaLongitud:CardView
    private lateinit var vistaTiempo:CardView
    private lateinit var vistaTemp:CardView
    private lateinit var autoCompleteDesde: AutoCompleteTextView
    private lateinit var autoCompleteHasta: AutoCompleteTextView
    private lateinit var adapterDesde: ArrayAdapter<String>
    private lateinit var adapterHasta: ArrayAdapter<String>
    private lateinit var numberTo: EditText
    private lateinit var resultado: TextView
    private lateinit var btnConvertir : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_conversor_unidades)

        inicializarVariables()
        configurarAdapter()
        initListeners()
        initUI()
        numberTo.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
            ) {
                realizarConversion()
                true
            } else {
                false
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun inicializarVariables() {
        autoCompleteDesde = findViewById(R.id.auto_complete)
        autoCompleteHasta = findViewById((R.id.auto_complete2))
        vistaMasa = findViewById(R.id.vistaMasa)
        vistaLongitud = findViewById(R.id.VistaLongitud)
        vistaTiempo = findViewById(R.id.vistaTiempo)
        vistaTemp  = findViewById(R.id.vistaTemp)
        numberTo = findViewById(R.id.numberTo)
        resultado = findViewById(R.id.resultado)
        btnConvertir = findViewById(R.id.btnConvertir)

    }

    private fun configurarAdapter() {
        adapterDesde = ArrayAdapter(this, R.layout.list_item, listaMasa)
        adapterHasta = ArrayAdapter(this, R.layout.list_item, listaMasa)
    }

    private fun initListeners() {
        autoCompleteDesde.setAdapter(adapterDesde)
        autoCompleteHasta.setAdapter(adapterHasta)

        autoCompleteDesde.onItemClickListener = AdapterView.OnItemClickListener { adapterView, _, i, _ ->
            val itemSelected = adapterView.getItemAtPosition(i)
            mostrarToast("Unidad: $itemSelected")
        }

        autoCompleteHasta.onItemClickListener = AdapterView.OnItemClickListener { adapterView, _, i, _ ->
            val itemSelected = adapterView.getItemAtPosition(i)
            mostrarToast("Unidad: $itemSelected")
        }

        val views = listOf(vistaMasa, vistaLongitud, vistaTiempo, vistaTemp)
        views.forEach { view ->
            view.setOnClickListener {
                selectView(view)
                updateUnidades(view)
                numberTo.setText("") // Resetear el valor del EditText-
                resultado.text = "" // Limpiar el resultado
            }
        }

        btnConvertir.setOnClickListener {
            mostrarResultado()}
        btnConvertir.setOnClickListener {
            realizarConversion()
        }
    }

    private fun updateUnidades(view : CardView){
        val lista = when (view) {
            vistaMasa -> listaMasa
            vistaLongitud -> listaLongitud
            vistaTiempo -> listaTiempo
            vistaTemp -> listaTemperatura
            else -> listaMasa // Por defecto
        }
        adapterDesde = ArrayAdapter(this, R.layout.list_item, lista)
        adapterHasta = ArrayAdapter(this, R.layout.list_item, lista)
        autoCompleteDesde.setAdapter(adapterDesde)
        autoCompleteHasta.setAdapter(adapterHasta)

        // Limpiar las selecciones
        autoCompleteDesde.text.clear()
        autoCompleteHasta.text.clear()
    }

    private fun mostrarToast(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }

    private fun realizarConversion() {
        hideKeyboard()
        mostrarResultado()
    }
    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(numberTo.windowToken, 0)
    }


    private fun getBackgroundColor(isSelected: Boolean): Int {
        val colorReference = if (isSelected) {
            R.color.background_component_selected
        } else {
            R.color.background_component
        }
        return ContextCompat.getColor(this, colorReference)
    }

    private fun selectView(view: CardView) {
        // Deseleccionar la vista previamente seleccionada
        selectedView?.setCardBackgroundColor(getBackgroundColor(false))

        // Seleccionar la nueva vista
        if (selectedView != view) {
            view.setCardBackgroundColor(getBackgroundColor(true))
            selectedView = view
        } else {
            // Si se hace clic en la misma vista, no la deseleccionamos
            // para mantener siempre una opción seleccionada
            view.setCardBackgroundColor(getBackgroundColor(true))
        }
    }


    private fun convertirMasa(valor:Double, unidadDesde: String, unidadHasta: String):Double{
        return when{

            //De tonelada a otras unidades
            unidadDesde =="Tonelada" && unidadHasta == "Kilogramos" -> valor * 1000
            unidadDesde =="Tonelada" && unidadHasta == "Libra" -> valor * 2205
            unidadDesde =="Tonelada" && unidadHasta == "Gramos" -> valor * 1e+6
            unidadDesde =="Tonelada" && unidadHasta == "Onza" -> valor * 35270

            //De kiogramos a otras unidades
            unidadDesde == "Kilogramos" && unidadHasta == "Tonelada" -> valor / 1000
            unidadDesde == "Kilogramos" && unidadHasta =="Libra" -> valor * 2.205
            unidadDesde == "Kilogramos" && unidadHasta =="Onza" -> valor * 35.274
            unidadDesde == "Kilogramos" && unidadHasta =="Gramos" -> valor * 1000

            // Libra a otras unidades
            unidadDesde == "Libra" && unidadHasta == "Tonelada" -> valor / 2204.62
            unidadDesde == "Libra" && unidadHasta == "Kilogramos" -> valor / 2.20462
            unidadDesde == "Libra" && unidadHasta == "Gramos" -> valor * 453.592
            unidadDesde == "Libra" && unidadHasta == "Onza" -> valor * 16

            // Gramos a otras unidades
            unidadDesde == "Gramos" && unidadHasta == "Tonelada" -> valor / (1.0*10).pow(-6)
            unidadDesde == "Gramos" && unidadHasta == "Kilogramos" -> valor / 1000
            unidadDesde == "Gramos" && unidadHasta == "Libra" -> valor / 453.592
            unidadDesde == "Gramos" && unidadHasta == "Onza" -> valor / 28.3495

            // Onza a otras unidades
            unidadDesde == "Onza" && unidadHasta == "Tonelada" -> valor / 35273.96
            unidadDesde == "Onza" && unidadHasta == "Kilogramos" -> valor / 35.274
            unidadDesde == "Onza" && unidadHasta == "Libra" -> valor / 16
            unidadDesde == "Onza" && unidadHasta == "Gramos" -> valor * 28.3495

            else -> valor
        }
    }
    fun convertirLongitud(valor: Double, unidadDesde: String, unidadHasta: String): Double {
        return when {
            // Kilómetro a otras unidades
            unidadDesde == "Kilometro" && unidadHasta == "Metro" -> valor * 1000
            unidadDesde == "Kilometro" && unidadHasta == "Centimetro" -> valor * 100000
            unidadDesde == "Kilometro" && unidadHasta == "Pie" -> valor * 3280.84
            unidadDesde == "Kilometro" && unidadHasta == "Pulgada" -> valor * 39370.1

            // Metro a otras unidades
            unidadDesde == "Metro" && unidadHasta == "Kilometro" -> valor / 1000
            unidadDesde == "Metro" && unidadHasta == "Centimetro" -> valor * 100
            unidadDesde == "Metro" && unidadHasta == "Pie" -> valor * 3.28084
            unidadDesde == "Metro" && unidadHasta == "Pulgada" -> valor * 39.3701

            // Centímetro a otras unidades
            unidadDesde == "Centimetro" && unidadHasta == "Kilometro" -> valor / 100000
            unidadDesde == "Centimetro" && unidadHasta == "Metro" -> valor / 100
            unidadDesde == "Centimetro" && unidadHasta == "Pie" -> valor / 30.48
            unidadDesde == "Centimetro" && unidadHasta == "Pulgada" -> valor / 2.54

            // Pie a otras unidades
            unidadDesde == "Pie" && unidadHasta == "Kilometro" -> valor / 3280.84
            unidadDesde == "Pie" && unidadHasta == "Metro" -> valor / 3.28084
            unidadDesde == "Pie" && unidadHasta == "Centimetro" -> valor * 30.48
            unidadDesde == "Pie" && unidadHasta == "Pulgada" -> valor * 12

            // Pulgada a otras unidades
            unidadDesde == "Pulgada" && unidadHasta == "Kilometro" -> valor / 39370.1
            unidadDesde == "Pulgada" && unidadHasta == "Metro" -> valor / 39.3701
            unidadDesde == "Pulgada" && unidadHasta == "Centimetro" -> valor * 2.54
            unidadDesde == "Pulgada" && unidadHasta == "Pie" -> valor / 12

            else -> valor
        }
    }

    fun convertirTiempo(valor: Double, unidadDesde: String, unidadHasta: String): Double {
        return when {
            // Día a otras unidades
            unidadDesde == "Día" && unidadHasta == "Hora" -> valor * 24
            unidadDesde == "Día" && unidadHasta == "Minuto" -> valor * 1440
            unidadDesde == "Día" && unidadHasta == "Segundo" -> valor * 86400
            unidadDesde == "Día" && unidadHasta == "Milisegundo" -> valor * 86400000

            // Hora a otras unidades
            unidadDesde == "Hora" && unidadHasta == "Día" -> valor / 24
            unidadDesde == "Hora" && unidadHasta == "Minuto" -> valor * 60
            unidadDesde == "Hora" && unidadHasta == "Segundo" -> valor * 3600
            unidadDesde == "Hora" && unidadHasta == "Milisegundo" -> valor * 3600000

            // Minuto a otras unidades
            unidadDesde == "Minuto" && unidadHasta == "Día" -> valor / 1440
            unidadDesde == "Minuto" && unidadHasta == "Hora" -> valor / 60
            unidadDesde == "Minuto" && unidadHasta == "Segundo" -> valor * 60
            unidadDesde == "Minuto" && unidadHasta == "Milisegundo" -> valor * 60000

            // Segundo a otras unidades
            unidadDesde == "Segundo" && unidadHasta == "Día" -> valor / 86400
            unidadDesde == "Segundo" && unidadHasta == "Hora" -> valor / 3600
            unidadDesde == "Segundo" && unidadHasta == "Minuto" -> valor / 60
            unidadDesde == "Segundo" && unidadHasta == "Milisegundo" -> valor * 1000

            // Milisegundo a otras unidades
            unidadDesde == "Milisegundo" && unidadHasta == "Día" -> valor / 86400000
            unidadDesde == "Milisegundo" && unidadHasta == "Hora" -> valor / 3600000
            unidadDesde == "Milisegundo" && unidadHasta == "Minuto" -> valor / 60000
            unidadDesde == "Milisegundo" && unidadHasta == "Segundo" -> valor / 1000

            else -> valor
        }
    }

    fun convertirTemperatura(valor: Double, unidadDesde: String, unidadHasta: String): Double {
        return when {
            // Kelvin a otras unidades
            unidadDesde == "Kelvin" && unidadHasta == "Fahrenheit" -> valor * 9/5 - 459.67
            unidadDesde == "Kelvin" && unidadHasta == "Celsius" -> valor - 273.15

            // Fahrenheit a otras unidades
            unidadDesde == "Fahrenheit" && unidadHasta == "Kelvin" -> (valor + 459.67) * 5/9
            unidadDesde == "Fahrenheit" && unidadHasta == "Celsius" -> (valor - 32) * 5/9

            // Celsius a otras unidades
            unidadDesde == "Celsius" && unidadHasta == "Kelvin" -> valor + 273.15
            unidadDesde == "Celsius" && unidadHasta == "Fahrenheit" -> valor * 9/5 + 32

            else -> valor
        }
    }

    private fun mostrarResultado() {
        val valorInput = numberTo.text.toString().toDoubleOrNull()
        val unidadDesde = autoCompleteDesde.text.toString()
        val unidadHasta = autoCompleteHasta.text.toString()

        if (valorInput == null) {
            mostrarToast("Por favor, ingrese un valor válido")
            return
        }
        if (unidadDesde.isEmpty() || unidadHasta.isEmpty()) {
            mostrarToast("Por favor, seleccione las unidades de conversión")
            return
        }

        val resultadoConversion = when (selectedView) {
            vistaMasa -> convertirMasa(valorInput, unidadDesde, unidadHasta)
            vistaLongitud -> convertirLongitud(valorInput, unidadDesde, unidadHasta)
            vistaTiempo -> convertirTiempo(valorInput, unidadDesde, unidadHasta)
            vistaTemp -> convertirTemperatura(valorInput, unidadDesde, unidadHasta)
            else -> valorInput
        }

        resultado.text = String.format("%.3f", resultadoConversion)
    }

    private fun initUI() {
        // la Masa estará seleccionada por defecto
        selectView(vistaMasa)
        updateUnidades(vistaMasa)
    }
}