package ru.susu.crud.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Entry point classes define <code>onModuleLoad()</code>
 */
public class crud implements EntryPoint {
    public static final String FIND_ITEM = "Поиск";
    public static final String ADD_ITEM = "Добавление";
    public static final String UPDATE_ITEM = "Редактирование";

    private VerticalPanel chooseTablePanel = new VerticalPanel();
    private Label chooseTableLabel = new Label("Choose the table");
    private ListBox tablesComboBox = new ListBox(false);
    private String currentTable;

    private VerticalPanel subMainPanel = new VerticalPanel();
    private HorizontalPanel comboBoxPanel = new HorizontalPanel();
    private Label comboBoxLabel = new Label();
    private ListBox comboBox = new ListBox(false);
    private HorizontalPanel insertPanel = new HorizontalPanel();
    private final FlexTable table = new FlexTable();

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {

        chooseTablePanel.add(chooseTableLabel);
        chooseTablePanel.add(tablesComboBox);

        comboBoxLabel.setText("Действие");
        comboBox.addItem(FIND_ITEM);
        comboBox.addItem(ADD_ITEM);
        comboBox.addItem(UPDATE_ITEM);
        comboBoxPanel.add(comboBoxLabel);
        comboBoxPanel.add(comboBox);

        //отрефакторить это ужасное дублирование
        Label insertLabel = new Label("Символы");
        final TextBox insertTextBox = new TextBox();
        Button insertButton = new Button("Поиск");

        insertPanel.add(insertLabel);
        insertPanel.add(insertTextBox);
        insertPanel.add(insertButton);

        insertButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                //crudService.App.getInstance().find(insertTextBox.getText(), new FindAsyncCallBack(table));
            }
        });

        comboBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent changeEvent) {
                insertPanel.clear();
                if (comboBox.getItemText(comboBox.getSelectedIndex()).equals(FIND_ITEM)) {
                    Label insertLabel = new Label("Символы");
                    final TextBox insertTextBox = new TextBox();
                    Button insertButton = new Button("Поиск");

                    insertPanel.add(insertLabel);
                    insertPanel.add(insertTextBox);
                    insertPanel.add(insertButton);

                    insertButton.addClickHandler(new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent clickEvent) {
                            //crudService.App.getInstance().find(insertTextBox.getText(), new FindAsyncCallBack(table));
                        }
                    });
                }
                if (comboBox.getItemText(comboBox.getSelectedIndex()).equals(ADD_ITEM)) {
                    final Label insertLabel = new Label("Строка");
                    final TextBox insertTextBox = new TextBox();
                    Button addButton = new Button("Добавить");
                    insertPanel.add(insertLabel);
                    insertPanel.add(insertTextBox);
                    insertPanel.add(addButton);
                }
                if (comboBox.getItemText(comboBox.getSelectedIndex()).equals(UPDATE_ITEM)) {
                    final Label insertLabel = new Label("Изменение");
                    final TextBox insertTextBox = new TextBox();
                    Button updateButton = new Button("Изменить");
                    insertPanel.add(insertLabel);
                    insertPanel.add(insertTextBox);
                    insertPanel.add(updateButton);
                }
            }
        });

        /*tablesComboBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent changeEvent) {
                try {
                    crudService.App.getInstance().getData(tablesComboBox.getItemText(tablesComboBox.getSelectedIndex()), new ViewDataAsyncCallBack(table));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });*/

        //crudService.App.getInstance().getStrings(new ViewAsyncCallback(table));

       //try {
            crudService.App.getInstance().getTables(new ViewTablesAsyncCallBack(tablesComboBox));
        //} catch (Exception e) {
        //    e.printStackTrace();
        //}
        //crudService.App.getInstance().setTable("students", new VoidAsyncCallback());
        currentTable = "students";
        //try {
        crudService.App.getInstance().getData(currentTable, new ViewDataAsyncCallBack(table));
        //} catch (Exception e) {
          //  e.printStackTrace();
        //}

        table.setBorderWidth(1);

        subMainPanel.add(comboBoxPanel);
        subMainPanel.add(insertPanel);
        subMainPanel.add(table);

        RootPanel.get("root").add(subMainPanel);
        RootPanel.get("tables_list").add(chooseTablePanel);
    }


    private static class VoidAsyncCallback implements AsyncCallback<Void> {

        @Override
        public void onFailure(Throwable caught) {
        }

        @Override
        public void onSuccess(Void result) {
        }
    }

    private static class FindAsyncCallBack implements AsyncCallback<ArrayList<String>> {
        private FlexTable table;

        public FindAsyncCallBack(FlexTable table) {
            this.table = table;
        }

        @Override
        public void onFailure(Throwable throwable) {
        }

        @Override
        public void onSuccess(ArrayList<String> result) {
            this.table.removeAllRows();
            int i = 0;
            for (String s : result) {
                this.table.setText(i, i, s);
                i++;
            }
        }
    }

    private static class ViewAsyncCallback implements AsyncCallback<ArrayList<String>> {

        private FlexTable table;

        public ViewAsyncCallback(FlexTable table) {
            this.table = table;
        }

        @Override
        public void onFailure(Throwable caught) {
        }

        @Override
        public void onSuccess(ArrayList<String> result) {
            for (String s : result) {
                this.table.setText(this.table.getRowCount(), 0, s);
            }
        }
    }

    private class ViewTablesAsyncCallBack implements AsyncCallback<List<String>> {
        private ListBox innerViewCombo;

        public ViewTablesAsyncCallBack(ListBox comboBox) {
            this.innerViewCombo = comboBox;
        }

        @Override
        public void onFailure(Throwable throwable) {
            //Window.alert("!!!");
        }

        @Override
        public void onSuccess(List<String> result) {
            for (String tab : result) {
                this.innerViewCombo.addItem(tab);
            }
            //currentTable = this.innerViewCombo.getItemText(this.innerViewCombo.getSelectedIndex());
            innerViewCombo.setSelectedIndex(-1);
        }
    }

    private class ViewDataAsyncCallBack implements AsyncCallback<Map<String, String[]>> {
        private FlexTable table;

        public ViewDataAsyncCallBack(FlexTable table) {
            this.table = table;
        }

        @Override
        public void onFailure(Throwable throwable) {

        }

        @Override
        public void onSuccess(Map<String, String[]> result) {
            Window.alert(result.toString());
            /*table.removeAllRows();
            int column = 0;
            for (String header : result.keySet()) {
                int row = 0;
                table.setText(row,column,header);
                row++;
                for (String s : result.get(header)){
                    table.setText(row,column,s);
                    row++;
                }
            } */
        }
    }
}
