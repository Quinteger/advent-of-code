package dev.quinteger.aoc;

import java.time.YearMonth;

public class SettingsBuilder {
    private int year = YearMonth.now().minusMonths(11).getYear();
    private int day;
    private String user = "quinteger";

    public SettingsBuilder setYear(int year) {
        this.year = year;
        return this;
    }

    public SettingsBuilder setDay(int day) {
        this.day = day;
        return this;
    }

    public SettingsBuilder setUser(String user) {
        this.user = user;
        return this;
    }

    public Settings build() {
        return new Settings(year, day, user);
    }
}
