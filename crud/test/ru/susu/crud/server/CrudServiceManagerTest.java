package ru.susu.crud.server;

import org.junit.Before;
import org.junit.Test;
import ru.susu.crud.TestConnectionProperties;
import ru.susu.crud.database.ConnectionProperties;
import ru.susu.crud.database.connection.ConnectionManager;
import ru.susu.crud.database.dataset.Field;
import ru.susu.crud.database.dataset.IntegerField;
import ru.susu.crud.database.dataset.StringField;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CrudServiceManagerTest {
    private Statement statement;
    private ConnectionProperties connectionProperties = TestConnectionProperties.getConnectionProperties();
    private CrudServiceManager crudServiceManager;

    @Before
    public void setUp() throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        ConnectionManager connectionManager = new ConnectionManager(connectionProperties);
        statement = connectionManager.getConnection().createStatement();
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS testtable(id INTEGER KEY, "
                + "name VARCHAR(60));");
        crudServiceManager = new CrudServiceManager();
        crudServiceManager.setConnectionManager(connectionManager);
    }

    @Test
    public void addFieldsTest() throws Exception {
        String idFieldName = "id";
        String nameFieldName = "name";
        String idAlias = "";
        String nameAlias = "";
        String table = "testtable";
        boolean autoincrement = false;
        List<Field> fields = new LinkedList<Field>();
        Field idField = new IntegerField(idFieldName, idAlias, table, autoincrement);
        fields.add(idField);
        Field nameField = new StringField(nameFieldName, nameAlias, table, autoincrement);
        fields.add(nameField);
        crudServiceManager.addFields(table, fields);
        assertEquals(1, crudServiceManager.getTables().size());

    }

    @Test
    public void addEditorsTest() throws Exception {
        //not implemented;
        //TODO:implement
    }


    @Test
    public void getTablesTest() throws Exception {

    }

    @Test
    public void insertDataTest() throws Exception {

    }

    @Test
    public void updateDataTest() throws Exception {

    }

    @Test
    public void deleteDataTest() throws Exception {

    }


    @Test
    public void tearDown() throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        ConnectionManager connectionManager = new ConnectionManager(connectionProperties);
        statement = connectionManager.getConnection().createStatement();
        statement.executeUpdate("DROP TABLE IF EXISTS testtable;");
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS testtable(id INTEGER KEY, "
                + "name VARCHAR(60));");
    }
}