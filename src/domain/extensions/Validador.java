package domain.extensions;

public class Validador {
    private static final int[] PESOS_CPF = {11, 10, 9, 8, 7, 6, 5, 4, 3, 2};
    private static final int[] PESOS_CNPJ = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

    public static boolean verificarCpf(String documento) {
        var cpf = documento.replaceAll("\\D", "");

        if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) return false;

        return validarNumeros(cpf, 9, 10, PESOS_CPF);
    }

    public static boolean verificarCnpj(String documento) {
        var cnpj = documento.replaceAll("\\D", "");

        if (cnpj.length() != 14 || cnpj.matches("(\\d)\\1{13}")) return false;

        return validarNumeros(cnpj, 12, 13, PESOS_CNPJ);
    }

    private static boolean validarNumeros(String documento, int primeiroDigito, int ultimoDigito, int[] pesos) {
        int somaPrimeiroDigito = 0, somaSegundoDigito = 0;
        for (int i = 0; i < primeiroDigito; i++) {
            int num = documento.charAt(i) - '0';
            somaPrimeiroDigito += num * pesos[i + 1];
            somaSegundoDigito += num * pesos[i];
        }

        var primeiroDigitoValidado = validarDigito(somaPrimeiroDigito);

        somaSegundoDigito += primeiroDigitoValidado * pesos[primeiroDigito];
        var segundoDigitoValidado = validarDigito(somaSegundoDigito);

        return primeiroDigitoValidado == documento.charAt(primeiroDigito) - '0' &&
                segundoDigitoValidado == documento.charAt(ultimoDigito) - '0';
    }

    private static int validarDigito(int soma) {
        int digito = 11 - (soma % 11);

        if (digito >= 10) digito = 0;

        return digito;
    }
}
