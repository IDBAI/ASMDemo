//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.bamboo.report;

public class DoReport {
    static Printer printer;

    public DoReport() {
    }

    public static void report(String text) {
        if (printer == null) {
            printer = new Printer();
        }
        printer.print(text);
    }
}
