package org.orp.collection.application;

import java.sql.DriverManager;
import java.sql.SQLException;

import org.orp.collection.servers.CollectionServerResource;
import org.orp.collection.servers.CollectionsServerResource;
import org.orp.collection.servers.TopicsServerResource;
import org.orp.collection.utils.DBHandler;
import org.orp.collection.utils.DBHandlerImpl;
import org.restlet.Restlet;
import org.restlet.ext.wadl.WadlApplication;
import org.restlet.routing.Router;

public class CollectionApplication extends WadlApplication{
	public CollectionApplication(){
		DBHandler handler;
		
		try {
			handler = DBHandlerImpl.newHandler(
					DriverManager.getConnection("jdbc:sqlite:db/collection.db"));
			
			if(!handler.exist("collection"))
				handler.createTable("CREATE TABLE COLLECTION(" +
						"ID VARCHAR(20) PRIMARY KEY NOT NULL," +
						"NAME VARCHAR(20) NOT NULL," +
						"URI VARCHAR(50) NOT NULL," +
						"CREATE_TIME DATE NOT NULL," +
						"TOPICS_SIZE INT," +
						"QRELS_SIZE INT," +
						"CORPUS VARCHAR(20))");
			handler.clean();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
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
