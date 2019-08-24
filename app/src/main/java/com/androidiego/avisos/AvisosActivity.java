package com.androidiego.avisos;

import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class AvisosActivity extends AppCompatActivity {
    private ListView mListView;
    private AvisosDBAdapter mDbAdapter;
    private AvisosSimpleCursorAdapter mCursorAdapter;
    private int numColumn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avisos);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mListView = (ListView) findViewById(R.id.avisos_list_view);
        findViewById(R.id.avisos_list_view);
        mListView.setDivider(null);
        mDbAdapter = new AvisosDBAdapter(this);
        mDbAdapter.open();

        if (savedInstanceState == null) {
            // limpiar todos los datos
            mDbAdapter.deleteAllReminders();
            // Add algunos datos
            mDbAdapter.createReminder("Visitar el Centro de Recogida", true);
            mDbAdapter.createReminder("Enviar los regalos prometidos", false);
            mDbAdapter.createReminder("Hacer la compra semanal", false);
            mDbAdapter.createReminder("Comprobar el correo", false);
        }

        Cursor cursor = mDbAdapter.fetchAllReminders();
        numColumn = cursor.getColumnCount();

        //desde las columnas definidas en la base de datos
        String[] from = new String[]{
                AvisosDBAdapter.COL_CONTENT
        };

        //a la id de views en el layout
        int[] to = new int[]{
                R.id.row_text
        };

        mCursorAdapter = new AvisosSimpleCursorAdapter(
                //context
                AvisosActivity.this,
                //el layout de la fila
                R.layout.avisos_row,
                //cursor
                cursor,
                //desde columnas definidas en la base de datos
                from,
                //a las ids de views en el layout
                to,
                //flag - no usado
                0);

        //el cursorAdapter (controller) está ahora actualizando la listView (view)
        //con datos desde la base de datos (modelo)
        mListView.setAdapter(mCursorAdapter);

        Toast.makeText(this, "Numero de Avisos: " + (numColumn+1), Toast.LENGTH_LONG).show();


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // cuando pulsamos en un item individual en la listview
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int masterListPosition, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AvisosActivity.this);
                ListView modeListView = new ListView(AvisosActivity.this);
                String modes[] = new String[]{ "Editar Aviso", "Borrar Aviso", "Finalizar" };
                ArrayAdapter<String> modeAdapter = new ArrayAdapter<>(AvisosActivity.this,
                        android.R.layout.simple_list_item_1, android.R.id.text1, modes);
                modeListView.setAdapter(modeAdapter);
                builder.setView(modeListView);
                final Dialog dialog = builder.create();
                dialog.show();
                // editar aviso
                modeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 0) {
                            Toast.makeText(AvisosActivity.this, "editar " + masterListPosition, Toast.LENGTH_SHORT).show();
                            // borrar avisos
                        } else if(position == 1) {
                            Toast.makeText(AvisosActivity.this, "borrar " + masterListPosition, Toast.LENGTH_SHORT).show();
                        } else {
                            // finish();
                        }
                        dialog.dismiss();
                    }
                });
            }
        });
    }//end onCreate()

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_avisos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_nuevo:
                // crear nuevo aviso
                Log.d(getLocalClassName(), "crear nuevo Aviso");
                return true;
            case R.id.action_salir:
                finish();
                return true;
            default:
                return false;
        }

        //return super.onOptionsItemSelected(item);
    }
}
