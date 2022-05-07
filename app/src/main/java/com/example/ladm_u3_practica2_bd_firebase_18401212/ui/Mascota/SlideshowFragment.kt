package com.example.ladm_u3_practica2_bd_firebase_18401212.ui.Mascota

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.ladm_u3_practica2_bd_firebase_18401212.databinding.FragmentSlideshowBinding
import com.example.ladm_u3_practica2_bd_firebase_18401212.MascotaActualizar
import com.google.firebase.firestore.FirebaseFirestore

class SlideshowFragment : Fragment() {

    private var _binding: FragmentSlideshowBinding? = null
    private val binding get() = _binding!!
    val arregloPropietarios = ArrayList<String>()
    val arregloMascota = ArrayList<String>()
    var arregloIDsMascota = ArrayList<String>()
    var arregloIDsPropietarios = ArrayList<String>()
    var arregloCurps = ArrayList<String>()
    var arregloNombreMascota = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val slideshowViewModel =
            ViewModelProvider(this).get(SlideshowViewModel::class.java)

        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding.root

        FirebaseFirestore.getInstance()//---------------P R O P I E T A R I O-------------------------------
            .collection("propietario")
            .addSnapshotListener { query, error ->
                arregloPropietarios.clear() // si no se pone te estara duplicando datos

                if(error!=null){
                    //si hubo error
                    AlertDialog.Builder(requireContext())
                        .setMessage(error.message)
                        .show()
                    return@addSnapshotListener //pasa salirme
                }


                for(documento in query!!){//ciclo que recoje los datos de la colleccion
                    var cadena = "Curp: ${documento.getString("curp")}\n" +
                                 " Nombre: ${documento.getString("nombre") }"
                    arregloPropietarios.add(cadena)
                    arregloIDsPropietarios.add(documento.id) //obtiene el ID de los documentos
                    arregloCurps.add(""+documento.getString("curp"))
                }

                binding.listaPropietario.adapter= ArrayAdapter(requireContext(), R.layout.simple_list_item_1,arregloPropietarios)
                binding.listaPropietario.setOnItemClickListener { adapterView, view, posicion, l ->
                    AlertDialog.Builder(requireContext())
                        .setMessage("¿Desea agregar como propietario a [ ${arregloPropietarios.get(posicion)} ]?")
                        .setPositiveButton("Si") {d,i ->
                            binding.etCurp.setText(arregloCurps.get(posicion))
                        }
                        .setNeutralButton("Cerrar") {d,i -> }
                        .show()
                }
            } //---------------------------------------------------------------------------------


        FirebaseFirestore.getInstance()//--------------M A S C O T A ---------------------------------------
            .collection("mascota")
            .addSnapshotListener { query, error ->
                arregloMascota.clear() // si no se pone te estara duplicando datos

                if(error!=null){
                    //si hubo error
                    AlertDialog.Builder(requireContext())
                        .setMessage(error.message)
                        .show()
                    return@addSnapshotListener //para salirme
                }


                for(documento in query!!){//ciclo que recoje los datos de la colleccion
                    var cadena = "idMascota: ${documento.getString("id_mascota")}\n" +
                                 "Raza: ${documento.getString("raza")}\n"+
                                 "Mascota: ${documento.getString("nombre")}\n"+
                                 "Dueño: ${documento.getString("curp_propietario") }"
                    arregloMascota.add(cadena)
                    arregloNombreMascota.add(""+documento.getString("nombre"))
                    arregloIDsMascota.add(documento.id) //obtiene el ID de los documentos
                }

                binding.listaMascotas.adapter= ArrayAdapter(requireContext(), R.layout.simple_list_item_1,arregloMascota)
                binding.listaMascotas.setOnItemClickListener { adapterView, view, posicion, l ->
                    val idsMascotasFB = arregloIDsMascota.get(posicion)
                    val idsPropietariosFB = arregloIDsPropietarios.get(posicion)

                    AlertDialog.Builder(requireContext())
                        .setMessage("¿Desea Elimnar o Modificar a [ ${arregloNombreMascota.get(posicion)} ]?")
                        .setNegativeButton("Eliminar") {d,i ->
                            eliminar(idsMascotasFB)
                        }
                        .setPositiveButton("Actualizar") {d,i ->
                            actualizar(idsMascotasFB,idsPropietariosFB)
                        }
                        .setNeutralButton("Cerrar") {d,i -> }
                        .show()
                }
            } //---------------------------------------------------------------------------------

        binding.btnInsertarMascota.setOnClickListener {
            val baseRemota = FirebaseFirestore.getInstance()
            val datos= hashMapOf(
                "nombre" to binding.etNombreMascota.text.toString(),
                "curp_propietario" to binding.etCurp.text.toString(),
                "raza" to binding.etRaza.text.toString(),
                "id_mascota" to (arregloNombreMascota.size+1).toString()
            )

            baseRemota.collection("mascota")
                .add(datos)
                .addOnSuccessListener { Toast.makeText(requireContext(),"Exito!, Si se Inserto correctamente",
                    Toast.LENGTH_LONG).show() } //si se pudo
                .addOnFailureListener {
                    AlertDialog.Builder(requireContext())
                        .setMessage(it.message)
                        .show()
                } //no se pudo
            binding.etRaza.setText("")
            binding.etNombreMascota.setText("")
            binding.etCurp.setText("")

        }//boton para intertar en BDremota



        return root
    }

    private fun eliminar(idEliminar:String) {
        val baseRemota = FirebaseFirestore.getInstance()
        alerta("mensaje","El id que se elimino fue: ${idEliminar}")
        baseRemota.collection("propietario")
            .document(idEliminar)
            .delete()
            .addOnSuccessListener {
                toast("Se Elimino Correctamente")
            }
            .addOnFailureListener{
                alerta("Error","Hubo ERROR: ${it.message!!}")
            }
    }

    private fun actualizar(idActualizar:String,idActualizarPropietario:String) {
        var intent= Intent(requireContext(), MascotaActualizar::class.java)
        intent.putExtra("idActualizar",idActualizar)
        intent.putExtra("idActualizarPropietario",idActualizarPropietario)
        startActivity(intent)
    }



    private fun alerta(titulo:String,mensaje: String)
    {
        AlertDialog.Builder(requireContext())
            .setTitle(titulo)
            .setMessage(mensaje)
            .show()

    }

    private fun toast(mensaje:String){
        Toast.makeText(requireContext(),mensaje, Toast.LENGTH_LONG).show()
    }
}