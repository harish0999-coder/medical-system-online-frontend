package com.java;

import java.sql.*;

public class DB {
	public static Connection con;
	
    public static Connection getConnection() {
       
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital?useSSL=false", "root", "Hareesh@999");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e);
        }
        return con;
    }
    
    public static void CloseConnection() {
		if (con != null) {
			try {
				con.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static ResultSet getResultFromSqlQuery(String SqlQueryString) {
		ResultSet rs = null;
		try {
			if (con == null) {
				getConnection();
			}
			rs = con.createStatement().executeQuery(SqlQueryString);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return rs;
	}

	public static int insertUpdateFromSqlQuery(String SqlQueryString) {
		int i = 2;
		try {
			if (con == null) {
				getConnection();
			}
			i = con.createStatement().executeUpdate(SqlQueryString);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return i;
	}
}
