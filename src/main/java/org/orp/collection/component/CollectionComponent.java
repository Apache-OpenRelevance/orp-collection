package org.orp.collection.component;

import org.orp.collection.application.CollectionApplication;
import org.restlet.Component;
import org.restlet.data.Protocol;

public class CollectionComponent {
	public static void main(String[] args) {
		try{
			Component component = new Component();
			component.getServers().add(Protocol.HTTP, 8111);
			component.getDefaultHost().attach(new CollectionApplication());
			component.start();
		}catch(Exception e){
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}

}
