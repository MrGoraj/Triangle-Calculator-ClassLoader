package pl.com.rbinternational.controller;

import pl.com.rbinternational.*;

@Calculator
public class TriangleController {

    private final Terminal terminal = new Terminal();
    private final TriangleCalculator calculator = new TriangleCalculator();

    /**
     * Calculator starts
     */
    public void start() {
        try {
            int a = terminal.inputA();
            int h = terminal.inputH();
            if (a > 0 && h > 0) {
                int result = calculator.calcTriangleArea(a, h);
                System.out.println("Pole trójkąta to: " + result);
            }
            else {
                System.out.println("Dane trójkąta mogą się składać wyłącznie z liczb dodatnich całkowitych!");
            }
        } catch (Exception e) {
            System.out.println("Dane trójkąta mogą się składać wyłącznie z liczb dodatnich całkowitych!");
        }
    }
}