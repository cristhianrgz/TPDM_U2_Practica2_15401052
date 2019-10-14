package mx.edu.tpdm_u2_practica3_15401052

import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AlertDialog
import java.sql.SQLException

class Main2Activity : AppCompatActivity() {

    var editFecha : EditText?= null
    var editTotal : EditText?= null
    var insertarCom : Button?= null
    var buscarCom : Button?= null
    var actualizarCom : Button?= null
    var eliminarCom : Button?= null
    var listaComView : ListView?= null
    //-------------------------------------
    var basedatos = BaseDatos(this, "practica2", null, 1)
    var listaCom : ArrayList<String> = ArrayList()
    var cadena = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        editFecha = findViewById(R.id.editFechaCom)
        editTotal = findViewById(R.id.editTotalCom)
        insertarCom = findViewById(R.id.btnInsertarCom)
        buscarCom = findViewById(R.id.btnBuscarCom)
        actualizarCom = findViewById(R.id.btnActualizarCom)
        eliminarCom = findViewById(R.id.btnEliminarCom)
        listaComView = findViewById(R.id.comprasRegistrados)
        consultaGeneralClientes()

        insertarCom?.setOnClickListener {
            pedirIDInsert(insertarCom?.text.toString())
        }

        buscarCom?.setOnClickListener {
            pedirID(buscarCom?.text.toString())
        }

        actualizarCom?.setOnClickListener {

        }

        eliminarCom?.setOnClickListener {
            pedirID(eliminarCom?.text.toString())
        }
    }

    fun insertar(){
        try{
            var transaccion = basedatos.writableDatabase
            var SQL = "INSERT INTO COMPRA VALUES(null,'FECHA', IDCLIENTE, 'TOTAL')"
            SQL = SQL.replace("FECHA", editFecha?.text.toString())
            SQL = SQL.replace("IDCLIENTE", cadena)
            SQL = SQL.replace("TOTAL", editTotal?.text.toString())
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

        AlertDialog.Builder(this).setTitle("ATENCION").setMessage("ESCRIBA EL ID DEL CLIENTE AL QUE SE LE VA A ${etiqueta} LA COMPRA: ").setView(campo)
            .setPositiveButton("OK"){dialog,which->
                if(validarCampo(campo) == false){
                    Toast.makeText(this@Main2Activity, "ERROR CAMPO VACÍO", Toast.LENGTH_LONG).show()
                    return@setPositiveButton
                }
                buscarIDInsert(campo.text.toString(),etiqueta)


            }.setNeutralButton("CANCELAR"){dialog, which ->  }.show()
    }

    fun buscarIDInsert(id: String, btnEtiqueta: String){
        try {
            var transaccion = basedatos.readableDatabase
            var SQL="SELECT * FROM CLIENTE WHERE IDCLIENTE="+id
            var  respuesta = transaccion.rawQuery(SQL,null)
            if (respuesta.moveToFirst()==true){
                cadena = respuesta.getString(0)

                if (btnEtiqueta.startsWith("Insertar")) {
                    insertar()
                }

            }else{
                mensaje("ERROR","NO EXISTE EL ID")
            }
        }catch (err: SQLException){
            mensaje("ERROR","NO SE PUDO REALIZAR LA BUSQUEDA")
        }
    }
    //--------------------------------------------------

    fun actualizar() {
        try{
            var transaccion = basedatos.writableDatabase
            var SQL = "UPDATE COMPRA SET FECHA='campofecha', COMPRA='campocompra', IDCLIENTE='campoID' WHERE IDCOMPRA='campoid'"
            if (validarCampos()==false){
                mensaje("ERROR","ALGUN CAMPO ESTA VACIO")
                return
            }
            SQL = SQL.replace("FECHA", editFecha?.text.toString())
            SQL = SQL.replace("IDCLIENTE", cadena)
            SQL = SQL.replace("TOTAL", editTotal?.text.toString())
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
            var SQL = "DELETE FROM COMPRA WHERE IDCOMPRA="+id
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
                    Toast.makeText(this@Main2Activity, "ERROR CAMPO VACÍO", Toast.LENGTH_LONG).show()
                    return@setPositiveButton
                }
                buscar(campo.text.toString(),etiqueta)

            }.setNeutralButton("CANCELAR"){dialog, which ->  }.show()
    }

    fun buscar(id: String, btnEtiqueta: String){
        try{
                var transaccion = basedatos.readableDatabase
                var SQL = "SELECT * FROM COMPRA WHERE IDCOMPRA="+id
                var respuesta = transaccion.rawQuery(SQL, null)

                if(respuesta.moveToFirst() == true){
                    var cadena = "IDCOMPRA: "+respuesta.getString(0)+" FECHA: "+respuesta.getString(1)+"IDCLIENTE: "+respuesta.getString(2)+" TOTAL: "+respuesta.getString(3)

                    if(btnEtiqueta.startsWith("Eliminar")){
                        var cadena = "¿SEGURO QUE DESEA ELIMINAR LA COMPRA CON ID [ "+respuesta.getString(0)+" ] ?"
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
            var SQL="SELECT * FROM COMPRA"
            var  respuesta = transaccion.rawQuery(SQL,null)
            if (respuesta.moveToFirst()==true){
                do{
                    listaCom.add(respuesta.getString(0) + " - " + respuesta.getString(1)+" - " + respuesta.getString(2)+" - " + respuesta.getString(3))
                }while(respuesta.moveToNext())
            }
            respuesta.close()
            val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listaCom)
            listaComView?.setAdapter(adapter);
        }catch (err: SQLiteException){
            mensaje("ERROR","NO se pudo ejecutar la consulta")
        }
    }

    fun mensaje(titulo: String, mensaje: String){
        AlertDialog.Builder(this).setTitle(titulo).setMessage(mensaje).setPositiveButton("OK"){ dialog, which -> }.show()
    }

    fun  validarCampos():Boolean{
        if(editFecha?.text!!.isEmpty() || editTotal?.text!!.isEmpty()){
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
        editFecha?.setText("")
        editTotal?.setText("")
    }
}
