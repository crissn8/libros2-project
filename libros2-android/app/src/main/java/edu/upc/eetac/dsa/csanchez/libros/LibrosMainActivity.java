package edu.upc.eetac.dsa.csanchez.libros;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.ArrayList;

import edu.upc.eetac.dsa.csanchez.libros.api.AppException;
import edu.upc.eetac.dsa.csanchez.libros.api.Libro;
import edu.upc.eetac.dsa.csanchez.libros.api.LibroCollection;
import edu.upc.eetac.dsa.csanchez.libros.api.LibrosAPI;


public class LibrosMainActivity extends ListActivity {


    private final static String TAG = LibrosMainActivity.class.toString();
    private static final String[] items = { "lorem", "ipsum", "dolor", "sit",
            "amet", "consectetuer", "adipiscing", "elit", "morbi", "vel",
            "ligula", "vitae", "arcu", "aliquet", "mollis", "etiam", "vel",
            "erat", "placerat", "ante", "porttitor", "sodales", "pellentesque",
            "augue", "purus" };

    private ArrayList<Libro> librosList;
    private LibroAdapter adapter;


    private void addLibros(LibroCollection libros){
        librosList.addAll(libros.getLibros());
        adapter.notifyDataSetChanged();
    }

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_libros_main);
        System.out.println("va");
        librosList = new ArrayList<Libro>();
        adapter = new LibroAdapter(this, librosList);
        setListAdapter(adapter);
        (new FetchStingsTask()).execute();
    }


    private class FetchStingsTask extends
            AsyncTask<Void, Void, LibroCollection> {
        private ProgressDialog pd;

        @Override
        protected LibroCollection doInBackground(Void... params) {
            LibroCollection libros = null;
            try {
                libros = LibrosAPI.getInstance(LibrosMainActivity.this)
                        .getLibros();
            } catch (AppException e) {
                e.printStackTrace();
            }
            return libros;
        }

        @Override
        protected void onPostExecute(LibroCollection result) {
            addLibros(result);
            if (pd != null) {
                pd.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(LibrosMainActivity.this);
            pd.setTitle("Searching...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_libros_main, menu);
        return true;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Libro libro = librosList.get(position);
        Log.d(TAG, libro.getLinks().get("self").getTarget());

        Intent intent = new Intent(this, LibrosDetailActivity.class);
        intent.putExtra("url", libro.getLinks().get("self").getTarget());
        startActivity(intent);
    }

}
