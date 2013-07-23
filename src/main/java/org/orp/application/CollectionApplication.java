package org.orp.application;

import org.orp.servers.CollectionServerResource;
import org.orp.servers.CollectionsServerResource;
import org.orp.servers.TopicsServerResource;
import org.orp.utils.DBHandlerImpl;
import org.restlet.Restlet;
import org.restlet.ext.wadl.WadlApplication;
import org.restlet.routing.Router;

public class CollectionApplication extends WadlApplication{
	public CollectionApplication(){
		DBHandlerImpl handler = DBHandlerImpl.newHandler(
				"jdbc:sqlite:db/collection.db");
		if(!handler.exist("collection"))
			handler.createTable("CREATE TABLE COLLECTION(" +
					"ID VARCHAR(20) PRIMARY KEY NOT NULL," +
					"NAME VARCHAR(20) NOT NULL," +
					"URI VARCHAR(50) NOT NULL," +
					"CREATE_TIME DATE NOT NULL," +
					"TOPICS_SIZE INT," +
					"QRELS_SIZE INT)");
		handler.clean();
	}
	
	@Override
	public Restlet createInboundRoot(){
		Router router = new Router();
		router.attach("/collections", CollectionsServerResource.class);
		router.attach("/collections/{colId}", CollectionServerResource.class);
		router.attach("/collections/{colId}/topics", TopicsServerResource.class);
		return router;
	}
}
