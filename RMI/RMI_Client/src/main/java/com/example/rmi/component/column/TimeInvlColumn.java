package com.example.rmi.component.column;

import com.example.rmi.component.Column;

public class TimeInvlColumn extends Column {

    private String min;
    private String max;

    public TimeInvlColumn(String name, String min, String max) {
        super(name);
        this.type = ColumnType.TIMEINVL.name();
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean validate(String data) {
        if (data == null || !data.matches("\\d{2}:\\d{2}")) {
            return false;
        }

        String[] parts = data.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);

        return hour >= 0 && hour <= 23 && minute >= 0 && minute <= 59 && isWithinRange(data, min, max);
    }

    private boolean isWithinRange(String time, String minTime, String maxTime) {
        return time.compareTo(minTime) >= 0 && time.compareTo(maxTime) <= 0;
    }

    public boolean validateMinMax(String min, String max) {
        if (min == null || !min.matches("\\d{2}:\\d{2}") || max == null || !max.matches("\\d{2}:\\d{2}")) {
            return false;
        }

        int minHour = 0;
        int minMinute = 0;
        try {
            String[] minParts = min.split(":");
            minHour = Integer.parseInt(minParts[0]);
            minMinute = Integer.parseInt(minParts[1]);

            String[] maxParts = min.split(":");
            int maxHour = Integer.parseInt(maxParts[0]);
            int maxMinute = Integer.parseInt(maxParts[1]);

            return minHour >= 0 && minHour <= 23 && minMinute >= 0 && minMinute <= 59 && maxHour >= 0 && maxHour <= 23 && maxMinute >= 0 && maxMinute <= 59;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }
}
