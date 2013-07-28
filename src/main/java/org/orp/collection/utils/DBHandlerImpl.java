package org.orp.collection.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


public class DBHandlerImpl implements DBHandler{
	
	private static final Logger log = LogManager.getLogger(DBHandler.class.getName());
	
	Connection c;
	Statement stmt;
	PreparedStatement prepstmt;

	private DBHandlerImpl(Connection c){ 
		this.c = c;
		try{
			stmt = c.createStatement();
			prepstmt = null;
		}catch(SQLException se){
			se.printStackTrace();
		}
	}

	public static DBHandler newHandler(Connection c){
		return new DBHandlerImpl(c);
	}

	public Set<Map<String, Object>> selectAll(String tabName){
		Set<Map<String, Object>> result = new HashSet<Map<String, Object>>();
		try{
			 ResultSet rs = stmt.executeQuery("SELECT * FROM " + tabName);
			 result = toResultMap(rs);
			 clean();
		}catch(SQLException se){
			se.printStackTrace();
		}
		return result;
	}

	public Set<Map<String, Object>> toResultMap(ResultSet rs) 
			throws SQLException{
		Set<Map<String, Object>> result = new HashSet<Map<String, Object>>();
		Map<String, Integer> schema = getSchema(rs);
		while(rs.next()){
			Map<String, Object> row = new HashMap<String, Object>();
			for(String col : schema.keySet()){
				int type = schema.get(col);
				if(type == Types.VARCHAR
						|| type == Types.CHAR
						|| type == Types.LONGNVARCHAR
						|| type == Types.LONGVARCHAR)
					row.put(col, rs.getString(col));
				else if(type == Types.INTEGER
						|| type == Types.BIGINT)
					row.put(col, rs.getInt(col));
				else if(type == Types.FLOAT)
					row.put(col, rs.getFloat(col));
				else if(type == Types.DOUBLE
						|| type == Types.DECIMAL)
					row.put(col, rs.getDouble(col));
				else if(type == Types.DATE)
					row.put(col, rs.getDate(col));
				else{
					row = null;
					throw new SQLException("Unsupported type: " + type);
				}
			}
			result.add(row);
		}

		return result;
	} 

	public Map<String, Integer> getSchema(ResultSet rs) 
			throws SQLException{
		 ResultSetMetaData rsinfo = rs.getMetaData();
		 Map<String, Integer> schema = new HashMap<String, Integer>();
		 for(int i = 1; i <= rsinfo.getColumnCount(); i ++)
			 schema.put(rsinfo.getColumnName(i).toLowerCase(), rsinfo.getColumnType(i));
		 return schema;
	}
	
	public String insertSQL(String tabName, Map<String, Object> values) {
		log.info("insert()");
		log.info("inserting into table: " + tabName);
		
		if(tabName == null || tabName.isEmpty()) {
			throw new IllegalArgumentException(
					"no table name is provided to insert");
		}
		
		if(values == null || values.isEmpty()) {
			throw new IllegalArgumentException(
					"no value is provided to insert into table");
		}
		
		if(tabName.equals("COLLECTION")) {
			return insertIntoCollectionSQL(values);
		} else {
			throw new RuntimeException(
					"Please insert into the exit tables");
		}
		
	
	}
	
	private String insertIntoCollectionSQL(Map<String, Object> values) {
		log.info("insertIntoCollection()");

		String col_list = new String("");
		String value_list = new String("");
		String insertSQL = null;
		
		for(String key: values.keySet()) {
			col_list = col_list + key + ",";
			value_list = value_list + values.get(key) + ",";
		}
		
		if(col_list.length() > 0) {
			col_list = col_list.substring(0, col_list.length() - 1);
		}
		
		if(value_list.length() > 0) {
			value_list = value_list.substring(0, value_list.length() - 1);
		}
		
		//System.out.println("col_list: " + col_list);
		//System.out.println("value_list: " + value_list);
		
		insertSQL = "INSERT INTO COLLECTION(" + col_list + ") VALUES(" + value_list + ");";
		
		return insertSQL;
		
		
	}
	
	public String selectSQL(String tabName, List<String> attributes, TreeMap<String, Object> conditions) {
		log.info("select()");
		log.info("Table Name: " + tabName);
		
		String cond_list = "";
		String attr_list = "";
		String selectSQL;
		
		if(tabName == null || tabName.isEmpty()) {
			throw new IllegalArgumentException(
					"no table name is provided to select from");
		}
		
		if(attributes == null || attributes.isEmpty()) {
			log.info("attributes List is null, will return results with all attributes");
			attr_list = "*";
		}
		
		if(conditions == null || conditions.isEmpty()) {
			log.info("no select condition is put in, will return all items");
		}
		
		for (String att : attributes) {
			attr_list = attr_list + att + ",";
		}
		
		if(attr_list.length() > 0) {
			attr_list = attr_list.substring(0, attr_list.length() - 1);
		}
		
		
		for (String key : conditions.keySet()) {
			cond_list = cond_list + key + " = ? AND ";
		}
		
		if(cond_list.length() > 0) {
			cond_list = cond_list.substring(0, cond_list.length() - 5);
		} else {
			selectSQL = "SELECT " + attr_list + " FROM " + tabName;
		}
		
		System.out.println(attr_list);
		System.out.println(cond_list);
		
		selectSQL = "SELECT " + attr_list + " FROM " + tabName + " WHERE " + cond_list;
		
		return selectSQL;
		
	}
	
	public PreparedStatement setPrepStmt(String sql, TreeMap<String, Object> conditions) {
		log.info("setPrepStmt()");
		
		try {
			prepstmt = c.prepareStatement(sql);
			int count = 1;
			for(String key : conditions.keySet()) {
				Object value = conditions.get(key);
				if (value instanceof String)
					prepstmt.setString(count ++, (String)value);
				if (value instanceof Integer) 
					prepstmt.setInt(count ++, (Integer)value);
				if (value instanceof Double) 
					prepstmt.setDouble(count ++, (Double)value);
				if (value instanceof Float) 
					prepstmt.setFloat(count ++, (Float)value);
				if (value instanceof Date) {
					SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-DD hh:mm:ss");
					prepstmt.setString(count ++, sdf.format((Date)value));
				}
			}
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return prepstmt;
	}
	
	public List<String> selectUriByName(String tabName, String collectionName){
		log.info("selectUriByName()");
		log.info("Table Name: " + tabName);
		log.info("Collection Name: " + collectionName);
	
		
		if(tabName == null || tabName.isEmpty()) {
			throw new IllegalArgumentException(
					"no table name is provided to select from");
		}
		
		if(collectionName == null || collectionName.isEmpty()) {
			throw new IllegalArgumentException(
					"no collection Name is provided");
		}
		
		String sql = "SELECT URI FROM" + tabName + "WHERE NAME = ?";
		List<String> uri_List = new ArrayList<String>();
		
		try {
				prepstmt = c.prepareStatement(sql);
				prepstmt.setString(1, collectionName);
				c.commit();
				ResultSet rs = prepstmt.executeQuery();
				
				while(rs.next()) {
					String uri = rs.getString("URI");
					
					if(uri == null || uri.isEmpty()) {
						log.error("no or empty URI is returned for: " + collectionName);
						uri = "null";
					}
					uri_List.add(uri);
				}
				
				rs.close();
				clean();
				
				
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return uri_List;
		
	}
	
	public String updateSQL(String tabName, TreeMap<String, Object> values, TreeMap<String, Object> conds) {
		
		String att_list = "";
		String cond_list = "";
		String updateSQL;
		
		if(tabName == null || tabName.isEmpty()) {
			throw new IllegalArgumentException(
					"no table name is provided to update");
		}
		
		if(values == null || values.isEmpty()) {
			log.info("attributes and values for updating is null");
		}

		
		for (String key: values.keySet()) {
			att_list = att_list + key + "= ?,";
		}
		
		if(att_list.length() > 0) {
			att_list = att_list.substring(0, att_list.length() - 1);
		}
		System.out.println("update: att_list: " + att_list);
		
		
		
		if(conds == null || conds.isEmpty()) {
			log.info("no select condition is put in, will update values for all items");
			updateSQL = "UPDATE " + tabName + " SET " + att_list;
		} else {
			conds.putAll(conds);
			for (String key: conds.keySet()) {
				cond_list = cond_list + key + " = ? AND ";
			}
			
			cond_list = cond_list.substring(0, cond_list.length() - 5);
			
			System.out.println("update cond_list: " + cond_list);
			
			updateSQL = "UPDATE " + tabName + " SET " + att_list + " WHERE " + cond_list;
		}
		
		return updateSQL;
		
	}
	
	public void update(String tabName, Map<String, Object> values, Map<String, Object> conds) {
		
		TreeMap<String, Object> sortedValues = new TreeMap<String, Object>();
		
		TreeMap<String, Object> sortedConds = new TreeMap<String, Object>();
		sortedConds.putAll(conds);
		
		String updateSQL = updateSQL(tabName, sortedValues, sortedConds);
		prepstmt = setPrepStmt(updateSQL, sortedConds);
		
		try {
			prepstmt.executeUpdate();
			c.commit();
			clean();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void updateById(String tabName, Map<String, Object> values, String id) {
		log.info("updateById()");
		log.info("update for id: " + id);
		
		
		
	}
	
	public String deleteSQL(String tabName, TreeMap<String, Object> conds) {
		String cond_list = "";
		String deleteSQL;
		
		if(tabName == null || tabName.isEmpty()) {
			throw new IllegalArgumentException(
					"no table name is provided to delete the rows");
		}
		
		if(conds == null || conds.isEmpty()) {
			log.info("no conditions are provided, will delete all");
			log.info("deleteAll():" + tabName);
			deleteAll(tabName);
			log.info("deleted all");
			return "";
		}
		
		for(String key: conds.keySet()) {
			cond_list = cond_list + key + "=? AND ";
		}
		
		if(cond_list.length() > 0) {
			cond_list = cond_list.substring(0, cond_list.length() - 5);
		}
		
		deleteSQL = "DELETE FROM " + tabName + " WHERE " + cond_list;
		return deleteSQL;
		
	}
	
	public void deleteAll(String tabName) {
		log.info("deleteAll()");
		
		try {
				stmt = c.createStatement();
				String delete_sql = "DELETE FROM " + tabName;
				stmt.executeUpdate(delete_sql);
				
				clean();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void delete(String tabName, Map<String, Object> conds) {
		log.info("delete()");
		TreeMap<String, Object> sortedConds = new TreeMap<String, Object>();
		sortedConds.putAll(conds);
		
		String deleteSQL = deleteSQL(tabName, sortedConds);
		prepstmt = setPrepStmt(deleteSQL, sortedConds);
		
		try {
			prepstmt.executeUpdate(deleteSQL);
			c.commit();
			clean();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void deleteById(String tabName, String id) {
		
		String deleteSQL = "DELETE FROM " + tabName + " WHERE ID = ?";
		
		try {
				prepstmt = c.prepareStatement(deleteSQL);
				prepstmt.setString(1, id);
				c.commit();
				clean();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void clean() throws SQLException {
		
		stmt.close();
		c.close();
	}

	@Override
	public boolean exist(String tabName) throws SQLException {
		// TODO Auto-generated method stub
		log.info("exist()");
		log.info("check table name: " + tabName);
		ResultSet rs = c.getMetaData().getTables(null, null, tabName, null);
		
		if(rs == null) {
			throw new RuntimeException("The result set of searching for " + tabName + "is null");
		}
		
		if(!rs.isBeforeFirst()) {
			return false;
		} else {
			return true;
		}

	}

	@Override
	public void createTable(String createStmt) throws SQLException {
		// TODO Auto-generated method stub
		log.info("createTable()");
		
		stmt = c.createStatement();
		stmt.executeQuery(createStmt);
		
		log.info("table created");
		clean();
			
	}
	

	@Override
	public Map<String, Object> selectAllById(String tabName, String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Map<String, Object>> select(String tabName,
			Map<String, Object> conditions) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void insert(String tabName, Map<String, Object> values) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, Integer> getTableInfo(String tabName)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Integer> getFieldsTypes(ResultSet rs)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PreparedStatement setPreparedParams(String query, String tabName,
			Map<Integer, Object> orderValues) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Map<String, Object>> toResultSet(ResultSet rs)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String removeSpecialChars(String tabName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String escapeSpecialChars(String tabName) {
		// TODO Auto-generated method stub
		return null;
	}


}
