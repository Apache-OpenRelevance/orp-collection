
import java.sql.Date;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.orp.utils.DBHandlerImpl;


public class DBHandlerTester extends TestCase{
	public void testDelete() throws ClassNotFoundException, SQLException{	
		DBHandlerImpl handler = DBHandlerImpl.newHandler("jdbc:sqlite:db/test.db");
		if(!handler.exist("TEST"))
			handler.createTable("CREATE TABLE TEST(" +
				"TIME DATE)");
		Map<String, Object> data = new HashMap<String, Object>();
		DateFormat pattern = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		data.put("time", pattern.format(new Date(System.currentTimeMillis())));
		handler.insert("TEST", data);
		Set<Map<String, Object>> result = handler.selectAll("TEST");
		for(Map<String, Object> row : result)
			for(String key : row.keySet())
				System.out.println(key + ": " + row.get(key));
	}
}
