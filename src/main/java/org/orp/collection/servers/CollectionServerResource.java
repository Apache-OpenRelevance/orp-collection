package org.orp.collection.servers;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.DriverManager;
import java.sql.SQLException;
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
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.orp.collection.commons.CollectionResource;
import org.orp.collection.exceptions.InvalidCommandException;
import org.orp.collection.utils.CollectionUtils;
import org.orp.collection.utils.DBHandler;
import org.orp.collection.utils.DBHandlerImpl;
import org.orp.collection.utils.JsonUtils;

public class CollectionServerResource extends WadlServerResource implements CollectionResource{

	private DBHandler handler;
	private String id;
	private String name;
	private Map<String, Object> info;
	
	@Override
	public void doInit(){
		
				try {
					handler = DBHandlerImpl.newHandler(DriverManager.getConnection("jdbc:sqlite:db/collection.db"));
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
			id = getRequest().getResourceRef().getIdentifier()
					.replaceAll("http://.*/collections/", "");
			info = handler.selectAllById("COLLECTION", id);
			if(info == null){
				setStatus(Status.CLIENT_ERROR_NOT_FOUND);
				return;
			}
			name = (String)info.get("name");
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

	public Representation execute(JsonRepresentation entity){
		try {
			Map<String, Object> data = JsonUtils.toMap(entity);
			if(data.size() != 1)
				throw new InvalidCommandException("");
			if(data.get("update-info") != null){
				@SuppressWarnings("unchecked")
				Map<String, Object> info = (Map<String, Object>)data.get("update-info");
				handler.updateById("COLLECTION", info, id);
				return CollectionUtils.message("Update successful.");
			}else{
				throw new InvalidCommandException("");
			}
		} catch (JsonParseException e) {
			return CollectionUtils.message("Parse Error.");
		} catch (JsonMappingException e) {
			return CollectionUtils.message("Mapping Error.");
		} catch (IOException e) {
			return CollectionUtils.message("IO Error.");
		} catch (InvalidCommandException e){
			return CollectionUtils.message("Invalid Command.");
		}
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
