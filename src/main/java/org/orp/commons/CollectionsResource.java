package org.orp.commons;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

public interface CollectionsResource {
	
	@Get
	public Representation list();
	
	@Post("json:json")
	public Representation execute(JsonRepresentation entity);
	
	@Delete("json:json")
	public Representation delete(JsonRepresentation entity);
}
