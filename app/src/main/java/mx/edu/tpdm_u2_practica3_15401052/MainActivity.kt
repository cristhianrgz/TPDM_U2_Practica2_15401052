package mx.edu.tpdm_u2_practica3_15401052

import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*
import java.sql.SQLException

class MainActivity : AppCompatActivity() {

    var editNombre : EditText ?= null
    var editDomicilio : EditText ?= null
    var editTelefono : EditText ?= null
    var insertarCl : Button ?= null
    var buscarCl : Button ?= null
    var actualizarCl : Button ?= null
    var eliminarCl : Button ?= null
    var listaClView : ListView ?= null
    //-------------------------------------
    var basedatos = BaseDatos(this, "practica2", null, 1)
    var listaCl : ArrayList<String> = ArrayList()
    var cadena = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        editNombre = findViewById(R.id.editNombreCl)
        editDomicilio = findViewById(R.id.editDomicilioCl)
        editTelefono = findViewById(R.id.editTelefonoCl)
        insertarCl = findViewById(R.id.btnInsertarCl)
        buscarCl = findViewById(R.id.btnBuscarCl)
        actualizarCl = findViewById(R.id.btnActualizarCl)
        eliminarCl = findViewById(R.id.btnEliminarCl)
        listaClView = findViewById(R.id.clientesRegistrados)
        consultaGeneralClientes()

        insertarCl?.setOnClickListener {
            pedirIDInsert(insertarCl?.text.toString())
        }

        buscarCl?.setOnClickListener {
            pedirID(buscarCl?.text.toString())
        }

        actualizarCl?.setOnClickListener {

        }

        eliminarCl?.setOnClickListener {
            pedirID(eliminarCl?.text.toString())
        }
    }

    fun insertar(){
        try{
            var transaccion = basedatos.writableDatabase
            var SQL = "INSERT INTO CLIENTE VALUES(null,'NOTELEFONO', 'NOMBRE', 'DOMICILIO', IDEMPRESA)"
            SQL = SQL.replace("notelefono", editTelefono?.text.toString())
            SQL = SQL.replace("nombre", editNombre?.text.toString())
            SQL = SQL.replace("domicilio", editDomicilio?.text.toString())
            SQL = SQL.replace("idempresa", cadena)
            if(validarCampos() == false){
                mensaje("ERROR", "AL PARECER HAY UN CAMPO DE TEXTO VACIO")
                return
            }
            transaccion.execSQL(SQL)
            transaccion.close()
            mensaje("EXITO", "EL REGISTRO SE INSERTO CORRECTAMENTE")
            limpiarCampos()
        }catch(err: SQLiteException){
            mensaje("Error", "NO SE PUDO INSERTAR TALVEZ EL ID YA EXISTE")
        }
    }

    fun pedirIDInsert(etiqueta:String){
        var campo = EditText(this)

        AlertDialog.Builder(this).setTitle("ATENCION").setMessage("ESCRIBA EL ID DE LA EMPRESA A DONDE SE VA A ${etiqueta} EL CLIENTE: ").setView(campo)
            .setPositiveButton("OK"){dialog,which->
                if(validarCampo(campo) == false){
                    Toast.makeText(this@MainActivity, "ERROR CAMPO VACÍO", Toast.LENGTH_LONG).show()
                    return@setPositiveButton
                }
                buscarIDInsert(campo.text.toString(),etiqueta)


            }.setNeutralButton("CANCELAR"){dialog, which ->  }.show()
    }

    fun buscarIDInsert(id: String, btnEtiqueta: String){
        try {
            var transaccion = basedatos.readableDatabase
            var SQL="SELECT * FROM EMPRESA WHERE IDEMPRESA="+id
            var  respuesta = transaccion.rawQuery(SQL,null)
            if (respuesta.moveToFirst()==true){
                cadena = respuesta.getString(0)

                if (btnEtiqueta.startsWith("Insertar")) {
                    insertar()
                }

            }else{
                mensaje("ERROR","NO EXISTE LA EMPRESA")
            }
        }catch (err: SQLException){
            mensaje("ERROR","NO SE PUDO ENCONTRAR LA EMPRESA")
        }
    }
    //--------------------------------------------------
    fun actualizar() {
        try{
            var transaccion = basedatos.writableDatabase
            var SQL = "UPDATE CLIENTES SET TELEFONO='campotel', NOMBRE='camponom', DOMICILIO='campodom' WHERE IDCLIENTE='campoid'"
            if (validarCampos()==false){
                mensaje("ERROR","ALGUN CAMPO ESTA VACIO")
                return
            }
            SQL = SQL.replace("NOTELEFONO", editTelefono?.text.toString())
            SQL = SQL.replace("NOMBRE", editNombre?.text.toString())
            SQL = SQL.replace("DOMICILIO", editDomicilio?.text.toString())
            transaccion.execSQL(SQL)
            transaccion.close()
            mensaje("Exito", "Se actualizo correctamente")
        }catch (err: SQLiteException){
            mensaje("Error", "No se pudo actualizar")
        }
    }
    //--------------------------------------------------
    fun eliminar(id: String){
        try{
            var transaccion = basedatos.writableDatabase
            var SQL = "DELETE FROM CLIENTES WHERE IDCLIENTE="+id
            transaccion.execSQL(SQL)
            transaccion.close()
            mensaje("EXITO", "EL REGISTRO SE ELIMINO CORRECTAMENTE")
        }catch(err: SQLiteException){
            mensaje("ERROR", "NO SE PUDO ELIMINAR EL REGISTRO")
        }
    }

    //---------------------------------------------------------------
    fun pedirID(etiqueta:String){
        var campo = EditText(this)
        campo.inputType = InputType.TYPE_CLASS_NUMBER

        AlertDialog.Builder(this).setTitle("ATENCION").setMessage("ESCRIBA EL ID A  ${etiqueta}: ").setView(campo)
            .setPositiveButton("OK"){dialog,which ->
                if(validarCampo(campo) == false){
                    Toast.makeText(this@MainActivity, "ERROR CAMPO VACÍO", Toast.LENGTH_LONG).show()
                    return@setPositiveButton
                }
                buscar(campo.text.toString(),etiqueta)

            }.setNeutralButton("CANCELAR"){dialog, which ->  }.show()
    }

    fun buscar(id: String, btnEtiqueta: String){
        try{
            var transaccion = basedatos.readableDatabase
            var SQL = "SELECT * FROM CLIENTES WHERE IDCLIENTE="+id
            var respuesta = transaccion.rawQuery(SQL, null)

            if(respuesta.moveToFirst() == true){
                var cadena = "Nombre: "+respuesta.getString(2)+" Domicilio: "+respuesta.getString(3)+" Telefono: "+respuesta.getString(1)

                if(btnEtiqueta.startsWith("Eliminar")){
                    var cadena = "¿SEGURO QUE DESEA ELIMINAR [ "+respuesta.getString(2)+" ] CON ID [ "+respuesta.getString(0)+" ] ?"
                    var alerta = AlertDialog.Builder(this)
                    alerta.setTitle("ATENCION").setMessage(cadena).setNeutralButton("NO"){dialog,which->
                        return@setNeutralButton
                    }.setPositiveButton("si"){dialog,which->
                        eliminar(id)
                    }.show()
                }

                if(btnEtiqueta.startsWith("Buscar")){
                    AlertDialog.Builder(this).setTitle("CONSULTA POR ID").setMessage(cadena).setPositiveButton("Ok"){dialog, which -> }.show()
                }
            }
            transaccion.execSQL(SQL)
            transaccion.close()
        }catch (err: SQLiteException){
            mensaje("ERROR", "NO SE PUDO REALIZAR LA BUSQUEDA")
        }
    }

    fun consultaGeneralClientes(){
        try {
            var transaccion = basedatos.writableDatabase
            var SQL="SELECT * FROM CLIENTES"
            var  respuesta = transaccion.rawQuery(SQL,null)
            if (respuesta.moveToFirst()==true){
                do{
                    listaCl.add(respuesta.getString(0) + " - " + respuesta.getString(1)+" - " + respuesta.getString(2)+" - " + respuesta.getString(3))
                }while(respuesta.moveToNext())
            }
            respuesta.close()
            val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listaCl)
            listaClView?.setAdapter(adapter);
        }catch (err: SQLiteException){
            mensaje("ERROR","NO se pudo ejecutar la consulta")
        }
    }

    fun mensaje(titulo: String, mensaje: String){
        AlertDialog.Builder(this).setTitle(titulo).setMessage(mensaje).setPositiveButton("OK"){dialog, which -> }.show()
    }

    fun  validarCampos():Boolean{
        if(editTelefono?.text!!.isEmpty() || editDomicilio?.text!!.isEmpty() || editNombre?.text!!.isEmpty()){
            return false
        }else{
            return true
        }
    }

    fun validarCampo(campo: EditText): Boolean{
        if(campo.text.toString().isEmpty()){
            return false
        }else{
            return true
        }
    }

    fun limpiarCampos(){
        editTelefono?.setText("")
        editNombre?.setText("")
        editDomicilio?.setText("")
    }
}
