package pl.com.rbinternational;

import java.util.Scanner;

public class Terminal {

    private final Scanner scanner = new Scanner(System.in);

    /**
     *
     * @return user-specified side of the triangle
     */
    public int inputA() {
        System.out.println("Podaj podstawę: ");
        return scanner.nextInt();
    }

    /**
     *
     * @return user-specified height of the triangle
     */
    public int inputH() {
        System.out.println("Podaj wysokość: ");
        return scanner.nextInt();
    }
}