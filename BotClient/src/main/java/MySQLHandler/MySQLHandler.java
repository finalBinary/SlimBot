package MySQLHandler;

//import ErrorHandling.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/*public class MySQLHandler{

	//MySQLHandler1 hand = new MySQLHandler1("localhost", "streamD", "StreamManager", "123");

	public static void main(String args[]){
		MySQLHandler1 hand = new MySQLHandler1("localhost", "streamDB", "StreamManager", "12");
		System.out.println("hi");
	}

}*/

public class MySQLHandler {

	protected Connection conn = null;
	protected String dbHost = null;//Host name
	protected String database = null; //Database name
	protected String dbUser = null; //database username
	protected String dbPassword = null; // database password


	public MySQLHandler(String Host, String db, String User, String Password) {

		dbHost=Host;
		database=db;
		dbUser=User;
		dbPassword=Password;

		try{

			Class.forName("com.mysql.jdbc.Driver"); // load database driver foe ODBC interface

			conn = DriverManager.getConnection("jdbc:mysql://" + Host // establish connection to database
					+ "/" + "?" + "user=" + User + "&"
					+ "password=" + Password);

			conn.createStatement().executeQuery("USE "+db); //Use DB

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {

			if(e.getErrorCode() == 1044){ //if access to db denied
				String sqlCheckIfDBexists = "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = '"+db+"'";

				try{
					if(conn.createStatement().executeQuery(sqlCheckIfDBexists).next()){
						System.out.println("Access to database was denied.\nMaybe the user has not the right permissions?");
						System.exit(1);
					} else{
						System.out.println("Database "+db+" does not exist.\nPlease create and restart.");
						System.exit(1);
					}

				} catch (SQLException e2){
					e2.printStackTrace();
					System.exit(1);
				}
			}

			if(e.getErrorCode() == 1045){ // if accsess to user denied
				System.out.println("Access denied for user.\nUsername or password wrong?");
				System.exit(1);
			}
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void closeConnection(){
		try{
			conn.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}


	public List<String> getcolumn(String table, String name) {
		List<String> buflist = new ArrayList<String>();
		if(conn != null)
		{
			// create query
			Statement query;
			try {
				query = conn.createStatement();

				String sql = "SELECT "+name+" FROM "+table;
				ResultSet result = query.executeQuery(sql);

				while (result.next()) {
					buflist.add(result.getString(name)); // Alternativ: result.getString(2);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return buflist;
	}

	public void query(String qry){
		try {
			Statement stmnt = conn.createStatement();
			stmnt.executeUpdate(qry);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}



	public void addEntry(String table, String botName, String json){
		String sql = "INSERT INTO "+table+" (botName, status, dt) VALUES('"+botName+"', '"+json+"', NOW())";

		try {
			conn.createStatement().executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public String getEntry(String table){
		return getEntry(table, "");
	}


	public String getEntry(String table, String user){
		Statement query;
		ResultSet result = null;
		String sqlQueryString;
		if(user == ""){
			sqlQueryString = "SELECT * FROM "+table;
		} else {
			sqlQueryString = "SELECT * FROM "+table+" WHERE botName = '"+user+"'";
		}	
		try {
			query = conn.createStatement();
			result = query.executeQuery(sqlQueryString);
			if (!result.isBeforeFirst() ) {
				return "{}";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return convertToJSON(result);
	}

	public String getBotNames(String table){
		Statement query;
		ResultSet result = null;
		String sqlQueryString = "SELECT DISTINCT botName FROM "+table;
		try {
			query = conn.createStatement();
			result = query.executeQuery(sqlQueryString);
			if (!result.isBeforeFirst() ) {
				return "{}";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		List<String> jsonArray = new ArrayList<String>();
		String buf = "";
		try{
			int totalRows = result.getMetaData().getColumnCount();
		        while (result.next()) {
				buf = "{\"" + result.getMetaData().getColumnLabel(totalRows).toLowerCase().toString() + "\"" 
						+ ": \"" + result.getObject(totalRows).toString()+"\"}";
				jsonArray.add(buf);
        		}
		}catch(Exception e){
			e.printStackTrace();
		}
	        return jsonArray.toString();


		//return convertToJSON(result);
	}


	public  String convertToJSON(ResultSet resultSet){

	        List<String> jsonArray = new ArrayList<String>();
		String buf = "";
		try{
	        while (resultSet.next()) {
	        	int totalRows = resultSet.getMetaData().getColumnCount();
			buf = "{";
	        	for (int i = 0; i < totalRows - 1; i++) {
		                buf = buf + "\""+resultSet.getMetaData().getColumnLabel(i + 1).toLowerCase().toString() + "\"" 
					+ ": " + "\"" + resultSet.getObject(i + 1).toString() + "\"" + ",";
			}

			buf = buf + "\"" + resultSet.getMetaData().getColumnLabel(totalRows).toLowerCase().toString() + "\"" 
					+ ": " 
					+ (resultSet.getObject(totalRows).toString() != "" ? resultSet.getObject(totalRows).toString() : "{}") 
					+ "}"; 
			jsonArray.add(buf);
        	}
		}catch(Exception e){
			e.printStackTrace();
		}
	        return "{ \"data\": "+jsonArray.toString() +"}";
	}
	


	public boolean checkrowentry(String table, String user, String posts, String follower, String following, String date, String givenlikes){		
		Statement query;
		ResultSet result;
		String check = "SELECT * FROM "+table+" WHERE user = '"+user+"' AND posts = "+posts+" AND follower = "+follower+" AND following = "+following+" AND date = '"+date+"' AND givenlikes ="+givenlikes;                            
		try {
			query = conn.createStatement();
			result = query.executeQuery(check);
			if (!result.isBeforeFirst() ) {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return true;
	}

	public boolean checkentry(String table, String id, String entry) {
		System.out.println("in checkentry");
		Statement query;
		ResultSet result;
		String check = "SELECT * FROM "+table+" WHERE "+id+" = '"+entry+"'";
		try {
			query = conn.createStatement();
			result = query.executeQuery(check);
			if (!result.isBeforeFirst() ) {
				System.out.println("in checkentry: return false");
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("in checkentry: return true");
		return true;
	}

}
