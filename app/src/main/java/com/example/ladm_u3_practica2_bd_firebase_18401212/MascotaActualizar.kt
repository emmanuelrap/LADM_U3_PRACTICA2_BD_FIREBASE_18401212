package com.example.ladm_u3_practica2_bd_firebase_18401212

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.ladm_u3_practica2_bd_firebase_18401212.databinding.ActivityMascotaActualizarBinding
import com.example.ladm_u3_practica2_bd_firebase_18401212.databinding.ActivityPropietarioActualizarBinding
import com.google.firebase.firestore.FirebaseFirestore

class MascotaActualizar : AppCompatActivity() {
    var idActualizar=""
    var idActualizarPropietario=""
    lateinit var binding: ActivityMascotaActualizarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMascotaActualizarBinding.inflate(layoutInflater)
        setContentView(binding.root)

       setTitle("Actualizar Mascota")

        idActualizar=intent.extras!!.getString("idActualizar")!!
        idActualizarPropietario=intent.extras!!.getString("idActualizarPropietario")!!

        val baseRemota= FirebaseFirestore.getInstance()// ----- M A S C O T A  (EVENTO)---------------
        baseRemota.collection("mascota")
            .document(idActualizar)
            .get()
            .addOnSuccessListener {
                binding.etCurpPropietario.setText(it.getString("curp_propietario"))
                binding.etID.setText(it.getString("id_mascota"))
                binding.etNombre.setText(it.getString("nombre")) // Numerico
                binding.etRaza.setText(it.getString("raza"))
            }
            .addOnFailureListener{
                AlertDialog.Builder(this)
                    .setTitle("ERROR")
                    .setMessage(it.message)
                    .show()
            }//--------------------------------------------------------------------------------


        baseRemota.collection("propietario")// ----- P R O P I E T A R I O  (EVENTO)---------------
            .document(idActualizarPropietario)
            .get()
            .addOnSuccessListener {
                binding.etCurp.setText(it.getString("curp"))
                binding.etEdadPropietario.setText(it.getString("edad"))
                binding.etNombrePropietario.setText(it.getString("nombre")) // Numerico
                binding.etTelefono.setText(it.getString("telefono"))
            }
            .addOnFailureListener{
                AlertDialog.Builder(this)
                    .setTitle("ERROR")
                    .setMessage(it.message)
                    .show()
            }//--------------------------------------------------------------------------------

        binding.btnRegresar.setOnClickListener {
            finish()
        }

        binding.btnActualizar.setOnClickListener {
            val baseRemota = FirebaseFirestore.getInstance()
            baseRemota.collection("propietario")//---------PROPIETARIO (BOTON)--------------------------
                .document(idActualizarPropietario)
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
                    Toast.makeText(this,"Se actualizo Correctamente", Toast.LENGTH_LONG).show()
                    binding.etEdadPropietario.text.clear()
                    binding.etTelefono.text.clear()
                    binding.etEdadPropietario.text.clear()
                    binding.etNombrePropietario.text.clear()
                    finish()
                }//--------------------------

            baseRemota.collection("mascota")//---------MASCOTA (BOTON)---------------------------
                .document(idActualizar)
                .update("nombre",binding.etNombre.text.toString(),
                    "curp_propietario",binding.etCurp.text.toString(),
                    "raza",binding.etRaza.text.toString(),
                    "id_mascota",binding.etID.text.toString()
                )
                .addOnFailureListener() {
                    AlertDialog.Builder(this)
                        .setTitle("ERROR")
                        .setMessage(it.message)
                        .show()
                }
                .addOnSuccessListener {
                    Toast.makeText(this,"Se actualizo Correctamente", Toast.LENGTH_LONG).show()
                    binding.etID.text.clear()
                    binding.etCurpPropietario.text.clear()
                    binding.etNombre.text.clear()
                    binding.etRaza.text.clear()
                    finish()
                }//--MASCOTA---
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
