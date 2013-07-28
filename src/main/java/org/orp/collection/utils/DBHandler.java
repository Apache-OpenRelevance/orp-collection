package org.orp.collection.utils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

public interface DBHandler {
	/**
	 * 
	 * @param a table's name
	 * @return true if the table exists; false if the table does not exist
	 * @throws SQLException
	 * 
	 * This method is used for determining if a table exists 
	 */
	public boolean exist(String tabName) throws SQLException;	
	
	/**
	 * 
	 * @param SQL statement for creating a table, 
	 * i.e. CREATE TABLE TEST(
	 * ID VARCHAR(20) PRIMARY KEY NOT NULL, 
	 * NAME VARCHAR(30) NOT NULL,
	 * ...)
	 * 
	 * This method is used for creating a table
	 */
	public void createTable(String createStmt) throws SQLException;
	
	/**
	 * 
	 * @param a table's name
	 * @return all the fields in the table.
	 * 
	 * This method is used for returning all the data storing in the table. An instance of 
	 * Map<String, Object> stands for a row and a set stands for the whole table. 
	 */
	
	public Set<Map<String, Object>> selectAll(String tabName);
	
	/**
	 * 
	 * @param a table's name
	 * @param an id, the primary key of the table
	 * @return a row identified by the id
	 * 
	 * This method is used for identifying data for a specific id. 
	 * 
	 * Since this method may involve user input, prepared SQL statement is required. 
	 */
	
	public Map<String, Object> selectAllById(String tabName, String id);
	/**
	 * 
	 * @param a table's name
	 * @param conditions specified in WHERE clause.
	 * @return rows identified by the conditions. 
	 * 
	 * This method is a generic method used for selecting row(s) from a table.The conditions map 
	 * consists of pairs of field names and values used as restraints. For now, it only needs to 
	 * support simple logic in the conditions, such as "=" and "AND". More complex logics should 
	 * be considered in the future. 
	 * 
	 * Since this method may involves user input, prepared SQL statement is required.  
	 */
	
	public Set<Map<String, Object>> select(String tabName, Map<String, Object> conditions);
	
	/**
	 *
	 * @param a table's name
	 * @param a group of fieldName-value pairs to be inserted into the table
	 * 
	 * This method is used for inserting a row into a table. 
	 * 
	 * Since this method may involves user input, prepared SQL statement is required. 	
	 */
	public void insert(String tabName, Map<String, Object> values);
	
	/**
	 * 
	 * @param tabName
	 * @param values
	 * @param id
	 */
	public void updateById(String tabName, Map<String, Object> values, String id);
	
	/**
	 * 
	 * @param a table's name
	 * @param a group of fieldName-value pairs to be updated in the table. 
	 * @param a group of fieldName-value pairs representing conditions in the WHERE clause
	 * 
	 * This method is used for updating a table. 
	 * 
	 * Since this method may involves user input, prepared SQL statement is required. 
	 */
	public void update(String tabName, Map<String, Object> values, Map<String, Object> conds);
	
	/**
	 * 
	 * @param a table's name
	 * @param an id
	 * 
	 * This method is used for deleting a row in a table by its id. 
	 * 
	 * Since this method may involves user input, prepared SQL statement is required. 	
	 */
	public void deleteById(String tabName, String id);
	
	/**
	 * 
	 * @param a table's name
	 * @param conditions specified in WHERE clause
	 * 
	 * This method is a generic method used for deleting row(s) in a table. The conditions map 
	 * consists of pairs of field names and values used as restraints. For now, it only needs 
	 * to support simple logic in the conditions, such as "=" and "AND". More complex logics 
	 * should be considered in the future. 
	 * 
	 * Since this method may involves user input, prepared SQL statement is required.
	 */
	public void delete(String tabName, Map<String, Object> conditions);
	
	/**
	 * 
	 * @param a table's name
	 * @return metadata of the table, including field names and their data types. 
	 * @throws SQLException
	 * 
	 * This method is used for getting metadata of a table. 
	 */
	public Map<String, Integer> getTableInfo(String tabName) throws SQLException;
	
	/**
	 * 
	 * @param a result set returned by a SELECT query. 
	 * @return field names and their data types in the result set. 
	 * @throws SQLException
	 * 
	 * This method is used for getting the field names and their data types in the result set. 
	 */
	public Map<String, Integer> getFieldsTypes(ResultSet rs) throws SQLException;
	
	/**
	 * This method is used for closing resources and connections.
	 */
	public void clean() throws SQLException;
	
	/**
	 * 
	 * @param a query to be prepared
	 * @param a table's name
	 * @param a set of values to be assigned to the query. The values should correspond to the field
	 * names listed in the query, so does the order of these values. Since order matters, consider 
	 * SortedMap such as TreeMap. 
	 *  
	 * @return a prepared SQL statement
	 * @throws SQLException
	 * 
	 * This method is used for prepared a SQL statement.
	 * 
	 * It is supposed to be a private method in prodcuction.
	 */
	public PreparedStatement setPreparedParams(String query, String tabName, Map<Integer, Object> orderValues) throws SQLException;
	
	/**
	 * 
	 * @param a result set returned by a SELECT query.
	 * @return a Java representation of the result set. An instance of Map<String, Object> stands for
	 * a row and an instance of the set stands for all the results. 
	 * 
	 * This method is used for converting a JDBC ResultSet into a Java representation, for more 
	 * convenience of retrieving data from the result. 
	 * 
	 * It is supposed to be a private method in production.
	 */
	public Set<Map<String, Object>> toResultSet(ResultSet rs) throws SQLException; 
	
	/**
	 * 
	 * @param  a table's name, but not limited to it. It can be any part of a query which is 
	 * represented by a java variable and may contains SQL special characters, leading to a SQL
	 * injection hazard. 
	 * 
	 * @return a clean SQL element with all special characters removed.
	 * 
	 * This method is used for removing SQL special characters in a string. See details by 
	 * googling "SQL special characters".
	 * 
	 * It is supposed to be a private method in production.
	 */
	public String removeSpecialChars(String tabName);
	
	/**
	 * 
	 * @param  a table's name, but not limited to it. It can be any part of a query which is 
	 * represented by a java variable and may contains SQL special characters, leading to a SQL
	 * injection hazard. 
	 *  
	 * @return a clean SQL element with all special characters escaped.
	 *
	 * This method is used for escaping SQL special characters in a string. See details by 
	 * googling "SQL special characters".
	 * 
	 * It is supposed to be a private method in production.
	 */
	public String escapeSpecialChars(String tabName);
	
	
	
}
