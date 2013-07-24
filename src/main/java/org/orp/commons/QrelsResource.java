package org.orp.commons;

import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

public interface QrelsResource {

	@Get
	public Representation download();
	
	@Post("json:json")
	public Representation execute();
}
