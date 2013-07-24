package org.orp.collection.commons;


import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;

public interface CollectionResource {

	@Get
	public Representation present();
	
	@Post("json:json")
	public Representation execute(JsonRepresentation entity);
	
	@Put
	public Representation store(Representation entity);
}
