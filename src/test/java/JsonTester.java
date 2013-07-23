import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;

import junit.framework.TestCase;

public class JsonTester extends TestCase{
	public void testJsonObject() throws IOException, JSONException{
		Map<String, Object> obj = new HashMap<String, Object>();
		obj.put("id", "123");
		obj.put("name", "bob");
		obj.put("url", "bob.org");
		
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("root", obj);
		
		JsonRepresentation entity = new JsonRepresentation(root);
		System.out.println(entity.getText());
		
		System.out.println(entity.getJsonObject().keys().next());
		for(String s : JSONObject.getNames(entity.getJsonObject()))
			System.out.println(s);
	}
}
