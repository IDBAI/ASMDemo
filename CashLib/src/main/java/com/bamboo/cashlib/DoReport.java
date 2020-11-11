package com.bamboo.cashlib;

public class DoReport {
    static Printer printer;


    public static void report(String text) {
        printer.print(text);
    }
}