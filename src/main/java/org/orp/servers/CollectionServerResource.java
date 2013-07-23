package org.orp.servers;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.restlet.data.Status;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.ext.wadl.WadlServerResource;
import org.restlet.representation.Representation;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.orp.commons.CollectionResource;
import org.orp.utils.CollectionUtils;
import org.orp.utils.DBHandlerImpl;

public class CollectionServerResource extends WadlServerResource implements CollectionResource{

	private DBHandlerImpl handler;
	private String id;
	private String name;
	private Map<String, Object> info;
	
	@Override
	public void doInit(){
			handler = DBHandlerImpl.newHandler("jdbc:sqlite:db/collection.db");
			id = getRequest().getResourceRef().getIdentifier()
					.replaceAll("http://.*/collections/", "");
			info = handler.selectAllById("COLLECTION", id);
			if(info == null){
				setStatus(Status.CLIENT_ERROR_NOT_FOUND);
				return;
			}
			name = (String)info.get("name");
	}
	
	public Representation execute(JsonRepresentation entity){
		
		return null;
	}
	
	public Representation present(){
		Map<String, Object> summary = new HashMap<String, Object>();
		summary.put("id", id);
		summary.put("name", name);
		if(info.get("topics_size") != null){
			summary.put("topics", getRequest().getResourceRef().getIdentifier()+ "/topics");
			summary.put("topics_size", info.get("topics_size"));
		}else
			summary.put("topics", "N/A");
		if(info.get("qrels_size") != null ){
			summary.put("qrels", getRequest().getResourceRef().getIdentifier()+ "/qrels");
			summary.put("qrels_size", info.get("qrels_size"));
		}else
			summary.put("qrels", "N/A");
		return new JsonRepresentation(summary);
	}

	public Representation store(Representation entity) {
		try{	
			DiskFileItemFactory factory = new DiskFileItemFactory();
			factory.setSizeThreshold(1024000);
			RestletFileUpload upload = new RestletFileUpload(factory);
			List<FileItem> items = upload.parseRepresentation(entity);
			FileItem fileItem = null;
			StringBuilder repoPath = new StringBuilder("collections/" + id);
			Map<String, String> map = new HashMap<String, String>();
			for(FileItem fi : items){
				map.put(fi.getFieldName(), fi.getString());
				if(fi.getFieldName().equals("upload"))
					fileItem = fi;
			}
	
			String type = map.get("type");
			String corpus = map.get("corpus");
	
			if(type.equals("topics"))
				repoPath.append("/topics/");
			else if(type.equals("qrels"))
				repoPath.append("/qrels/");
			else if(map.get("type") == null){
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				return CollectionUtils.message("Please specify file type(\"topics\" or \"qrels\")");
			}
			else{
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				return CollectionUtils.message("Problematic request");
			}
			compressedWrite(fileItem, repoPath.append(type + ".gz").toString());
			Map<String, Object> updates = new HashMap<String, Object>();
			updates.put(type + "_size", (int)new File(repoPath.toString()).length());
			if(corpus != null) updates.put("corpus", corpus);
			Map<String, Object> conds = new HashMap<String, Object>();
			conds.put("id", id);
			handler.update("Collection", updates, conds);
		}catch(FileUploadException fue){
			fue.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return CollectionUtils.message("Collection uploaded successfully.");
	}
	
	private void compressedWrite(FileItem fi, String path)
			throws CompressorException, IOException{
			InputStream fileIn = new BufferedInputStream(fi.getInputStream());
			CompressorOutputStream gzipOut = new CompressorStreamFactory()
				.createCompressorOutputStream(CompressorStreamFactory.GZIP,
						new BufferedOutputStream(new FileOutputStream(path)));
			int read = 0;
			byte[] bytes = new byte[1024];
			while((read = fileIn.read(bytes)) != -1)
				gzipOut.write(bytes, 0, read);
			fileIn.close();
			gzipOut.close();
		}
}
