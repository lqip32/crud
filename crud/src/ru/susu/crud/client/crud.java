package ru.susu.crud.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.datepicker.client.DateBox;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Entry point classes define <code>onModuleLoad()</code>
 */
public class crud implements EntryPoint {
    public static final String PATTERN = "yyyy-MM-dd";
    private String currentTableName;
    private List<Map<String, String[]>> listOfEditors = new LinkedList<Map<String, String[]>>();

    private VerticalPanel chooseTablePanel = new VerticalPanel();
    private Label chooseTableLabel = new Label("Choose the table");
    private ListBox chooseTableComboBox = new ListBox(false);

    private VerticalPanel subMainPanel = new VerticalPanel();
    private Label tableHeader = new Label();
    private final FlexTable mainTable = new FlexTable();
    private Button addEntryButton = new Button("Add");

    public void setTableHeaderText(String text) {
        this.tableHeader.setText(text);
    }


    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        chooseTablePanel.add(chooseTableLabel);
        chooseTablePanel.add(chooseTableComboBox);

        chooseTableComboBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent changeEvent) {
                currentTableName = chooseTableComboBox.getItemText(chooseTableComboBox.getSelectedIndex());
                try {
                    baseView();
                } catch (Exception e) {
                }
            }
        });

        addEntryButton.addClickHandler(new AddEntryClickHandler());

        crudService.App.getInstance().getTables(new ViewTablesAsyncCallBack(chooseTableComboBox));
        RootPanel.get("root").add(subMainPanel);
        RootPanel.get("tables_list").add(chooseTablePanel);
    }

    private void baseView() {
        subMainPanel.clear();

        setTableHeaderText(currentTableName.toUpperCase());
        try {
            crudService.App.getInstance().selectData(currentTableName, new ViewDataAsyncCallBack(mainTable));
        } catch (Exception e) {
        }

        mainTable.setBorderWidth(1);
        mainTable.setCellSpacing(0);
        mainTable.setCellPadding(4);

        subMainPanel.add(tableHeader);
        subMainPanel.add(mainTable);
        subMainPanel.add(addEntryButton);
    }

    private class ViewTablesAsyncCallBack implements AsyncCallback<String[]> {
        private ListBox innerViewCombo;

        public ViewTablesAsyncCallBack(ListBox comboBox) {
            this.innerViewCombo = comboBox;
        }

        @Override
        public void onFailure(Throwable throwable) {
            Window.alert(throwable.toString());
        }

        @Override
        public void onSuccess(String[] result) {
            for (String tab : result) {
                this.innerViewCombo.addItem(tab);
            }
            innerViewCombo.setSelectedIndex(-1);
            currentTableName = innerViewCombo.getValue(0);
            baseView();
        }
    }

    private class ViewDataAsyncCallBack implements AsyncCallback<String[][]> {
        private FlexTable table;

        public ViewDataAsyncCallBack(FlexTable table) {
            this.table = table;
        }

        @Override
        public void onFailure(Throwable throwable) {
            Window.alert(throwable.toString());
        }

        @Override
        public void onSuccess(String[][] result) {
            mainTable.removeAllRows();
            this.table.removeAllRows();
            int i = 0;
            for (String[] line : result) {
                for (int j = 0; j < line.length; j++) {
                    this.table.setText(i + 2, j, line[j]);
                }
                if (i != 0) {
                    Button deleteButton = new Button("Delete");
                    deleteButton.addClickHandler(new DeleteButtonClickHandler(i - 1));
                    this.table.setWidget(i + 2, line.length, deleteButton);

                    Button updateButton = new Button("Update");
                    updateButton.addClickHandler(new UpdateButtonClickHandler(i - 1, line));
                    this.table.setWidget(i + 2, line.length + 1, updateButton);
                }
                i++;
            }
        }
    }

    private class GetEditorsAsyncCallBack implements AsyncCallback<List<Map<String, String[]>>> {


        private int row;
        private FlexTable table;
        private String[] updatingLine = null;

        public GetEditorsAsyncCallBack(FlexTable table) {
            this.table = table;
        }

        public GetEditorsAsyncCallBack(FlexTable table, int row, String[] updatingLine) {
            this.updatingLine = updatingLine;
            this.table = table;
            this.row = row;
        }

        @Override
        public void onFailure(Throwable caught) {
            caught.printStackTrace();
        }

        @Override
        public void onSuccess(List<Map<String, String[]>> result) {
            table.removeAllRows();
            int row = 0;
            for (String header : result.get(0).get("fields")) {
                table.setText(row, 0, header);
                EditorsFactory ef = (updatingLine != null) ? new EditorsFactory(updatingLine[row]) : new EditorsFactory();
                table.setWidget(row, 1, ef.getEditor(result.get(row + 1).get("type")[0]));
                row++;
            }
        }
    }

    private class DeleteButtonClickHandler implements ClickHandler {

        private int lineToDelete;

        public DeleteButtonClickHandler(int lineToDelete) {
            this.lineToDelete = lineToDelete;
        }

        @Override
        public void onClick(ClickEvent event) {
            try {
                crudService.App.getInstance().deleteData(currentTableName, lineToDelete, new ViewDataAsyncCallBack(mainTable));
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }
    }

    private class InsertButtonClickHandler implements ClickHandler {

        @Override
        public void onClick(ClickEvent event) {
            String[] lines = new String[mainTable.getRowCount()];
            EditorsFactory ef = new EditorsFactory();
            for (int i = 0; i < mainTable.getRowCount(); i++) {
                lines[i] = ef.getContent(mainTable.getWidget(i, 1));
            }
            try {
                crudService.App.getInstance().insertData(currentTableName, lines, new ViewDataAsyncCallBack(mainTable));
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    private class UpdateButtonClickHandler implements ClickHandler {
        private final int lineToUpdate;
        private final String[] updatingLine;

        public UpdateButtonClickHandler(int lineToUpdate, String[] updatingLine) {
            this.lineToUpdate = lineToUpdate;
            this.updatingLine = updatingLine;
        }

        @Override
        public void onClick(ClickEvent event) {
            subMainPanel.clear();

            setTableHeaderText("Update the entry");
            try {
                crudService.App.getInstance().getEditors(currentTableName, new GetEditorsAsyncCallBack(mainTable, lineToUpdate, updatingLine));
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            Button updateEntryButton = new Button("Update");

            updateEntryButton.addClickHandler(new UpdateEntryButtonClickHandler(this.lineToUpdate));

            subMainPanel.add(tableHeader);
            subMainPanel.add(mainTable);
            subMainPanel.add(updateEntryButton);
        }
    }

    private class AddEntryClickHandler implements ClickHandler {

        @Override
        public void onClick(ClickEvent event) {
            subMainPanel.clear();
            setTableHeaderText("Add the entry");
            try {
                crudService.App.getInstance().getEditors(currentTableName, new GetEditorsAsyncCallBack(mainTable));
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            Button insertButton = new Button("Add to the table");

            insertButton.addClickHandler(new InsertButtonClickHandler());

            subMainPanel.add(tableHeader);
            subMainPanel.add(mainTable);
            subMainPanel.add(insertButton);
        }
    }

    private class UpdateEntryButtonClickHandler implements ClickHandler {
        private int lineToUpdate;

        public UpdateEntryButtonClickHandler(int lineToUpdate) {
            this.lineToUpdate = lineToUpdate;
        }

        @Override
        public void onClick(ClickEvent event) {
            String[] lines = new String[mainTable.getRowCount()];
            EditorsFactory ef = new EditorsFactory();
            for (int i = 0; i < mainTable.getRowCount(); i++) {
                lines[i] = ef.getContent(mainTable.getWidget(i, 1));
            }
            try {
                crudService.App.getInstance().updateData(currentTableName, this.lineToUpdate, lines, new ViewDataAsyncCallBack(mainTable));
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    private class EditorsFactory {
        private String editorValue;

        public EditorsFactory() {
            this.editorValue = null;
        }

        public EditorsFactory(String value) {
            this.editorValue = value;
        }

        public Widget getEditor(String type) {
            Widget result = new Widget();

            if (type.equals("TextEditor")) {
                TextBox tb = new TextBox();
                if (this.editorValue != null) {
                    tb.setText(editorValue);
                }
                result = tb;
            }
            if (type.equals("DateEditor")) {
                DateBox db = new DateBox();
                DateBox.DefaultFormat df = new DateBox.DefaultFormat(DateTimeFormat.getFormat(PATTERN));
                db.setFormat(df);
                if (this.editorValue == null) {
                    db.setValue(new Date(System.currentTimeMillis()));
                } else {
                    DateTimeFormat dtf = DateTimeFormat.getFormat(PATTERN);
                    db.setValue(dtf.parse(this.editorValue));
                }
                result = db;

            }

            return result;
        }

        public String getContent(Widget widget) {
            String result = null;
            if (widget instanceof TextBox) {
                result = ((TextBox) widget).getText();
            }
            if (widget instanceof DateBox) {
                DateTimeFormat df = DateTimeFormat.getFormat(PATTERN);
                result = df.format(((DateBox) widget).getValue());
            }
            return result;
        }
    }
}
