package edu.upc.eetac.dsa.csanchez.libros.api;


import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class LibrosAPI {

    private final static String TAG = LibrosAPI.class.getName();
    private static LibrosAPI instance = null;
    private URL url;


    private LibrosRootAPI rootAPI = null;

    private LibrosAPI(Context context) throws IOException, AppException {
        super();

        AssetManager assetManager = context.getAssets();
        Properties config = new Properties();
        config.load(assetManager.open("config.properties"));
        String urlHome = config.getProperty("books.home");
        url = new URL(urlHome);

        Log.d("LINKS", url.toString());
        getRootAPI();
    }

    public final static LibrosAPI getInstance(Context context) throws AppException {
        if (instance == null)
            try {
                instance = new LibrosAPI(context);
            } catch (IOException e) {
                throw new AppException(
                        "Can't load configuration file");
            }
        return instance;
    }

    private void getRootAPI() throws AppException {
        Log.d(TAG, "getRootAPI()");
        rootAPI = new LibrosRootAPI();
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.connect();
        } catch (IOException e) {
            throw new AppException(
                    "Can't connect to Libros API Web Service");
        }

        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JSONObject jsonObject = new JSONObject(sb.toString());
            JSONArray jsonLinks = jsonObject.getJSONArray("links");
            parseLinks(jsonLinks, rootAPI.getLinks());
        } catch (IOException e) {
            throw new AppException(
                    "Can't get response from Libros API Web Service");
        } catch (JSONException e) {
            throw new AppException("Error parsing Libros Root API");
        }


    }

    public LibroCollection getLibros() throws AppException {
        Log.d(TAG, "getStings()");
        LibroCollection libros = new LibroCollection();

        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) new URL(rootAPI.getLinks()
                    .get("books").getTarget()).openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.connect();
        } catch (IOException e) {
            throw new AppException(
                    "Can't connect to Libros API Web Service");
        }

        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }


            JSONObject jsonObject = new JSONObject(sb.toString());
            JSONArray jsonLinks = jsonObject.getJSONArray("links");
            parseLinks(jsonLinks, libros.getLinks());


            JSONArray jsonStings = jsonObject.getJSONArray("books");
            for (int i = 0; i < jsonStings.length(); i++) {
                Libro libro = new Libro();
                JSONObject jsonSting = jsonStings.getJSONObject(i);
                libro.setTitulo(jsonSting.getString("titulo"));
                libro.setId(jsonSting.getInt("id"));
                libro.setEdicion(jsonSting.getString("edicion"));
                libro.setLengua(jsonSting.getString("lengua"));
                libro.setFecha_edicion(jsonSting.getLong("fecha_edicion"));
                jsonLinks = jsonSting.getJSONArray("links");
                parseLinks(jsonLinks, libro.getLinks());
                libros.getLibros().add(libro);

            }
        } catch (IOException e) {
            throw new AppException(
                    "Can't get response from Libros API Web Service");
        } catch (JSONException e) {
            throw new AppException("Error parsing Libros Root API");
        }

        return libros;
    }

    private Map<String, Libro> librosCache = new HashMap<String, Libro>();


    public Libro getLibro(String urlSting) throws AppException {
        Libro libro = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(urlSting);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);

            libro = librosCache.get(urlSting);
            String eTag = (libro == null) ? null : libro.getETag();
            if (eTag != null)
                urlConnection.setRequestProperty("If-None-Match", eTag);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_NOT_MODIFIED) {
                Log.d(TAG, "CACHE");
                return librosCache.get(urlSting);
            }
            Log.d(TAG, "NOT IN CACHE");
            libro = new Libro();
            eTag = urlConnection.getHeaderField("ETag");
            libro.setETag(eTag);
            librosCache.put(urlSting, libro);

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            JSONObject jsonSting = new JSONObject(sb.toString());
            libro.setTitulo(jsonSting.getString("titulo"));
            libro.setId(jsonSting.getInt("id"));
           // libro.setLastModified(jsonSting.getLong("lastModified"));
            libro.setFecha_edicion(jsonSting.getLong("fecha_edicion"));
            libro.setFecha_impresion(jsonSting.getLong("fecha_impresion"));
            libro.setEditorial(jsonSting.getString("editorial"));
            libro.setEdicion(jsonSting.getString("content"));
            libro.setLengua(jsonSting.getString("lengua"));
            JSONArray jsonLinks = jsonSting.getJSONArray("links");
            parseLinks(jsonLinks, libro.getLinks());
        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new AppException("Bad sting url");
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new AppException("Exception when getting the sting");
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new AppException("Exception parsing response");
        }

        return libro;
    }

    private void parseLinks(JSONArray jsonLinks, Map<String, Link> map)
            throws AppException, JSONException {
        for (int i = 0; i < jsonLinks.length(); i++) {
            Link link = null;
            try {
                link = SimpleLinkHeaderParser
                        .parseLink(jsonLinks.getString(i));
            } catch (Exception e) {
                throw new AppException(e.getMessage());
            }
            String rel = link.getParameters().get("rel");
            String rels[] = rel.split("\\s");
            for (String s : rels)
                map.put(s, link);
        }
    }

}
