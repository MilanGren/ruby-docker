package com.example.springboot;

/**
 * Copyright 2009-2015 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import org.apache.ibatis.jdbc.RuntimeSqlException;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Clinton Begin
 */
public class MyScriptRunner {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");

    private static final String DEFAULT_DELIMITER = ";";

    public Connection connection;

    private boolean stopOnError;
    private boolean autoCommit;
    private boolean sendFullScript;
    private boolean removeCRs;
    private boolean escapeProcessing = true;

    private PrintWriter logWriter = new PrintWriter(System.out);
    private PrintWriter errorLogWriter = new PrintWriter(System.err);

    private String delimiter = DEFAULT_DELIMITER;
    private boolean fullLineDelimiter = false;

    public MyScriptRunner(Connection connection) {
        this.connection = connection;
    }

    public void runScript(Reader reader) {
        executeFullScript(reader);
    }

    private void executeFullScript(Reader reader) {
        StringBuilder script = new StringBuilder();
        try {
            BufferedReader lineReader = new BufferedReader(reader);
            String line;
            while ((line = lineReader.readLine()) != null) {
                script.append(line);
                script.append(LINE_SEPARATOR);
            }
            String command = script.toString();
            println(command);
            executeStatement(command);
        } catch (Exception e) {
            String message = "Error executing: " + script + ".  Cause: " + e;
            printlnError(message);
            throw new RuntimeSqlException(message, e);
        }
    }

    public void commit() {
        try {
            connection.commit();
        } catch (SQLException e) {
            String message = "Error executing.  Cause: " + e;
            printlnError(message);
            throw new RuntimeSqlException(e);
        }
    }

    public void rollback() {
        try {
            connection.rollback();
        } catch (SQLException e) {
            String message = "Error executing.  Cause: " + e;
            printlnError(message);
            throw new RuntimeSqlException(e);
        }
    }


    public void executeStatement(String command) throws SQLException {
        boolean hasResults = false;
        //connection.setAutoCommit(false);
        Statement statement = connection.createStatement();
        String sql = command;
        if (removeCRs) {
            sql = sql.replaceAll("\r\n", "\n");
        }

        if (stopOnError) {
            hasResults = statement.execute(sql);
        } else {
            try {
                hasResults = statement.execute(sql);
            } catch (SQLException e) {
                String message = "Error executing: " + command + ".  Cause: " + e;
                printlnError(message);
            }
        }

        printResults(statement, hasResults);
        try {
            statement.close();
        } catch (Exception e) {
            // Ignore to workaround a bug in some connection pools
        }
    }

    private void printResults(Statement statement, boolean hasResults) {
        try {
            if (hasResults) {
                ResultSet rs = statement.getResultSet();
                if (rs != null) {
                    ResultSetMetaData md = rs.getMetaData();
                    int cols = md.getColumnCount();
                    for (int i = 0; i < cols; i++) {
                        String name = md.getColumnLabel(i + 1);
                        print(name + "\t");
                    }
                    println("");
                    while (rs.next()) {
                        for (int i = 0; i < cols; i++) {
                            String value = rs.getString(i + 1);
                            print(value + "\t");
                        }
                        println("");
                    }
                }
            }
        } catch (SQLException e) {
            printlnError("Error printing results: " + e.getMessage());
        }
    }

    private void print(Object o) {
        if (logWriter != null) {
            logWriter.print(o);
            logWriter.flush();
        }
    }

    private void println(Object o) {
        if (logWriter != null) {
            logWriter.println(o);
            logWriter.flush();
        }
    }

    private void printlnError(Object o) {
        if (errorLogWriter != null) {
            errorLogWriter.println(o);
            errorLogWriter.flush();
        }
    }

}
