package org.orp.servers;

import java.io.File;
import java.sql.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.orp.commons.CollectionsResource;
import org.orp.utils.DBHandlerImpl;
import org.orp.utils.CollectionUtils;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.ext.wadl.WadlServerResource;
import org.restlet.representation.Representation;

public class CollectionsServerResource extends WadlServerResource implements CollectionsResource{
	
	private DBHandlerImpl handler;
	private static final String REPO = "collections/";
	
	public void doInit(){
			handler = DBHandlerImpl.newHandler("jdbc:sqlite:db/collection.db");
	}
	
	public Representation list(){
		Map<String, Object> result = new HashMap<String, Object>();
		Set<Map<String, Object>> rs = handler.selectAll("COLLECTION");
		
		Iterator<Map<String, Object>> iter = rs.iterator();
		int i = 1;
		while(iter.hasNext()){
			Map<String, Object> obj = new HashMap<String, Object>();
			obj.putAll(iter.next());
			if(obj.get("topics_size") == null) obj.put("topics_size", "N/A");
			if(obj.get("qrels_size") == null) obj.put("qrels_size", "N/A");
			result.put("c" + (i++), obj);
		}
			
		if(result.isEmpty())
			setStatus(Status.SUCCESS_NO_CONTENT);
		handler.clean();
		return new JsonRepresentation(result);
	}

	public Representation create(JsonRepresentation entity) {
			Map<String, Object> values = new HashMap<String, Object>();
			values.put("name", CollectionUtils.getJsonValue(entity, "name"));
			String id = UUID.randomUUID().toString().replaceAll("-", "");
			values.put("id", id);
			values.put("uri", getRequest().getResourceRef().getIdentifier() + "/" + id);
			values.put("create_time", CollectionUtils.dateFormat(new Date(System.currentTimeMillis())));
			
			new File(REPO + id + "/topics/").mkdirs();
			new File(REPO + id + "/qrels/").mkdirs();
			
			handler.insert("COLLECTION", values);
			handler.clean();
		
			
			setStatus(Status.SUCCESS_CREATED);
			return new JsonRepresentation(values);
	}
	
	public Representation delete(JsonRepresentation entity){
			String id = null;
			id = CollectionUtils.getJsonValue(entity, "id"); 
			if(handler.selectAllById("Collection", id).isEmpty())
				return CollectionUtils.message("Collection " + id + " not found.");
			CollectionUtils.deleteFile(new File(REPO + id));
			handler.deleteById("Collection", id);
			handler.clean();
			return CollectionUtils.message("Collection " + id + " has been deleted.");		
	}
	
}
