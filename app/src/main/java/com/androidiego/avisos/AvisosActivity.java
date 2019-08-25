package com.androidiego.avisos;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class AvisosActivity extends AppCompatActivity {
    private ListView mListView;
    private AvisosDBAdapter mDbAdapter;
    private AvisosSimpleCursorAdapter mCursorAdapter;
    private int numColumn;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
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
                Snackbar.make(view, "Nuevo  Aviso añadido", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                fireCustomDialog(null);
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
                        // editar aviso
                        if (position == 0) {
                            Toast.makeText(AvisosActivity.this, "editar " + masterListPosition, Toast.LENGTH_SHORT).show();
                            int nId = getIdFromPosition(masterListPosition);
                            Aviso aviso = mDbAdapter.fetchReminderById(nId);
                            fireCustomDialog(aviso);
                            // borrar avisos
                        } else if(position == 1) {
                            Toast.makeText(AvisosActivity.this, "borrar " + masterListPosition, Toast.LENGTH_SHORT).show();
                            mDbAdapter.deleteReminderById(getIdFromPosition(masterListPosition));
                            mCursorAdapter.changeCursor(mDbAdapter.fetchAllReminders());
                        } else {
                            // finish();
                        }
                        dialog.dismiss();
                    }
                });
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) { // Es innecesario en nuestro caso, ya que tenemos api mínima de 14 y HONEYCOMB es de 11 :)
            mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL); // define el comportamiento de elección para la lista.
            mListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    MenuInflater inflater = mode.getMenuInflater();
                    inflater.inflate(R.menu.cam_menu, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.menu_item_delete_aviso:
                            for (int nC = mCursorAdapter.getCount() -1; nC >= 0; nC--) {
                                if (mListView.isItemChecked(nC)) {
                                    mDbAdapter.deleteReminderById(getIdFromPosition(nC));
                                }
                            }
                            mode.finish();
                            mCursorAdapter.changeCursor(mDbAdapter.fetchAllReminders());
                            return true;
                    }

                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {

                }
            });
        }

    }//end onCreate()

    private int getIdFromPosition(int nC) {
        return (int)mCursorAdapter.getItemId(nC);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_avisos, menu);
        return true;
    }

    /**
     * Metodo usado tanto para editar como para crear|insertar Avisos
     * @param aviso
     */
    private void fireCustomDialog(final Aviso aviso) {
        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//Crea una caja de diálogo sin título, ya que lo tenemos en el nuestro propio
        dialog.setContentView(R.layout.dialog_custom);//inyectamos diseño personalizado que hemos creado

        TextView titleView = (TextView)dialog.findViewById(R.id.custom_title);
        final EditText editCustom = (EditText)dialog.findViewById(R.id.custom_edit_reminder);
        Button commitButton = (Button)dialog.findViewById(R.id.custom_button_commit);
        final CheckBox checkBox = (CheckBox)dialog.findViewById(R.id.custom_check_box);
        LinearLayout rootLayout = (LinearLayout)dialog.findViewById(R.id.custom_root_layout);
        final boolean isEditOperation = (aviso != null);

        // esto es para un edit
        if (isEditOperation) {
            titleView.setText(getString(R.string.edit_aviso));
            checkBox.setChecked(aviso.getId() == 1);
            editCustom.setText(aviso.getContent());
            rootLayout.setBackgroundColor(getResources().getColor(R.color.azul_neutro));
        }

        commitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reminderText = editCustom.getText().toString();
                // para actualizar | editar aviso
                if (isEditOperation) {
                    Aviso reminderEdited = new Aviso(aviso.getId(),
                            reminderText, checkBox.isChecked() ? 1 : 0);
                    mDbAdapter.updateReminder(reminderEdited);
                 // esto es para un nuevo aviso
                } else {
                    mDbAdapter.createReminder(reminderText, checkBox.isChecked());
                }
                mCursorAdapter.changeCursor(mDbAdapter.fetchAllReminders());
                dialog.dismiss();
            }
        });

        Button buttonCancel = (Button)dialog.findViewById(R.id.custom_button_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
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
                    //Log.d(getLocalClassName(), "crear nuevo Aviso");
                fireCustomDialog(null);
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
