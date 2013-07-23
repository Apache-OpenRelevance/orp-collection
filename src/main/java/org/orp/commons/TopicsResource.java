package org.orp.commons;


import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

public interface TopicsResource {
	/**
	 * 
	 * @return a JSON file with topic file name, size and corpus, etc.
	 */
	@Get
	public Representation present();
	
	@Post("json:json")
	public Representation execute(JsonRepresentation entity);
}
