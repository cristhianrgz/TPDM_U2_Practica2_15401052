package mx.edu.tpdm_u2_practica3_15401052

import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main3.*

class Main3Activity : AppCompatActivity() {

    var editDescripcionEm : EditText?= null
    var editDomEmpresa : EditText?= null
    var insertarEm : Button?= null
    var buscarEm : Button?= null
    var actualizarEm : Button?= null
    var eliminarEm : Button?= null
    var listaEmView : ListView?= null
    //-------------------------------------
    var basedatos = BaseDatos(this, "practica2", null, 1)
    var listaCEm : ArrayList<String> = ArrayList()
    var cadena = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)
        editDescripcionEm = findViewById(R.id.editDescripEm)
        editDomEmpresa = findViewById(R.id.editDomEm)
        insertarEm = findViewById(R.id.btnInsertarEm)
        buscarEm = findViewById(R.id.btnBuscarEm)
        actualizarEm = findViewById(R.id.btnActualizarEm)
        eliminarEm = findViewById(R.id.btnEliminarEm)
        listaEmView = findViewById(R.id.empresasRegistradas)
        consultaGeneralEmpresa()

        insertarEm?.setOnClickListener {
            insertar()
        }

        buscarEm?.setOnClickListener {
            pedirID(buscarEm?.text.toString())
        }

        actualizarEm?.setOnClickListener {

        }

        eliminarEm?.setOnClickListener {
            pedirID(eliminarEm?.text.toString())
        }
    }

    fun insertar(){
        try{
            var transaccion = basedatos.writableDatabase
            var SQL = "INSERT INTO EMPRESA VALUES(null,'DESCRIPCION', 'DOMICILIO')"
            SQL = SQL.replace("DESCRIPCION", editDescripcionEm?.text.toString())
            SQL = SQL.replace("DOMICILIO", editDomEmpresa?.text.toString())

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
            var SQL = "UPDATE EMPRESA SET DESCRIPCION='campodescrip', DOMICILIO='campodom' WHERE IDEMPRESA='campoid'"
            if (validarCampos()==false){
                mensaje("ERROR","ALGUN CAMPO ESTA VACIO")
                return
            }
            SQL = SQL.replace("DESCRIPCION", editDescripcionEm?.text.toString())
            SQL = SQL.replace("DOMICILIO", editDomEmpresa?.text.toString())
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
            var SQL = "DELETE FROM EMPRESA WHERE IDEMPRESA="+id
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
                    Toast.makeText(this@Main3Activity, "ERROR CAMPO VACÍO", Toast.LENGTH_LONG).show()
                    return@setPositiveButton
                }
                buscar(campo.text.toString(),etiqueta)

            }.setNeutralButton("CANCELAR"){dialog, which ->  }.show()
    }

    fun buscar(id: String, btnEtiqueta: String){
        try{
            var transaccion = basedatos.readableDatabase
            var SQL = "SELECT * FROM EMPRESA WHERE IDEMPRESA="+id
            var respuesta = transaccion.rawQuery(SQL, null)

            if(respuesta.moveToFirst() == true){
                var cadena = "ID: "+respuesta.getString(0)+" Descripción: "+respuesta.getString(1)+" Domicilio: "+respuesta.getString(2)

                if(btnEtiqueta.startsWith("Eliminar")){
                    var cadena = "¿SEGURO QUE DESEA ELIMINAR [ "+respuesta.getString(1)+" ] CON ID [ "+respuesta.getString(0)+" ] ?"
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

    fun consultaGeneralEmpresa(){
        try {
            var transaccion = basedatos.writableDatabase
            var SQL="SELECT * FROM EMPRESA"
            var  respuesta = transaccion.rawQuery(SQL,null)
            if (respuesta.moveToFirst()==true){
                do{
                    listaCEm.add(respuesta.getString(0) + " - " + respuesta.getString(1)+" - " + respuesta.getString(2))
                }while(respuesta.moveToNext())
            }
            respuesta.close()
            val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listaCEm)
            listaEmView?.setAdapter(adapter);
        }catch (err: SQLiteException){
            mensaje("ERROR","NO se pudo ejecutar la consulta")
        }
    }

    fun mensaje(titulo: String, mensaje: String){
        AlertDialog.Builder(this).setTitle(titulo).setMessage(mensaje).setPositiveButton("OK"){ dialog, which -> }.show()
    }

    fun  validarCampos():Boolean{
        if(editDescripcionEm?.text!!.isEmpty() || editDomEmpresa?.text!!.isEmpty()){
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
        editDescripcionEm?.setText("")
        editDomEmpresa?.setText("")
    }
}
