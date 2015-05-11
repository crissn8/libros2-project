package edu.upc.eetac.dsa.csanchez.libros;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import edu.upc.eetac.dsa.csanchez.libros.api.AppException;
import edu.upc.eetac.dsa.csanchez.libros.api.Libro;
import edu.upc.eetac.dsa.csanchez.libros.api.LibrosAPI;

/**
 * Created by Cristina on 11/05/2015.
 */
public class LibrosDetailActivity extends Activity{

    private final static String TAG = LibrosDetailActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.libros_detail_layout);
        String urlLibro = (String) getIntent().getExtras().get("url");
        (new FetchStingTask()).execute(urlLibro);
    }

    private void loadLibro(Libro libro) {
        TextView tvDetailTitulo = (TextView) findViewById(R.id.tvDetailTitulo);
        TextView tvDetailLengua = (TextView) findViewById(R.id.tvDetailLengua);
        TextView tvDetailEdicion = (TextView) findViewById(R.id.tvDetailEdicion);
        TextView tvDetailFecha_edicion = (TextView) findViewById(R.id.tvDetailFecha_edicion);
        TextView tvDetailFecha_impresion = (TextView) findViewById(R.id.tvDetailFecha_impresion);

        tvDetailTitulo.setText(libro.getTitulo());
        tvDetailLengua.setText(libro.getLengua());
        tvDetailEdicion.setText(libro.getEdicion());
        tvDetailFecha_edicion.setText(SimpleDateFormat.getInstance().format(
                libro.getFecha_edicion()));
        tvDetailFecha_impresion.setText(SimpleDateFormat.getInstance().format(
                libro.getFecha_impresion()));
    }

    private class FetchStingTask extends AsyncTask<String, Void, Libro> {
        private ProgressDialog pd;

        @Override
        protected Libro doInBackground(String... params) {
            Libro libro = null;
            try {
                libro = LibrosAPI.getInstance(LibrosDetailActivity.this)
                        .getLibro(params[0]);
            } catch (AppException e) {
                Log.d(TAG, e.getMessage(), e);
            }
            return libro;
        }

        @Override
        protected void onPostExecute(Libro result) {
            loadLibro(result);
            if (pd != null) {
                pd.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(LibrosDetailActivity.this);
            pd.setTitle("Loading...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

    }


}
