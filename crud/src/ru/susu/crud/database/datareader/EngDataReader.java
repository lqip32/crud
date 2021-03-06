package ru.susu.crud.database.datareader;

import ru.susu.crud.database.connection.ConnectionManager;
import ru.susu.crud.database.dataset.Dataset;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public class EngDataReader {
    private String sql;
    private ConnectionManager connectionManager;
    private Dataset dataset;

    public EngDataReader(String sql, Dataset dataset, ConnectionManager connectionManager) {
        this.sql = sql;
        this.dataset = dataset;
        this.connectionManager = connectionManager;
    }

    public void execute() throws Exception{
        java.sql.Connection connection = connectionManager.getConnection();
        PreparedStatement selectDataFromTable = connection.prepareStatement(sql);
        connection.setAutoCommit(false);
        ResultSet resultSetOfData = selectDataFromTable.executeQuery();
        ResultSetMetaData resultSetMetaData = resultSetOfData.getMetaData();
        int columnCount = resultSetMetaData.getColumnCount();
        dataset.clear();
        String[] line = new String[columnCount];
        int j = 0;
        while (resultSetOfData.next()) {
            j++;
            for (int i = 1; i <= columnCount; i++) {
                line[i - 1] = resultSetOfData.getString(i);
            }
            dataset.insertLine(line);
        }
        connection.commit();
        connection.setAutoCommit(true);
    }

}
