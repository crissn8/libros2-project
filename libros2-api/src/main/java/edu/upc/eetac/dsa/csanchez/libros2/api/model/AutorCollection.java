package edu.upc.eetac.dsa.csanchez.libros2.api.model;

import java.util.ArrayList;
import java.util.List;

import edu.upc.eetac.dsa.csanchez.libros2.api.model.Autor;

public class AutorCollection {

	private List<Autor> authors;
	 


	public AutorCollection() {
	super();
	authors = new ArrayList<>();
	}
	 
	public List<Autor> getAuthors() {
		return authors;
	}



	public void setAuthors(List<Autor> authors) {
		this.authors = authors;
	}


	 
	public void addAuthor(Autor autor) {
	authors.add(autor);
	}
	
	
}
