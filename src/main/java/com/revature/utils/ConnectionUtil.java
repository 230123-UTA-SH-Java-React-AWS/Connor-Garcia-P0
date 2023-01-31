package com.revature.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//This class is responsible for connecting to our database
public class ConnectionUtil {

    //We want only one connection to the database the entire time
    private static Connection con = null;

    //Private constructor to prevent this object from being instantiated
    private ConnectionUtil(){
        con = null;
    }

    //Method that gives us a connection to the DB
    //This will always return the existing connection if it does exist
    public static Connection getConnection() {
        try {
            if(con != null && !con.isClosed()) {
                return con;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String url, user, pass;
        //These fields are filled with local environment variables to
        // ensure that hardcoded credentials are never uploaded to
        // git
        url = System.getenv("PZEROURL");
        user = System.getenv("PZEROUSER");
        pass = System.getenv("PZEROPASSWORD");

        try {
            con = DriverManager.getConnection(url, user, pass);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return con;
    }
}
