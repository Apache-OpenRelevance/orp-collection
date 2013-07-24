package org.orp.collection.servers;

import java.io.File;
import java.io.FileNotFoundException;

import org.orp.collection.commons.QrelsResource;
import org.orp.collection.utils.CollectionUtils;
import org.restlet.data.Disposition;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.wadl.WadlServerResource;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;

public class QrelServerResource extends WadlServerResource implements QrelsResource{

	public Representation download() {
		try{	
			String id = getRequest().getResourceRef().getIdentifier()
					.replaceAll("http://.*/collections/", "");
			File file = new File("collections/" + id + "/qrels/qrels.gz");
			if(!file.exists())
				throw new FileNotFoundException();
			FileRepresentation download = new FileRepresentation(
					file, MediaType.APPLICATION_GNU_TAR);
			Disposition disp = new Disposition(Disposition.TYPE_ATTACHMENT);
			disp.setFilename("qrels.gz");
			download.setDisposition(disp);
			return download;
		}catch(FileNotFoundException fe){
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return CollectionUtils.message("No file found");
		}
	}

	public Representation execute() {
		return null;
	}

}
