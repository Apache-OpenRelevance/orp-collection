package org.orp.collection.servers;

import java.io.File;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.orp.collection.commons.CollectionsResource;
import org.orp.collection.exceptions.CollectionNotFoundException;
import org.orp.collection.utils.CollectionUtils;
import org.orp.collection.utils.DBHandler;
import org.orp.collection.utils.DBHandlerImpl;
import org.orp.collection.utils.JsonUtils;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.ext.wadl.WadlServerResource;
import org.restlet.representation.Representation;

public class CollectionsServerResource extends WadlServerResource implements
		CollectionsResource {
	
	private static final Logger log = LogManager.getLogger(CollectionsServerResource.class.getName());

	private DBHandler handler;
	private static final String REPO = "collections/";

	public void doInit() {

		try {
			handler = DBHandlerImpl.newHandler(DriverManager
					.getConnection("jdbc:sqlite:db/collection.db"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Representation list() throws SQLException {
		log.info("list(): list all collections");

		Map<String, Object> result = new HashMap<String, Object>();
		Set<Map<String, Object>> rs = handler.selectAll("COLLECTION");
		
		if(rs == null || rs.isEmpty()) {
			log.info("no result is returned from COLLECTION");
		}
		
		JSONArray collections = new JSONArray();

		for (Map<String, Object> obj : rs) {

			if (obj.get("topics_size") == null)
				obj.put("topics_size", "N/A");
			if (obj.get("qrels_size") == null)
				obj.put("qrels_size", "N/A");
			if (obj.get("corpus") == null)
				obj.put("corpus", "N/A");

			collections.put(obj);

		}

		result.put("collections", collections);

		if (result.isEmpty())
			setStatus(Status.SUCCESS_NO_CONTENT);
		try {
			handler.clean();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new JsonRepresentation(result);
	}

	public Representation execute(JsonRepresentation entity) {
		log.info("execute()");

		Map<String, Object> values = new HashMap<String, Object>();
		
		try {
			
			String name = JsonUtils.getSimpleValue(entity, "name");
			values.put("name", name);
			String id = UUID.randomUUID().toString().replaceAll("-", "");
			values.put("id", id);
			values.put("uri", getRequest().getResourceRef().getIdentifier()
					+ "/" + id);
			values.put("create_time", CollectionUtils.dateFormat(new Date(
					System.currentTimeMillis())));

			new File(REPO + id + "/topics/").mkdirs();
			new File(REPO + id + "/qrels/").mkdirs();

			handler.insert("COLLECTION", values);
			handler.clean();

			setStatus(Status.SUCCESS_CREATED);
			
		} catch (JSONException je) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return CollectionUtils.message("Invalid key or missing value.");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		if(values == null || values.size() == 0) {
			log.info("no value has been put into the map");
		}
		return new JsonRepresentation(values);
	}

	public Representation delete(JsonRepresentation entity) {
		log.info("delete()");
		
		String id = null;

		try {

			id = JsonUtils.getSimpleValue(entity, "id");

			if (handler.selectAllById("Collection", id) == null)
				throw new CollectionNotFoundException();

			CollectionUtils.deleteFile(new File(REPO + id));
			handler.deleteById("Collection", id);
			handler.clean();

		} catch (JSONException je) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return CollectionUtils.message("Invalid key or missing value.");
		} catch (CollectionNotFoundException ce) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return CollectionUtils.message("Collection " + id + " not found.");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return CollectionUtils.message("Collection " + id
				+ " has been deleted.");
	}

}
