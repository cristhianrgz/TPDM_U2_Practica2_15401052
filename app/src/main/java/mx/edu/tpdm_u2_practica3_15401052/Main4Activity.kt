package mx.edu.tpdm_u2_practica3_15401052

import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AlertDialog

class Main4Activity : AppCompatActivity() {

    var editProducto : EditText ?= null
    var editCantidad : EditText ?= null
    var editPrecio : EditText ?= null
    var insertarAlm : Button?= null
    var buscarAlm : Button?= null
    var actualizarAlm : Button?= null
    var eliminarAlm : Button?= null
    var listaAlmView : ListView?= null
    //-------------------------------------
    var basedatos = BaseDatos(this, "practica2", null, 1)
    var listaAlm : ArrayList<String> = ArrayList()
    var cadena = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main4)

        editProducto = findViewById(R.id.editProducto)
        editCantidad = findViewById(R.id.editCantidad)
        editPrecio = findViewById(R.id.editPrecio)
        insertarAlm = findViewById(R.id.btnInsertarAlm)
        buscarAlm = findViewById(R.id.btnBuscarAlm)
        actualizarAlm = findViewById(R.id.btnActualizarAlm)
        eliminarAlm = findViewById(R.id.btnEliminarAlm)
        listaAlmView = findViewById(R.id.almacenesRegistradas)
        consultaGeneralAlmacenes()

        insertarAlm?.setOnClickListener {
            insertar()
        }

        buscarAlm?.setOnClickListener {
            pedirID(buscarAlm?.text.toString())
        }

        actualizarAlm?.setOnClickListener {

        }

        eliminarAlm?.setOnClickListener {
            pedirID(eliminarAlm?.text.toString())
        }
    }

    fun insertar(){
        try{
            var transaccion = basedatos.writableDatabase
            var SQL = "INSERT INTO ALMACEN VALUES(null,'PRODUCTO', 'CANTIDAD', 'PRECIO')"
            SQL = SQL.replace("PRODUCTO", editProducto?.text.toString())
            SQL = SQL.replace("CANTIDAD", editCantidad?.text.toString())
            SQL = SQL.replace("PRECIO", editPrecio?.text.toString())
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

    fun actualizar() {
        try{
            var transaccion = basedatos.writableDatabase
            var SQL = "UPDATE ALMACEN SET PRODUCTO='campoproducto', CANTIDAD='campocantidad', PRECIO='campoprecio' WHERE IDALMACEN='campoid'"
            if (validarCampos()==false){
                mensaje("ERROR","ALGUN CAMPO ESTA VACIO")
                return
            }
            SQL = SQL.replace("PRODUCTO", editProducto?.text.toString())
            SQL = SQL.replace("CANTIDAD", editCantidad?.text.toString())
            SQL = SQL.replace("PRECIO", editPrecio?.text.toString())
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
            var SQL = "DELETE FROM ALMACEN WHERE IDALMACEN="+id
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
                    Toast.makeText(this@Main4Activity, "ERROR CAMPO VACÍO", Toast.LENGTH_LONG).show()
                    return@setPositiveButton
                }
                buscar(campo.text.toString(),etiqueta)

            }.setNeutralButton("CANCELAR"){dialog, which ->  }.show()
    }

    fun buscar(id: String, btnEtiqueta: String){
        try{
            var transaccion = basedatos.readableDatabase
            var SQL = "SELECT * FROM ALMACEN WHERE IDALMACEN="+id
            var respuesta = transaccion.rawQuery(SQL, null)

            if(respuesta.moveToFirst() == true){
                var cadena = "IDALMACEN: "+respuesta.getString(0)+" PRODUCTO: "+respuesta.getString(1)+"CANTIDAD: "+respuesta.getString(2)+" PRECIO: "+respuesta.getString(3)

                if(btnEtiqueta.startsWith("Eliminar")){
                    var cadena = "¿SEGURO QUE DESEA ELIMINAR EL ALMACEN CON ID [ "+respuesta.getString(0)+" ] ?"
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

    fun consultaGeneralAlmacenes(){
        try {
            var transaccion = basedatos.writableDatabase
            var SQL="SELECT * FROM ALMACEN"
            var  respuesta = transaccion.rawQuery(SQL,null)
            if (respuesta.moveToFirst()==true){
                do{
                    listaAlm.add(respuesta.getString(0) + " - " + respuesta.getString(1)+" - " + respuesta.getString(2)+" - " + respuesta.getString(3))
                }while(respuesta.moveToNext())
            }
            respuesta.close()
            val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listaAlm)
            listaAlmView?.setAdapter(adapter);
        }catch (err: SQLiteException){
            mensaje("ERROR","NO se pudo ejecutar la consulta")
        }
    }

    fun mensaje(titulo: String, mensaje: String){
        AlertDialog.Builder(this).setTitle(titulo).setMessage(mensaje).setPositiveButton("OK"){ dialog, which -> }.show()
    }

    fun  validarCampos():Boolean{
        if(editProducto?.text!!.isEmpty() || editCantidad?.text!!.isEmpty() || editPrecio?.text!!.isEmpty()){
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
        editProducto?.setText("")
        editPrecio?.setText("")
        editCantidad?.setText("")
    }
}
