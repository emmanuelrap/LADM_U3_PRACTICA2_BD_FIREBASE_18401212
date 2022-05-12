package com.example.ladm_u3_practica2_bd_firebase_18401212

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.ladm_u3_practica2_bd_firebase_18401212.databinding.ActivityPropietarioActualizarBinding
import com.google.firebase.firestore.FirebaseFirestore

class PropietarioActualizar : AppCompatActivity() {
    var idActualizar=""
    lateinit var binding: ActivityPropietarioActualizarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPropietarioActualizarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle("Actualizar Propietario")

        idActualizar=intent.extras!!.getString("idActualizar")!!

        val baseRemota= FirebaseFirestore.getInstance()
        baseRemota.collection("propietario")
            .document(idActualizar)
            .get()
            .addOnSuccessListener {
                binding.etCurp.setText(it.getString("curp"))
                binding.etNombrePropietario.setText(it.getString("nombre"))
                binding.etEdadPropietario.setText(it.getString("edad")) // Numerico
                binding.etTelefono.setText(it.getString("telefono"))
            }
            .addOnFailureListener{
                AlertDialog.Builder(this)
                    .setTitle("ERROR")
                    .setMessage(it.message)
                    .show()
            }

        binding.btnRegresar.setOnClickListener {
            finish()
        }

        binding.btnActualizar.setOnClickListener {
            val baseRemota = FirebaseFirestore.getInstance()
            baseRemota.collection("propietario")
                .document(idActualizar)
                .update("nombre",binding.etNombrePropietario.text.toString(),
                    "curp",binding.etCurp.text.toString(),
                                      "edad",binding.etEdadPropietario.text.toString(),
                                      "telefono",binding.etTelefono.text.toString()
                       )
                        .addOnFailureListener() {
                            AlertDialog.Builder(this)
                                .setTitle("ERROR")
                                .setMessage(it.message)
                                .show()
                        }
                        .addOnSuccessListener {
                            Toast.makeText(this,"Se actualizo Correctamente",Toast.LENGTH_LONG).show()
                            binding.etTelefono.text.clear()
                            binding.etEdadPropietario.text.clear()
                            binding.etNombrePropietario.text.clear()
                            binding.etCurp.text.clear()
                            finish()
                        }
        }

    }

    private fun alerta(titulo:String,mensaje: String)
    {
        AlertDialog.Builder(this)
            .setTitle(titulo)
            .setMessage(mensaje)
            .show()

    }

    private fun toast(mensaje:String){
        Toast.makeText(this,mensaje, Toast.LENGTH_LONG).show()
    }
}
