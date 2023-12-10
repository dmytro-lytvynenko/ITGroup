package com.example.rmi.controller;

import com.example.rmi.component.Table;
import com.example.rmi.component.TableData;
import com.example.rmi.component.column.ColumnType;
import com.example.rmi.request.ColumnRequest;
import com.example.rmi.request.EditCellRequest;
import com.example.rmi.request.TableRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import static com.example.rmi.RestApplication.remoteDB;

@Controller
@RequestMapping("/lab")
public class DatabaseController {

    @GetMapping("/tables")
    @Operation(summary = "Get list of all tables", description = "Returns a list of all tables from the database")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of all tables",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = TableData.class))))
    public ResponseEntity<List<TableData>> getAllTables() throws RemoteException {
        List<TableData> tablesData = remoteDB.getTablesData();
        return ResponseEntity.ok(tablesData);
    }

    @GetMapping("/viewTable")
    @Operation(summary = "Get details of a specific table", description = "Returns details of a table including columns and rows for a given table index")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved details of the specified table",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Table.class)))
    @ApiResponse(responseCode = "404", description = "Table not found for the given index")
    public ResponseEntity<?> viewTable(@Valid @Parameter(description = "Index of the table to retrieve") int tableIndex) throws RemoteException {
        // Assuming Table is a custom DTO that encapsulates all the details of a table
        Table tableDetails = new Table(remoteDB.getTablesData().get(tableIndex).name);
        for (int i = 0; i < remoteDB.getColumns(tableIndex).size(); i++) {
            tableDetails.addColumn(remoteDB.getColumns(tableIndex).get(i));
        }
        for (int i = 0; i < remoteDB.getRows(tableIndex).size(); i++) {
            tableDetails.addRow(remoteDB.getRows(tableIndex).get(i));
        }

        return ResponseEntity.ok(tableDetails);
    }


    @PostMapping("/addTable")
    @Operation(summary = "Add a new table", description = "Creates a new table with the given name and returns the location of the newly created table")
    @ApiResponse(responseCode = "302", description = "Table successfully created and redirect to the table view",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Table.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input for table name")
    public ResponseEntity<?> addTable(@Valid @RequestBody TableRequest tableRequest) throws RemoteException {
        remoteDB.createTable(tableRequest.getName());
        Table tableDetails = new Table(remoteDB.getTablesData().get(remoteDB.getTablesData().size()-1).name);
        return ResponseEntity.ok(tableDetails);
    }


    @PostMapping("/addColumn")
    @Operation(summary = "Add a new column", description = "Adds a new column to the specified table")
    @ApiResponse(responseCode = "200", description = "Column successfully added",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Table.class)))
    public ResponseEntity<?> addColumn(@Valid @RequestBody ColumnRequest columnRequest) throws RemoteException {
        remoteDB.addColumn(columnRequest.getTableIndex(), columnRequest.getName(), columnRequest.getColumnType(), columnRequest.getMin(), columnRequest.getMax());

        Table tableDetails = new Table(remoteDB.getTablesData().get(columnRequest.getTableIndex()).name);
        for (int i = 0; i < remoteDB.getColumns(columnRequest.getTableIndex()).size(); i++) {
            tableDetails.addColumn(remoteDB.getColumns(columnRequest.getTableIndex()).get(i));
        }
        for (int i = 0; i < remoteDB.getRows(columnRequest.getTableIndex()).size(); i++) {
            tableDetails.addRow(remoteDB.getRows(columnRequest.getTableIndex()).get(i));
        }
        return ResponseEntity.ok(tableDetails);
    }


    @PostMapping("/addRow")
    @Operation(summary = "Add a new row", description = "Adds a new row to the specified table")
    @ApiResponse(responseCode = "200", description = "Row added successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Table.class)))
    public ResponseEntity<?> addRow(@Valid int tableIndex) throws RemoteException {
        remoteDB.addRow(tableIndex);
        Table tableDetails = new Table(remoteDB.getTablesData().get(tableIndex).name);
        for (int i = 0; i < remoteDB.getColumns(tableIndex).size(); i++) {
            tableDetails.addColumn(remoteDB.getColumns(tableIndex).get(i));
        }
        for (int i = 0; i < remoteDB.getRows(tableIndex).size(); i++) {
            tableDetails.addRow(remoteDB.getRows(tableIndex).get(i));
        }
        return ResponseEntity.ok(tableDetails);
    }


    @Transactional
    @DeleteMapping("/deleteRow")
    @Operation(summary = "Delete a row", description = "Deletes a row from the specified table")
    @ApiResponse(responseCode = "200", description = "Row deleted successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Table.class)))
    public ResponseEntity<?> deleteRow(@Valid int tableIndex, @Valid int rowIndex) throws RemoteException {
        System.out.println(remoteDB.deleteRow(tableIndex,rowIndex));
        Table tableDetails = new Table(remoteDB.getTablesData().get(tableIndex).name);
        for (int i = 0; i < remoteDB.getColumns(tableIndex).size(); i++) {
            tableDetails.addColumn(remoteDB.getColumns(tableIndex).get(i));
        }
        for (int i = 0; i < remoteDB.getRows(tableIndex).size(); i++) {
            tableDetails.addRow(remoteDB.getRows(tableIndex).get(i));
        }
        return ResponseEntity.ok(tableDetails);
    }

    @Transactional
    @DeleteMapping("/deleteColumn")
    @Operation(summary = "Delete a column", description = "Deletes a column from the specified table")
    @ApiResponse(responseCode = "200", description = "Column deleted successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Table.class)))
    public ResponseEntity<?> deleteColumn(@Valid int tableIndex, @Valid int columnIndex) throws RemoteException {
        System.out.println(remoteDB.deleteColumn(tableIndex,columnIndex));
        Table tableDetails = new Table(remoteDB.getTablesData().get(tableIndex).name);
        for (int i = 0; i < remoteDB.getColumns(tableIndex).size(); i++) {
            tableDetails.addColumn(remoteDB.getColumns(tableIndex).get(i));
        }
        for (int i = 0; i < remoteDB.getRows(tableIndex).size(); i++) {
            tableDetails.addRow(remoteDB.getRows(tableIndex).get(i));
        }
        return ResponseEntity.ok(tableDetails);
    }

    @Transactional
    @DeleteMapping("/deleteTable")
    @Operation(summary = "Delete a table", description = "Deletes the specified table")
    @ApiResponse(responseCode = "200", description = "Table deleted successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = List.class)))
    public ResponseEntity<?> deleteTable(@Valid int tableIndex) throws RemoteException {
        System.out.println(remoteDB.deleteTable(tableIndex));
        List<TableData> tablesData = remoteDB.getTablesData();
        return ResponseEntity.ok(tablesData);
    }


    @PostMapping("/editCell")
    @Operation(summary = "Edit a cell", description = "Edits the value of a specified cell in a table")
    @ApiResponse(responseCode = "200", description = "Cell edited successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Table.class)))
    public ResponseEntity<?> editCell(@Valid @RequestBody EditCellRequest editCellRequest) throws RemoteException {
        if (editCellRequest.getNewValue() != null && !editCellRequest.getNewValue().trim().isEmpty()) {
            // Perform the edit operation
            remoteDB.editCell(editCellRequest.getTableIndex(), editCellRequest.getRowIndex(), editCellRequest.getColumnIndex(), editCellRequest.getNewValue());
        }
        Table tableDetails = new Table(remoteDB.getTablesData().get(editCellRequest.getTableIndex()).name);
        for (int i = 0; i < remoteDB.getColumns(editCellRequest.getTableIndex()).size(); i++) {
            tableDetails.addColumn(remoteDB.getColumns(editCellRequest.getTableIndex()).get(i));
        }
        for (int i = 0; i < remoteDB.getRows(editCellRequest.getTableIndex()).size(); i++) {
            tableDetails.addRow(remoteDB.getRows(editCellRequest.getTableIndex()).get(i));
        }
        return ResponseEntity.ok(tableDetails);
    }

    @PostMapping("/tablesMultiply")
    @Operation(summary = "Multiply Tables", description = "Finds multiplication of the specified tables")
    @ApiResponse(responseCode = "200", description = "Tables multiplication found successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Table.class)))
    public ResponseEntity<?> tablesIntersection(@Valid int tableIndex1,@Valid int tableIndex2) throws RemoteException {
        remoteDB.tablesMultiply(tableIndex1,tableIndex2);
        Table tableDetails = new Table(remoteDB.getTablesData().get(remoteDB.getTablesData().size()-1).name);
        int tableIndex = remoteDB.getTablesData().size()-1;
        for (int i = 0; i < remoteDB.getColumns(tableIndex).size(); i++) {
            tableDetails.addColumn(remoteDB.getColumns(tableIndex).get(i));
        }
        for (int i = 0; i < remoteDB.getRows(tableIndex).size(); i++) {
            tableDetails.addRow(remoteDB.getRows(tableIndex).get(i));
        }
        return ResponseEntity.ok(tableDetails);
    }

}
