package com.example.rmi;

import com.example.rmi.component.Column;
import com.example.rmi.component.Database;
import com.example.rmi.component.Row;
import com.example.rmi.component.Table;
import com.example.rmi.component.column.*;
import com.example.rmi.io.SQLDatabaseExporter;
import com.example.rmi.io.SQLDatabaseImporter;

import java.sql.SQLException;

public class DatabaseManager {
    private static DatabaseManager instance;
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/db";
//    public static DBMS instanceCSW;

    private DatabaseManager(){
    }

    public static DatabaseManager getInstance(){
        if (instance == null){
            instance = new DatabaseManager();
//            database = new Database("DB");
            try {
                isImporting = true;
                SQLDatabaseImporter.importDatabase(JDBC_URL,USERNAME,PASSWORD);
                isImporting = false;
                System.out.println(database.tables.size());
            } catch (SQLException e){
                isImporting = false;
                throw new RuntimeException(e);
            }
//            System.out.println(database.tables.size());
        }
        return instance;
    }
    public static boolean isImporting;
    public static Database database;

    public void populateTable() {
        Table table = new Table("testTable" + database.tables.size());
        table.addColumn(new IntegerColumn("column1"));
        table.addColumn(new RealColumn("column2"));
        table.addColumn(new StringColumn("column3"));
        table.addColumn(new CharColumn("column4"));
        table.addColumn(new TimeColumn("column5"));
        table.addColumn(new TimeInvlColumn("column6", "12:00", "20:00"));
        Row row1 = new Row();
        row1.values.add("10");
        row1.values.add("10.0");
        row1.values.add("10");
        row1.values.add("1");
        row1.values.add("12:00");
        row1.values.add("15:00");
        table.addRow(row1);
        Row row2 = new Row();
        row2.values.add("15");
        row2.values.add("15.0");
        row2.values.add("15");
        row2.values.add("3");
        row2.values.add("12:00");
        row2.values.add("15:00");
        table.addRow(row2);
        database.addTable(table);
        SQLDatabaseExporter.exportDatabase(database,JDBC_URL,USERNAME,PASSWORD);
    }


    public void createDB(String name) {
        database = new Database(name);
    }

    public Boolean addTable(String name){
        if (name != null && !name.isEmpty()) {
            Table table = new Table(name);
            database.addTable(table);
            if (!isImporting) SQLDatabaseExporter.exportDatabase(database,JDBC_URL,USERNAME,PASSWORD);
            return true;
        }
        else {
            return false;
        }
    }

    public Boolean deleteTable(int tableIndex){

        if (tableIndex != -1) {
            database.deleteTable(tableIndex);
            SQLDatabaseExporter.exportDatabase(database,JDBC_URL,USERNAME,PASSWORD);
            return true;
        }
        else {
            return false;
        }
    }

    public Boolean addColumn(int tableIndex, String columnName, ColumnType columnType, String min, String max) {
        if (columnName != null && !columnName.isEmpty()) {
            if (tableIndex != -1) {

                switch (columnType) {
                    case INT -> {
                        Column columnInt = new IntegerColumn(columnName);
                        database.tables.get(tableIndex).addColumn(columnInt);
                    }
                    case REAL -> {
                        Column columnReal = new RealColumn(columnName);
                        database.tables.get(tableIndex).addColumn(columnReal);
                    }
                    case STRING -> {
                        Column columnStr = new StringColumn(columnName);
                        database.tables.get(tableIndex).addColumn(columnStr);
                    }
                    case CHAR -> {
                        Column columnChar = new CharColumn(columnName);
                        database.tables.get(tableIndex).addColumn(columnChar);
                    }
                    case TIME -> {
                        Column timeColumn = new TimeColumn(columnName);
                        database.tables.get(tableIndex).addColumn(timeColumn);
                    }
                    case TIMEINVL -> {
                        Column timeInvlColumn = new TimeInvlColumn(columnName,min,max);
                        database.tables.get(tableIndex).addColumn(timeInvlColumn);
                    }
                }
                for (Row row : database.tables.get(tableIndex).rows) {
                    row.values.add("");
                }
                if (!isImporting) SQLDatabaseExporter.exportDatabase(database,JDBC_URL,USERNAME,PASSWORD);
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    public Boolean deleteColumn(int tableIndex, int columnIndex/*, CustomTableModel tableModel*/){
        if (columnIndex != -1) {
//            tableModel.removeColumn(columnIndex);
            database.tables.get(tableIndex).deleteColumn(columnIndex);
            SQLDatabaseExporter.exportDatabase(database,JDBC_URL,USERNAME,PASSWORD);
            return true;
        } else {
            return false;
        }
    }

    public Boolean addRow(int tableIndex, Row row){
        if (tableIndex != -1) {
            for (int i = row.values.size(); i < database.tables.get(tableIndex).columns.size(); i++) {
                row.values.add("");
            }
            database.tables.get(tableIndex).addRow(row);
            System.out.println(row.values);
            System.out.println(database.tables.get(tableIndex).rows.size());
            if (!isImporting) SQLDatabaseExporter.exportDatabase(database,JDBC_URL,USERNAME,PASSWORD);
            return true;
        }
        else {
            return false;
        }
    }

    public Boolean deleteRow(int tableIndex, int rowIndex/*, CustomTableModel tableModel*/){
        if (rowIndex != -1) {
//            tableModel.removeRow(rowIndex);
            database.tables.get(tableIndex).deleteRow(rowIndex);
            SQLDatabaseExporter.exportDatabase(database,JDBC_URL,USERNAME,PASSWORD);
            return true;
        }
        else {
            return false;
        }
    }

    public Boolean updateCellValue(String value, int tableIndex, int columnIndex, int rowIndex/*, CustomTable table*/){
        if (database.tables.get(tableIndex).columns.get(columnIndex).validate(value)){
            database.tables.get(tableIndex).rows.get(rowIndex).setAt(columnIndex,value.trim());
            SQLDatabaseExporter.exportDatabase(database,JDBC_URL,USERNAME,PASSWORD);
            return true;
        }
        return false;
    }

    public boolean tablesMultiply(int tableIndex1, int tableIndex2){
        Table tempTable1 = new Table(database.tables.get(tableIndex1));
        Table tempTable2 = new Table(database.tables.get(tableIndex2));
        System.out.println(tempTable2.name + " " + tempTable1.name);
        addTable(tempTable1.name + "_and_" + tempTable2.name);
        for (int i = 0; i < tempTable1.columns.size(); i++) {
            if (ColumnType.valueOf(tempTable1.columns.get(i).getType()).equals(ColumnType.TIMEINVL)) {
                addColumn(database.tables.size() - 1, tempTable1.name + "_" + tempTable1.columns.get(i).getName(),
                        ColumnType.valueOf(tempTable1.columns.get(i).getType()),
                        ((TimeInvlColumn) tempTable1.columns.get(i)).getMin(),
                        ((TimeInvlColumn) tempTable1.columns.get(i)).getMax());
            }
            else {
                addColumn(database.tables.size() - 1, tempTable1.name + "_" +  tempTable1.columns.get(i).getName()
                        ,ColumnType.valueOf(tempTable1.columns.get(i).getType()),"","");
            }
        }
        for (int i = 0; i < tempTable2.columns.size(); i++) {
            if (ColumnType.valueOf(tempTable2.columns.get(i).getType()).equals(ColumnType.TIMEINVL)) {
                addColumn(database.tables.size() - 1, tempTable2.name + "_" + tempTable2.columns.get(i).getName(),
                        ColumnType.valueOf(tempTable2.columns.get(i).getType()),
                        ((TimeInvlColumn) tempTable2.columns.get(i)).getMin(),
                        ((TimeInvlColumn) tempTable2.columns.get(i)).getMax());
            }
            else {
                addColumn(database.tables.size() - 1, tempTable2.name + "_" + tempTable2.columns.get(i).getName()
                        ,ColumnType.valueOf(tempTable2.columns.get(i).getType()),"","");
            }
        }
        for (int i = 0; i < tempTable1.rows.size(); i++) {
            for (int i1 = 0; i1 < tempTable2.rows.size(); i1++) {
                Row row = new Row();
                for (int i2 = 0; i2 < tempTable1.rows.get(i).values.size(); i2++) {
                    row.values.add(tempTable1.rows.get(i).values.get(i2));
                }
                for (int i2 = 0; i2 < tempTable2.rows.get(i1).values.size(); i2++) {
                    row.values.add(tempTable2.rows.get(i1).values.get(i2));
                }
                addRow(database.tables.size() - 1,row);
            }
        }
        SQLDatabaseExporter.exportDatabase(database,JDBC_URL,USERNAME,PASSWORD);
        return true;
    }
}
