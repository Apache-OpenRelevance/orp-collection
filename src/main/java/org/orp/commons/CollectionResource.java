package org.orp.commons;


import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Put;

public interface CollectionResource {

	@Get
	public Representation present();
	
	@Put
	public Representation store(Representation entity);
}
