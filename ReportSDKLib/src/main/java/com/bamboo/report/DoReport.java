package com.bamboo.report;

public class DoReport {
    static Printer printer;


    public static void report(String text) {
        printer.print(text);
    }
}