package com.example.ladm_u3_practica2_bd_firebase_18401212.ui.Propietario

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.ladm_u3_practica2_bd_firebase_18401212.PropietarioActualizar
import com.example.ladm_u3_practica2_bd_firebase_18401212.databinding.FragmentDuenoRegistroBinding
import com.google.firebase.firestore.FirebaseFirestore


class GalleryFragment : Fragment() {

    private var _binding: FragmentDuenoRegistroBinding? = null
    val arregloDatos = ArrayList<String>()
    var arregloIDs = ArrayList<String>()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(GalleryViewModel::class.java)

        _binding = FragmentDuenoRegistroBinding.inflate(inflater, container, false)
        val root: View = binding.root



        FirebaseFirestore.getInstance()
            .collection("propietario")
            .addSnapshotListener { query, error ->
                arregloDatos.clear() // si no se pone te estara duplicando datos

                if(error!=null){
                    //si hubo error
                    AlertDialog.Builder(requireContext())
                        .setMessage(error.message)
                        .show()
                    return@addSnapshotListener //pasa salirme
                }


                for(documento in query!!){//ciclo que recoje los datos de la colleccion
                    var cadena = "Curp: ${documento.getString("curp")}\n" +
                            " Nombre: ${documento.getString("nombre") }"+
                            " Edad: ${documento.getString("edad") }"+
                            " telefono: ${documento.getString("telefono") }"
                    arregloDatos.add(cadena)
                    arregloIDs.add(documento.id) //obtiene el ID de los documentos
                }

                binding.listaPropietario.adapter= ArrayAdapter(requireContext(), R.layout.simple_list_item_1,arregloDatos)

                binding.listaPropietario.setOnItemClickListener { adapterView, view, posicion, l ->
                    val idsFB = arregloIDs.get(posicion)
                    AlertDialog.Builder(requireContext())
                        .setMessage("¿Desea Elimnar o Modificar a [ ${idsFB} ]?")
                        .setNegativeButton("Eliminar") {d,i ->
                            eliminar(idsFB)
                        }
                        .setPositiveButton("Actualizar") {d,i ->
                            actualizar(idsFB)
                        }
                        .setNeutralButton("Cerrar") {d,i -> }
                        .show()

                }

            } //---------------------------------------------------------------------------------

        binding.insertar.setOnClickListener {
            val baseRemota = FirebaseFirestore.getInstance()
            val datos= hashMapOf(
                "nombre" to binding.etNombre.text.toString(),
                "curp" to binding.etCurp.text.toString(),
                "edad" to binding.etEdad.text.toString(),
                "telefono" to binding.etTelefono.text.toString()
            )

            baseRemota.collection("propietario")
                .add(datos)
                .addOnSuccessListener { Toast.makeText(requireContext(),"Exito!, Si se Inserto correctamente",
                    Toast.LENGTH_LONG).show() } //si se pudo
                .addOnFailureListener {
                    AlertDialog.Builder(requireContext())
                        .setMessage(it.message)
                        .show()
                } //no se pudo
            binding.etNombre.setText("")
            binding.etEdad.setText("")
            binding.etCurp.setText("")
            binding.etTelefono.setText("")

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

    private fun actualizar(idActualizar:String) {
        var intent= Intent(requireContext(),PropietarioActualizar::class.java)
        intent.putExtra("idActualizar",idActualizar)
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
        Toast.makeText(requireContext(),mensaje,Toast.LENGTH_LONG).show()
    }







    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}