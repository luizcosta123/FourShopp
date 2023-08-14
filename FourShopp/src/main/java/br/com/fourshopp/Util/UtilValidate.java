package br.com.fourshopp.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilValidate {

    private static Scanner scanner;

    public static int integerValidate(Scanner scanner) {
        int opcao = 0;

            try {
                opcao = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Digite um valor inteiro!");
                scanner.next();
                integerValidate(scanner);
            }

        return opcao;
    }

    public static double doubleValidate(Scanner scanner) {
        double opcao = 0;

        try {
            opcao = scanner.nextDouble();
        } catch (InputMismatchException e) {
            System.out.println("Digite um valor double!");
            scanner.next();
            doubleValidate(scanner);
        }

        return opcao;
    }

    public static Long longValidate(Scanner scanner) {
        Long opcao = 0L;

        try {
            opcao = scanner.nextLong();
        } catch (InputMismatchException e) {
            System.out.println("Digite um valor Long!");
            scanner.next();
            longValidate(scanner);
        }

        return opcao;
    }

    public static String passwordValidate(String password, Scanner scanner) {
        while (true) {
            if(password.length() < 8) {
                System.out.println("\n============================================================ \n" +
                        "Digite uma senha maior ou igual a 8 caracteres! \n" +
                        "============================================================ \n");
                System.out.println("Insira sua senha: ");
                password = scanner.next();
                continue;
            }
            return password;
        }
    }

    // Método novo!
    public static boolean cpfValidate(String cpf) {

        String pattern = "([0-9]{3}[\\.][0-9]{3}[\\.][0-9]{3}[\\-][0-9]{2})";
        Pattern regex = Pattern.compile(pattern);

        Matcher matcher = regex.matcher(cpf);

        if(!matcher.matches()) {
            return false;
        }

        String S1, S2, S3, S4, S5, S6, S7, S8, S9, check = "";
        int N1, N2, N3, N4, N5, N6, N7, N8, N9, verify1, verify2;

        S1 = cpf.substring(0, 1);
        N1 = Integer.valueOf(S1);
        S2 = cpf.substring(1, 2);
        N2 = Integer.parseInt(S2);
        S3 = cpf.substring(2, 3);
        N3 = Integer.valueOf(S3);
        S4 = cpf.substring(4, 5);
        N4 = Integer.parseInt(S4);
        S5 = cpf.substring(5, 6);
        N5 = Integer.valueOf(S5);
        S6 = cpf.substring(6, 7);
        N6 = Integer.parseInt(S6);
        S7 = cpf.substring(8, 9);
        N7 = Integer.parseInt(S7);
        S8 = cpf.substring(9, 10);
        N8 = Integer.valueOf(S8);
        S9 = cpf.substring(10, 11);
        N9 = Integer.parseInt(S9);

        verify1 = (N1 * 10 + N2 * 9 + N3 * 8 + N4 * 7 + N5 * 6 + N6 * 5 + N7 * 4 + N8 * 3 + N9 * 2);

        if ((verify1 % 11) < 2)
            verify1 = 0;
        else
            verify1 = 11 - (verify1 % 11);

        verify2 = (N1 * 11 + N2 * 10 + N3 * 9 + N4 * 8 + N5 * 7 + N6 * 6 + N7 * 5 + N8 * 4 + N9 * 3 + verify1 * 2);
        if ((verify2 % 11) < 2)
            verify2 = 0;
        else
            verify2 = 11 - (verify2 % 11);
        check = (S1 + S2 + S3 + "." + S4 + S5 + S6 + "." + S7 + S8 + S9 + "-" + verify1 + "" + verify2);

        if (check.equals(cpf))
            return true;
        else
            return false;
    }

    public static boolean dateValidate(String data) {
        String pattern =  "(^(((0[1-9]|1[0-9]|2[0-8])[\\/](0[1-9]|1[012]))|((29|30|31)[\\/](0[13578]|1[02]))|((29|30)[\\/](0" +
                "[4,6,9]|11)))[\\/](19|[2-9][0-9])\\d\\d$)|(^29[\\/]02[\\/](19|[2-9][0-9])(00|04|08|12|16|20|24|28|32" +
                "|36|40|44|48|52|56|60|64|68|72|76|80|84|88|92|96)$)";
        Pattern regex = Pattern.compile(pattern);

        Matcher matcher = regex.matcher(data);

        if(!matcher.matches()) {
            return false;
        }

        return true;
    }

    public static Date parseStringToDate(String data) {

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        try {
            Date formatDate = format.parse(data);
            return formatDate;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

}
