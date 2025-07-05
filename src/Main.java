import domain.common.Constantes;
import domain.common.Mensagens;
import domain.entities.Cliente;
import domain.entities.Conta;
import domain.enums.ETipoConta;
import domain.extensions.Validador;
import lombok.experimental.ExtensionMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

@ExtensionMethod({Validador.class})
public class Main {
    private static final String MASCARA = "%s %s";
    private static final Random RANDOM = new Random();

    private static final List<Conta> CONTAS = new ArrayList<>();
    private static final Scanner ENTRADA = new Scanner(System.in);

    public static void main(String[] args) {
        mockContas();
        while (true) {
            System.out.println(Mensagens.LIMPAR_CONSOLE);
            System.out.println("Seja bem vindo\n");

            System.out.println("1 - Acessar");
            System.out.println("2 - Criar");
            System.out.println("3 - Sair");
            var operacao = selecionarOperacao();
            if (operacao == 3) break;

            System.out.println(Mensagens.LIMPAR_CONSOLE);

            switch (operacao) {
                case 1 -> acessarConta();
                case 2 -> cadastrar();
                default -> System.out.println(Mensagens.ERRO_OPERACAO_INVALIDA + "\n");
            }
        }
    }

    private static int selecionarOperacao() {
        System.out.println(Mensagens.SELECIONE_OPERACAO);
        var operacao = ENTRADA.nextInt();
        ENTRADA.nextLine();
        return operacao;
    }

    private static void acessarConta() {
        var conta = entrar();
        if (conta == null) {
            System.out.println(Mensagens.ERRO_ACESSAR_CONTA);
            System.out.println(Mensagens.ERRO_OPERACAO);
            return;
        }

        while (true) {
            System.out.println(Mensagens.LIMPAR_CONSOLE);
            conta.imprimirDadosConta();

            System.out.println("1 - Saldo");
            System.out.println("2 - Deposito");
            System.out.println("3 - Saque");
            System.out.println("4 - Transferência");
            System.out.println("5 - Alterar senha");
            System.out.println("6 - Sair");
            var operacao = selecionarOperacao();

            if (operacao == 6)
                break;

            switch (operacao) {
                case 1 -> conta.buscarSaldo();
                case 2 -> conta.depositar(informarSaldo("depositar"));
                case 3 -> conta.sacar(informarSaldo("sacar"));
                case 4 -> {
                    var contaDestinatario = buscarConta();
                    if (contaDestinatario == null)
                        continue;

                    conta.transferir(contaDestinatario, informarSaldo("transferir"));
                }
                case 5 -> conta.alterarSenha(ENTRADA);
                default -> System.out.println(Mensagens.ERRO_OPERACAO_INVALIDA + "\n");
            }
        }
    }

    private static double informarSaldo(String operacao) {
        System.out.println(Mensagens.LIMPAR_CONSOLE);
        System.out.printf("Valor a %s\n", operacao);
        var valor = ENTRADA.nextDouble();
        ENTRADA.nextLine();
        return valor;
    }

    private static Conta buscarConta() {
        while (true) {
            System.out.println(Mensagens.LIMPAR_CONSOLE);

            System.out.println(Mensagens.INFORME_AGENCIA);
            var agencia = ENTRADA.nextLine();

            System.out.println(Mensagens.INFORME_CONTA);
            var numeroConta = ENTRADA.nextShort();
            ENTRADA.nextLine();

            System.out.println(Mensagens.INFORME_CONTA_DIGITO);
            var digitoConta = ENTRADA.nextShort();
            ENTRADA.nextLine();

            var contaCompleta = String.format(Constantes.MASCARA_NUMERO_CONTA, numeroConta, digitoConta);

            var conta = CONTAS.stream()
                    .filter(Conta.buscarConta(agencia, contaCompleta))
                    .findFirst()
                    .orElse(null);

            if (conta == null) {
                System.out.println("Conta não encontrada\n");
                System.out.println("1 - Buscar novamente");
            } else {
                System.out.println("Dados destinatário:");
                conta.imprimirDadosConta();
                System.out.println("1 - Confirmar");
            }

            System.out.println("2 - Sair");
            var operacao = selecionarOperacao();
            if (operacao == 2) return null;

            if (operacao != 1) {
                System.out.println(Mensagens.ERRO_OPERACAO_INVALIDA + "\n");
                System.out.println(Mensagens.ERRO_OPERACAO);
                return null;
            }

            if (conta == null) continue;

            return conta;
        }
    }

    private static Conta entrar() {
        System.out.println(Mensagens.INFORME_AGENCIA);
        var agencia = ENTRADA.nextLine();

        System.out.println(Mensagens.INFORME_CONTA);
        var numeroConta = ENTRADA.nextShort();
        ENTRADA.nextLine();

        System.out.println(Mensagens.INFORME_CONTA_DIGITO);
        var digitoConta = ENTRADA.nextShort();
        ENTRADA.nextLine();

        var contaCompleta = String.format(Constantes.MASCARA_NUMERO_CONTA, numeroConta, digitoConta);

        System.out.println(Mensagens.INFORME_SENHA);
        var senha = ENTRADA.nextLine();

        return CONTAS.stream()
                .filter(Conta.verificarConta(agencia, contaCompleta, senha))
                .findFirst()
                .orElse(null);
    }

    private static void cadastrar() {
        System.out.println(Mensagens.LIMPAR_CONSOLE);
        System.out.println("Informe seu nome completo");
        var nome = ENTRADA.nextLine();

        var valido = false;
        String documento = "";
        while (!valido) {
            System.out.println("Informe CPF ou CNPJ");
            documento = ENTRADA.nextLine();

            valido = documento.verificarCpf() || documento.verificarCnpj();

            if (!valido) {
                System.out.println(Mensagens.LIMPAR_CONSOLE);
                System.out.println(Mensagens.DOCUMENTO_INVALIDO + "\n");
            }
        }

        System.out.println(Mensagens.LIMPAR_CONSOLE);

        List<ETipoConta> tipoContas = new ArrayList<>();
        while (tipoContas.isEmpty()) {
            System.out.println("Informe o tipo de conta");
            System.out.printf("1 - %s \n", ETipoConta.CORRENTE);
            System.out.printf("2 - %s \n", ETipoConta.POUPANCA);
            System.out.printf("3 - %s e %s \n", ETipoConta.CORRENTE, ETipoConta.POUPANCA);
            var operacao = selecionarOperacao();

            if (operacao <= 0 || operacao > 3) {
                System.out.println(Mensagens.LIMPAR_CONSOLE);
                System.out.println(Mensagens.ERRO_OPERACAO_INVALIDA);
                continue;
            }

            tipoContas = switch (operacao) {
                case 1 -> List.of(ETipoConta.CORRENTE);
                case 2 -> List.of(ETipoConta.POUPANCA);
                default -> List.of(ETipoConta.CORRENTE, ETipoConta.POUPANCA);
            };
        }

        System.out.println(Mensagens.LIMPAR_CONSOLE);

        System.out.println("Informe uma senha:");
        var senha = ENTRADA.nextLine();

        gerarContas(tipoContas, nome, documento, senha, true);
        System.out.println(Mensagens.LIMPAR_CONSOLE);
        System.out.println(Mensagens.SUCESSO_CADASTRO_CONTA);
    }

    //Mocks
    private static void mockContas() {
        while (CONTAS.size() < 100) {
            var nome = gerarNomes();
            if (CONTAS.stream().anyMatch(f -> f.verificarNome(nome)))
                continue;

            var tipoContas = switch (RANDOM.nextInt(3)) {
                case 0 -> List.of(ETipoConta.CORRENTE);
                case 1 -> List.of(ETipoConta.POUPANCA);
                case 2 -> List.of(ETipoConta.CORRENTE, ETipoConta.POUPANCA);
                default -> new ArrayList<ETipoConta>();
            };

            gerarContas(tipoContas, nome, Constantes.DOCUMENTOS.get(CONTAS.size()), Constantes.SENHA_MOCK, false);
        }
    }

    private static String gerarNomes() {
        String nome = Constantes.NOMES.get(RANDOM.nextInt(Constantes.NOMES.size()));
        String sobrenome = Constantes.SOBRENOMES.get(RANDOM.nextInt(Constantes.SOBRENOMES.size()));
        return String.format(MASCARA, nome, sobrenome);
    }

    private static void gerarContas(List<ETipoConta> tipoContas, String nome,
                                    String documento, String senha, boolean informarConta) {
        if (informarConta)
            System.out.println("Conta(s) criada(s):");

        for (var tipoConta : tipoContas) {
            var cliente = new Cliente(nome, documento);
            var conta = new Conta(tipoConta, senha, cliente);
            CONTAS.add(conta);

            if (informarConta) {
                conta.imprimirDadosConta();
            }
        }
    }

}