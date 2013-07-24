
import java.sql.Date;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.codehaus.jettison.json.JSONArray;
import org.orp.colletion.utils.DBHandlerImpl;


public class DBHandlerTester extends TestCase{
	public void testDelete() throws ClassNotFoundException, SQLException{	
//		DBHandlerImpl handler = DBHandlerImpl.newHandler("jdbc:sqlite:db/collection.db");
//		if(!handler.exist("TEST"))
//			handler.createTable("CREATE TABLE TEST(" +
//				"TIME DATE)");
//		Map<String, Object> data = new HashMap<String, Object>();
//		DateFormat pattern = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//		data.put("time", pattern.format(new Date(System.currentTimeMillis())));
//		handler.insert("TEST", data);
//		Set<Map<String, Object>> result = handler.selectAll("TEST");
//		for(Map<String, Object> row : result)
//			for(String key : row.keySet())
//				System.out.println(key + ": " + row.get(key));
//		Map<String, Object> values = new HashMap<String, Object>();
//		values.put("topics_size", "100");
//		values.put("qrels_size", "200");
//		
//		Map<String, Object> conds = new HashMap<String, Object>();
//		conds.put("id", "034c0bb2b799402797213833c30445b6");
//		
//		handler.update("collection", values, conds);
		JSONArray arr = new JSONArray();
	}
}
