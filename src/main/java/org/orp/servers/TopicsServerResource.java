package org.orp.servers;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


import org.orp.commons.TopicsResource;
import org.orp.utils.CollectionUtils;
import org.restlet.data.Disposition;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.ext.wadl.WadlServerResource;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;

public class TopicsServerResource extends WadlServerResource implements TopicsResource{

	public Representation present() {
		Map<String, Object> cmd = new HashMap<String, Object>();
		cmd.put("command", "download");
		return execute(new JsonRepresentation(cmd));
	}
	
	public Representation execute(JsonRepresentation entity) {
		String cmd = CollectionUtils.getJsonValue(entity, "command");
		String dir = getRequest().getResourceRef().getIdentifier()
				.replaceAll("http://.*/collections/", "");
		if(cmd.equals("download")){
			File file = new File("collections/" + dir);
			if(file.list().length == 0){
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				return CollectionUtils.message("No file found");
			}
			FileRepresentation download = new FileRepresentation(
					file.listFiles()[0], MediaType.APPLICATION_GNU_TAR);
			Disposition disp = new Disposition(Disposition.TYPE_ATTACHMENT);
			disp.setFilename("test.img");
			download.setDisposition(disp);
			return download;
		}else if(cmd.equals("info")){
			return null;
		}else
			return null;
	}

}
