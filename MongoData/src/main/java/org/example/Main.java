package org.example;

import org.example.controller.MenuController;
import org.example.controller.NeoController;

public class Main {
    public static void main(String[] args){
        new MenuController().run();
        System.out.println("END PROGRAM");
    }
}