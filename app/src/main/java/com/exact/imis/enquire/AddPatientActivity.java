package com.exact.imis.enquire;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddPatientActivity extends Activity {

    Button btnSave;
    EditText name,code;

    SQLiteDatabase db;

    final String Path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/IMIS/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);

        btnSave = (Button) findViewById(R.id.btnSave);
        name = (EditText) findViewById(R.id.patientName);
        code = (EditText) findViewById(R.id.CHFID);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name.getText().toString().equals("")){
                    Toast.makeText(getBaseContext(), "Enter the patient name", Toast.LENGTH_SHORT).show();
                }else if(code.getText().toString().equals("")){
                    Toast.makeText(getBaseContext(), "Enter the cheque number", Toast.LENGTH_SHORT).show();
                }else{
                    createOrOpenDatabases();
                    if(getInsureeNumbersStatut(code.getText().toString()).equals("Disponible")){
                        //add patient and update the statut
                        updateStatutInsureeNumber(code.getText().toString());
                    }else if(getInsureeNumbersStatut(code.getText().toString()).equals("")){
                        insertInsureeNumber(code.getText().toString(),"En cours");
                    }else if(getInsureeNumbersStatut(code.getText().toString()).equals("En cours")){
                        Toast.makeText(getBaseContext(), "this cheque number already used ", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });



    }

    public void insertInsureeNumber(String Numero, String Statut) {
        try {
            ContentValues cv = new ContentValues();
            cv.put("Number", Numero);
            cv.put("Statut", Statut);
            db.insert("tblInsureeNumbers", null, cv);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getInsureeNumbersStatut(String number) {
        String statut = "";
        try {
            String table = "tblInsureeNumbers";
            String[] columns = {"Statut"};
            String selection = "Number=?";
            String[] selectionArgs = {number};
            String limit = "1";
            Cursor c = db.query(table, columns, selection, selectionArgs, null, null, null, limit);
            if (c.getCount() == 1) {
                c.moveToFirst();
                statut = c.getString(c.getColumnIndexOrThrow("Statut"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statut;
    }

    //modifie le statut d'un numéro de cheque
    public void updateStatutInsureeNumber(String Code){
        try {
            ContentValues cv = new ContentValues();
            cv.put("Statut", "En cours");
            db.update("tblInsureeNumbers", cv,"Number=?", new String[]{Code});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void validateForm(String name,String code){
        if(name.equals("")){
            Toast.makeText(this, "Enter Patient name", Toast.LENGTH_SHORT).show();
        }else if(code.equals("")){
            Toast.makeText(this, "Enter the cheque number", Toast.LENGTH_SHORT).show();
        }else if(getInsureeNumbersStatut(code).equals("En cours")){
            //insert patient and update the cheque number
            updateStatutInsureeNumber(code);
            showDial("This cheque number was already used");
        }else{
            if(getInsureeNumbersStatut(code).equals("")){
                //insert patient and statut to database
                insertInsureeNumber(code,"En cours");
                showDial("Patient save successful");
            }else if(getInsureeNumbersStatut(code).equals("Disponible")){
                //insert patient and update the cheque number
                updateStatutInsureeNumber(code);
                showDial("Patient save successful");
            }
        }
    }

    public AlertDialog showDial(String msg){
        return new AlertDialog.Builder(this)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Ok", new android.content.DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    public void createOrOpenDatabases() {
        if (!checkDatabase()) {
            db = SQLiteDatabase.openOrCreateDatabase(Path +"ImisData.db3", null);
            createTables();
        }
    }

    public void createTables() {
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS tblInsureeNumbers(Number text, Statut text);");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean checkDatabase() {
        return db != null && db.isOpen();
    }
}