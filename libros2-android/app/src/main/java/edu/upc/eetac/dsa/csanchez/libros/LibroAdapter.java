package edu.upc.eetac.dsa.csanchez.libros;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import edu.upc.eetac.dsa.csanchez.libros.api.Libro;

public class LibroAdapter extends BaseAdapter {

    private final ArrayList<Libro> data;
    private LayoutInflater inflater;

    public LibroAdapter(Context context, ArrayList<Libro> data) {
        super();
        inflater = LayoutInflater.from(context);
        this.data = data;
    }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return ((Libro) getItem(position)).getId();
    }
    private static class ViewHolder {
        TextView tvTitulo;
        TextView tvEdicion;
        TextView tvLengua;
        TextView tvEditorial;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_row_libros, null);
            viewHolder = new ViewHolder();
            viewHolder.tvTitulo = (TextView) convertView
                    .findViewById(R.id.tvTitulo);
            viewHolder.tvEdicion = (TextView) convertView
                    .findViewById(R.id.tvEdicion);
            viewHolder.tvLengua = (TextView) convertView
                    .findViewById(R.id.tvLengua);
            viewHolder.tvEditorial = (TextView) convertView
                    .findViewById(R.id.tvEditorial);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String titulo = data.get(position).getTitulo();
        String edicion = data.get(position).getEdicion();
        String lengua = data.get(position).getLengua();
        String editorial= data.get(position).getEditorial();
        System.out.println("lengua"+lengua);
        viewHolder.tvTitulo.setText(titulo);
        viewHolder.tvEdicion.setText(edicion);
        viewHolder.tvLengua.setText(lengua);
        viewHolder.tvEditorial.setText(editorial);
        return convertView;
    }


}
