package com.example.rmi.component.column;

import com.example.rmi.component.Column;

public class TimeColumn extends Column {

    public TimeColumn(String name) {
        super(name);
        this.type = ColumnType.TIME.name();
    }

    @Override
    public boolean validate(String data) {
        if (data == null || !data.matches("\\d{2}:\\d{2}")) {
            return false;
        }

        String[] parts = data.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);

        return hour >= 0 && hour <= 23 && minute >= 0 && minute <= 59;
    }
}