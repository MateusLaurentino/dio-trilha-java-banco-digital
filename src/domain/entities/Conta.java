package domain.entities;

import domain.common.Constantes;
import domain.common.Mensagens;
import domain.enums.ETipoConta;

import java.util.Scanner;
import java.util.function.Predicate;

public class Conta {
    private static int autoIncremento = 1;
    private static byte digitoConta = 0;

    private final String agencia = "1672";
    private final Cliente cliente;
    private final String numero;
    private String senha;
    private double saldo;
    private final ETipoConta tipo;

    public Conta(ETipoConta tipo, String senha, Cliente cliente) {
        this.cliente = cliente;
        this.numero = gerarNumero();
        this.senha = senha;
        this.saldo = 0D;
        this.tipo = tipo;
    }

    public void alterarSenha(Scanner scanner) {
        System.out.println(Mensagens.LIMPAR_CONSOLE);
        System.out.println("Informe a nova senha:");
        this.senha = scanner.nextLine();
        System.out.println("Senha alterada com sucesso.");
    }

    public String buscarAgencia() {
        return this.agencia;
    }

    public String buscarNome() {
        return cliente.buscarNome();
    }

    public String buscarNumero() {
        return this.numero;
    }

    public ETipoConta buscarTipo() {
        return this.tipo;
    }

    public void buscarSaldo() {
        System.out.printf("Saldo atual: %.2f\n\n", this.saldo);
    }

    public void depositar(double saldo) {
        if (validarSaldo(saldo, false)) {
            this.saldo += saldo;
        }
    }

    public void sacar(double saldo) {
        if (validarSaldo(saldo, true)) {
            this.saldo -= saldo;
        }

    }

    public void transferir(Conta conta, double saldo) {
        if (validarSaldo(saldo, true)) {
            this.sacar(saldo);
            conta.depositar(saldo);
        }
    }

    public boolean verificarNome(String nome) {
        return this.cliente.buscarNome().equalsIgnoreCase(nome);
    }

    public void imprimirDadosConta() {
        System.out.printf("Titular: %s\n", this.buscarNome());
        System.out.printf("Agencia: %s\n", this.buscarAgencia());
        System.out.printf("CONTA-%s: %s\n\n", this.buscarTipo(), this.buscarNumero());
    }

    public static Predicate<Conta> buscarConta(String agencia, String numeroConta) {
        return conta -> agencia.equals(conta.agencia) &&
                numeroConta.equals(conta.numero);
    }

    public static Predicate<Conta> verificarConta(String agencia, String numeroConta, String senha) {
        return conta -> agencia.equals(conta.agencia) &&
                numeroConta.equals(conta.numero) &&
                senha.equals(conta.senha);
    }

    private boolean validarSaldo(double saldo, boolean validarSaldoConta) {
        if (saldo <= 0) {
            System.out.println(Mensagens.VALOR_INCORRETO);
            return false;
        }

        if (validarSaldoConta && saldo > this.saldo) {
            System.out.println(Mensagens.SALDO_INVALIDO);
            return false;
        }

        return true;
    }

    private static String gerarNumero() {
        if (autoIncremento == Integer.MAX_VALUE)
            autoIncremento = 1;

        if (autoIncremento == 1)
            digitoConta++;

        return String.format(Constantes.MASCARA_NUMERO_CONTA, autoIncremento++, digitoConta);
    }

}