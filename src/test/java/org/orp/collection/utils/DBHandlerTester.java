package org.orp.collection.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;


public class DBHandlerTester extends TestCase{
	
	static Connection con = null;
	static DBHandler dbh = null;
	
	
	@Before
	public void beforeTest(){
		

			try {
				Class.forName("org.sqlite.JDBC");
				con = DriverManager.getConnection("jdbc:sqlite:db/collection.db");
				dbh = DBHandlerImpl.newHandler(con);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

		
	}
	
	@Test
	public void TestGetTableInfo() {
		
		Map<String, Integer> tabInfo = new HashMap<String, Integer>();
		
		try {
				tabInfo = dbh.getTableInfo("COLLECTION");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
		}
		
		//System.out.println(tabInfo);
		
		TestCase.assertFalse("did not load table info", tabInfo.isEmpty());
		
	}
	
	@After
	public void afterTest() throws SQLException {
		con.close();
	}
	
	
}
