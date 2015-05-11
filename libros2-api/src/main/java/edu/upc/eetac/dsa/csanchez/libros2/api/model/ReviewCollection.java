package edu.upc.eetac.dsa.csanchez.libros2.api.model;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Link;

import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import org.glassfish.jersey.linking.InjectLink.Style;

import edu.upc.eetac.dsa.csanchez.libros2.api.MediaType;
import edu.upc.eetac.dsa.csanchez.libros2.api.model.Review;

public class ReviewCollection {
	
	private List<Review> reviews;
	
	@InjectLinks({
		@InjectLink(value = "/books/reviews", style = Style.ABSOLUTE, rel = "focus", title = "Reviews Libro", type = MediaType.REVIEWS_API_REVIEW_COLLECTION) })
	private List<Link> links;
	
	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	public ReviewCollection(){
		
		super();
		reviews = new ArrayList<>();
	}
	
	public List<Review> getReviews() {
		return reviews;
	}

	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}
	
	
	public void addReview(Review review) {
	reviews.add(review);
	}

}
