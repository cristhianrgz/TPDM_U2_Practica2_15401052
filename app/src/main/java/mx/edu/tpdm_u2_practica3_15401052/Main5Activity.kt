package mx.edu.tpdm_u2_practica3_15401052

import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AlertDialog
import java.sql.SQLException

class Main5Activity : AppCompatActivity() {

    var editCantidad : EditText?= null
    var editPrecio : EditText?= null
    var insertarDet : Button?= null
    var buscarDet : Button?= null
    var actualizarDet : Button?= null
    var eliminarDet : Button?= null
    var listaDetView : ListView?= null
    var editIdAlm : EditText ?= null
    //-------------------------------------
    var basedatos = BaseDatos(this, "practica2", null, 1)
    var listaDet : ArrayList<String> = ArrayList()
    var cadena = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main5)

        editCantidad = findViewById(R.id.editCantidadDet)
        editPrecio = findViewById(R.id.editPrecioDet)
        insertarDet = findViewById(R.id.btnInsertarDet)
        buscarDet = findViewById(R.id.btnBuscarDet)
        actualizarDet = findViewById(R.id.btnActualizarDet)
        eliminarDet = findViewById(R.id.btnEliminarDet)
        listaDetView = findViewById(R.id.detComprasRegistradas)
        editIdAlm = findViewById(R.id.editIDALMACEN)
        consultaGeneralDetCompra()

        insertarDet?.setOnClickListener {
            pedirIDInsert(insertarDet?.text.toString())
        }

        buscarDet?.setOnClickListener {
            pedirID(buscarDet?.text.toString())
        }

        actualizarDet?.setOnClickListener {

        }

        eliminarDet?.setOnClickListener {
            pedirID(eliminarDet?.text.toString())
        }
    }

    fun insertar(){
        try{
            var transaccion = basedatos.writableDatabase
            var SQL = "INSERT INTO DETALLECOMPRA VALUES(null,'IDALMACEN', 'CANTIDAD', 'PRECIO', 'IDCOMPA')"
            SQL = SQL.replace("IDALMACEN", editIdAlm?.text.toString())
            SQL = SQL.replace("CANTIDAD", editCantidad?.text.toString())
            SQL = SQL.replace("PRECIO", editPrecio?.text.toString())
            SQL = SQL.replace("IDCOMPRA", cadena)
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

        AlertDialog.Builder(this).setTitle("ATENCION").setMessage("ESCRIBA EL ID COMPRA QUE SE VA A ${etiqueta} AL DETALLECOMPRA : ").setView(campo)
            .setPositiveButton("OK"){dialog,which->
                if(validarCampo(campo) == false){
                    Toast.makeText(this@Main5Activity, "ERROR CAMPO VACÍO", Toast.LENGTH_LONG).show()
                    return@setPositiveButton
                }
                buscarIDInsert(campo.text.toString(),etiqueta)


            }.setNeutralButton("CANCELAR"){dialog, which ->  }.show()
    }

    fun buscarIDInsert(id: String, btnEtiqueta: String){
        try {
            var transaccion = basedatos.readableDatabase
            var SQL="SELECT * FROM  COMPRA WHERE IDCOMPRA="+id
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
            var SQL = "UPDATE DETALLECOMPRA SET IDALMACEN='campoidAlm', CANTIDAD='campocant', PRECIO='campoprec', IDCOMPRA='campoidCompra' WHERE IDETALLECOMPRA='campoid'"
            if (validarCampos()==false){
                mensaje("ERROR","ALGUN CAMPO ESTA VACIO")
                return
            }
            SQL = SQL.replace("IDALMACEN", editIdAlm?.text.toString())
            SQL = SQL.replace("CANTIDAD", editCantidad?.text.toString())
            SQL = SQL.replace("PRECIO", editPrecio?.text.toString())
            SQL = SQL.replace("IDCOMPRA", cadena)
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
            var SQL = "DELETE FROM DETALLECOMPRA WHERE IDDETALLECOMPRA="+id
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
                    Toast.makeText(this@Main5Activity, "ERROR CAMPO VACÍO", Toast.LENGTH_LONG).show()
                    return@setPositiveButton
                }
                buscar(campo.text.toString(),etiqueta)

            }.setNeutralButton("CANCELAR"){dialog, which ->  }.show()
    }

    fun buscar(id: String, btnEtiqueta: String){
        try{
            var transaccion = basedatos.readableDatabase
            var SQL = "SELECT * FROM DETALLECOMPRA WHERE IDDETALLECOMPRA="+id
            var respuesta = transaccion.rawQuery(SQL, null)

            if(respuesta.moveToFirst() == true){
                var cadena = "IDDETALLECOMPRA: "+respuesta.getString(0)+" IDALMACEN: "+respuesta.getString(1)+" CANTIDAD: "+respuesta.getString(2)+" PRECIO: "+respuesta.getString(3)+" IDCOMPRA: "+respuesta.getString(4)

                if(btnEtiqueta.startsWith("Eliminar")){
                    var cadena = "¿SEGURO QUE DESEA ELIMINAR EL DETALLE DE COMPRA CON ID [ "+respuesta.getString(0)+" ] ?"
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

    fun consultaGeneralDetCompra(){
        try {
            var transaccion = basedatos.writableDatabase
            var SQL="SELECT * FROM DETALLECOMPRA"
            var  respuesta = transaccion.rawQuery(SQL,null)
            if (respuesta.moveToFirst()==true){
                do{
                    listaDet.add(respuesta.getString(0) + " - " + respuesta.getString(1)+" - " + respuesta.getString(2)+" - " + respuesta.getString(3)+" - " + respuesta.getString(4))
                }while(respuesta.moveToNext())
            }
            respuesta.close()
            val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listaDet)
            listaDetView?.setAdapter(adapter);
        }catch (err: SQLiteException){
            mensaje("ERROR","NO se pudo ejecutar la consulta")
        }
    }
    fun mensaje(titulo: String, mensaje: String){
        AlertDialog.Builder(this).setTitle(titulo).setMessage(mensaje).setPositiveButton("OK"){dialog, which -> }.show()
    }

    fun  validarCampos():Boolean{
        if(editCantidad?.text!!.isEmpty() || editPrecio?.text!!.isEmpty() || editIdAlm?.text!!.isEmpty()){
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
        editCantidad?.setText("")
        editPrecio?.setText("")
        editIdAlm?.setText("")
    }
}
