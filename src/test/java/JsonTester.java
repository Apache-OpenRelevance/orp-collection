import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.orp.collection.utils.JsonUtils;
import org.restlet.ext.json.JsonRepresentation;

import junit.framework.TestCase;

public class JsonTester extends TestCase{
	public void testJsonObject() throws IOException, JSONException{
		Map<String, Object> obj = new HashMap<String, Object>();
		Map<String, Object> urls = new HashMap<String, Object>();
		obj.put("id", "123");
		obj.put("name", "bob");
		urls.put("1", "bob.org");
		urls.put("2", "john.org");
		obj.put("url", urls);
		
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("root", obj);
		
		
		JsonRepresentation entity = new JsonRepresentation(root);
		System.out.println(entity.getText());
		Map<String, Object> map = new ObjectMapper().readValue(entity.getStream(), HashMap.class);
	}
}
