package br.com.fourshopp.Util;

import br.com.fourshopp.entities.*;
import br.com.fourshopp.repository.ProdutoRepository;
import br.com.fourshopp.service.ProdutoService;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.*;
import java.util.List;

import static br.com.fourshopp.Util.UtilValidate.*;

public class UtilMenu {

    private static double valorTotalCompra;

    private static Scanner scanner;

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private ProdutoRepository produtoRepository;

    public static int home(Scanner scanner) {
        System.out.println("====== BEM-VINDO AO FOURSHOPP ======");
        System.out.println("1- Sou cliente " +
                "\n2- Área do ADM" +
                "\n3- Seja um Cliente " +
                "\n4- Login funcionário" +
                "\n5- Encerrar ");
        int opcao = opcaoMenu(scanner, 1, 5);

        return opcao;
    }

    public static int opcaoMenu(Scanner scanner, int inicio, int fim) {
        System.out.println("Insira uma opção: ");
        int opcao = integerValidate(scanner);

        while(true) {
            if(opcao < inicio || opcao > fim) {
                System.out.println("Insira uma opção válida: ");
                opcao = integerValidate(scanner);
                continue;
            }
            break;
        }

        return opcao;
    }

    public static void listarChefesDeSecao(List<Funcionario> listaChefes) {
        for(int i = 0; i< listaChefes.size(); i++) {
            Funcionario chefe = listaChefes.get(i);
            System.out.println("Código: " + chefe.getId()+" - Nome: "+chefe.getNome()+" - CPF: "+ chefe.getCpf()+" - Setor: "+ chefe.getSetor());
            System.out.println("-----------------------------------------");
        }
    }

    public static void listarOperadores(List<Operador> listaOperador) {
        for(int i = 0; i< listaOperador.size(); i++) {
            Operador operador = listaOperador.get(i);
            System.out.println("Código: " + operador.getId()+" - Nome: "+operador.getNome()+" - CPF: "+ operador.getCpf());
            System.out.println("-----------------------------------------");
        }
    }

    // SOU CLIENTE
    public static Cliente menuCadastroCliente(Scanner scanner){

        System.out.println("Insira seu nome: ");
        String nome = scanner.nextLine();
        nome = scanner.nextLine();

        System.out.println("Insira seu email: ");
        String email = scanner.nextLine();

        System.out.println("Insira seu celular: ");
        String celular = scanner.nextLine();

        System.out.println("Insira sua password: ");
        String password = scanner.nextLine();

        password = passwordValidate(password, scanner);

        System.out.println("Insira seu cpf: ");
        String cpf = scanner.nextLine();

        while(true) {
            if(!cpfValidate(cpf)) {
                System.out.println("\n============================================================ \n" +
                        "Insira um cpf válido e no formato correto (xxx.xxx.xxx-xx)! \n" +
                        "============================================================ \n");

                System.out.println("Insira seu cpf: ");
                cpf = scanner.nextLine();
                continue;
            }
            break;
        }

        System.out.println("Insira sua rua: ");
        String rua = scanner.nextLine();

        System.out.println("Insira seu cidade: ");
        String cidade = scanner.nextLine();

        System.out.println("Insira seu bairro: ");
        String bairro = scanner.nextLine();

        System.out.println("Insira seu numero: ");
        int numero = scanner.nextInt();

        System.out.println("Insira sua data de nascimento (dd/MM/yyyy): ");
        String dataNascimento = scanner.nextLine();
        dataNascimento = scanner.nextLine();

        while(true) {
            if(!dateValidate(dataNascimento)) {
                System.out.println("\n============================================================ \n" +
                        "Insira a data no formato correto (xx/xx/xxxx)! \n" +
                        "============================================================ \n");

                System.out.println("Insira sua data de nascimento (dd/MM/yyyy): ");
                dataNascimento = scanner.nextLine();
                continue;
            }
            break;
        }

        Endereco endereco = new Endereco(rua, cidade, bairro, numero);
        Cliente cliente = new Cliente(nome, email, celular, password, cpf, endereco, parseStringToDate(dataNascimento)); // data errada!

        return cliente;

    }

    public static int menuSetor(Scanner scanner) {
        System.out.println("Digite a opção desejada: " +
                "\n1- MERCEARIA" +
                "\n2- BAZAR" +
                "\n3- ELETRÔNICOS");
        int opcao = opcaoMenu(scanner, 1, 3);
        return opcao;
    }

    public static void gerarCupomFiscal(Cliente cliente) throws IOException {
        List<Produto> produtos = cliente.getProdutoList();
        Document document = new Document(PageSize.A4);
        File file = new File("CupomFiscal_" + new Random().nextInt() + ".pdf");
        String absolutePath = file.getAbsolutePath();
        PdfWriter.getInstance(document, new FileOutputStream(absolutePath));
        document.open();

        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontTitle.setSize(18);

        Image image1 = Image.getInstance("src/main/java/br/com/fourshopp/service/fourshopp.png");
        image1.scaleAbsolute(140f, 140f);
        image1.setAlignment(Element.ALIGN_CENTER);

        Font fontParagraph = FontFactory.getFont(FontFactory.HELVETICA);
        fontParagraph.setSize(12);

        Font total = FontFactory.getFont(FontFactory.HELVETICA);
        total.setSize(12);
        total.setColor(Color.blue);

        Font header = FontFactory.getFont(FontFactory.HELVETICA);
        header.setSize(12);
        header.setFamily("bold");

        document.add(image1);

        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        ListItem item1 = new ListItem();
        produtos.forEach(produto -> {

            System.out.println(produto.toString());
            Chunk nome = new Chunk("\n" + produto.getNome()+" ("+produto.getQuantidade()+") \nPreço unidade : R$"+df.format(produto.getPreco() / produto.getQuantidade()));
            Phrase frase = new Phrase();
            frase.add(nome);

            Paragraph x = new Paragraph(frase);

            String preco = "............................................................................................................................R$ "
                           + df.format(produto.getPreco());
            Paragraph y = new Paragraph(preco);
            y.setAlignment(Paragraph.ALIGN_RIGHT);
            item1.add(x);
            item1.add(y);

            valorTotalCompra =  valorTotalCompra + produto.getPreco();
        });

        Double produtosMercearia = 0.0;

        for(int i = 0; i < produtos.size(); i++) {
            Produto produto = produtos.get(i);
            Double preco = produto.getPreco();

            if(produto.getSetor() == 1) {
                produtosMercearia += preco;
            }
        }

        if(produtosMercearia >= 500) {
            double desconto = produtosMercearia / 500;

            Math.floor(desconto);

            double descontoTotal = 0.10 * (500 * desconto);

            valorTotalCompra -= descontoTotal;
        }

        Paragraph paragraph = new Paragraph("\n\nTOTAL: R$" + df.format(valorTotalCompra), total);
        paragraph.setAlignment(Paragraph.ALIGN_RIGHT);

        document.add(item1);
        document.add(paragraph);

        document.close();
    }

    public static Funcionario cadastrarFuncionario(Scanner scanner) throws ParseException {

        System.out.println("Insira seu nome: ");
        String nome = scanner.nextLine();
        nome = scanner.nextLine();

        System.out.println("Insira seu email: ");
        String email = scanner.nextLine();

        System.out.println("Insira seu celular: ");
        String celular = scanner.nextLine();

        System.out.println("Insira sua password: ");
        String password = scanner.nextLine();

        password = passwordValidate(password, scanner);

        System.out.println("Insira seu cpf: ");
        String cpf = scanner.nextLine();

        while(true) {
            if(!cpfValidate(cpf)) {
                System.out.println("\n============================================================ \n" +
                        "Insira um cpf válido e no formato correto (xxx.xxx.xxx-xx)! \n" +
                        "============================================================ \n");

                System.out.println("Insira seu cpf: ");
                cpf = scanner.nextLine();
                continue;
            }
            break;
        }

        System.out.println("Insira sua rua: ");
        String rua = scanner.nextLine();

        System.out.println("Insira seu cidade: ");
        String cidade = scanner.nextLine();

        System.out.println("Insira seu bairro: ");
        String bairro = scanner.nextLine();

        System.out.println("Insira seu numero: ");
        int numero = integerValidate(scanner);

        System.out.println("Data de contratação: ");
        String hireDate = scanner.nextLine();
        hireDate = scanner.nextLine();

        while(true) {
            if(!dateValidate(hireDate)) {
                System.out.println("\n============================================================ \n" +
                        "Insira a data no formato correto (xx/xx/xxxx)! \n" +
                        "============================================================ \n");

                System.out.println("Data de contratação: ");
                hireDate = scanner.nextLine();
                continue;
            }
            break;
        }

        System.out.println("Insira o salário CLT bruto: ");
        double salario = doubleValidate(scanner);

        Endereco endereco = new Endereco(rua, cidade, bairro, numero);
        return new Funcionario(nome, email, celular, password, cpf, endereco, parseStringToDate(hireDate),Cargo.CHEFE_SECAO, Setor.MERCEARIA, salario, new ArrayList<>(), new ArrayList<>());

    }

    public static Operador menuCadastrarOperador(Scanner scanner) throws ParseException {

        System.out.println("Insxira seu nome: ");
        String nome = scanner.next();

        System.out.println("Insira seu email: ");
        String email = scanner.next();

        System.out.println("Insira seu celular: ");
        String celular = scanner.next();

        System.out.println("Insira sua password: ");
        String password = scanner.next();

        password = passwordValidate(password, scanner);

        System.out.println("Insira seu cpf: ");
        String cpf = scanner.next();

        System.out.println("Insira sua rua: ");
        String rua = scanner.nextLine();
        rua = scanner.nextLine();

        System.out.println("Insira seu cidade: ");
        String cidade = scanner.nextLine();

        System.out.println("Insira seu bairro: ");
        String bairro = scanner.nextLine();

        System.out.println("Insira seu numero: ");
        int numero = integerValidate(scanner);

        System.out.println("Data de contratação: ");
        String hireDate = scanner.next();

        while(true) {
            if(!dateValidate(hireDate)) {
                System.out.println("\n============================================================ \n" +
                        "Insira a data no formato correto (xx/xx/xxxx)! \n" +
                        "============================================================ \n");

                System.out.println("Data de contratação: ");
                hireDate = scanner.nextLine();
                continue;
            }
            break;
        }

        System.out.println("Insira o salário CLT bruto: ");
        double salario = doubleValidate(scanner);

        System.out.println("Digite a carga horária");
        int cargaHoraria = integerValidate(scanner);

        Endereco endereco = new Endereco(rua, cidade, bairro, numero);

        return new Operador(nome,email,celular,password,cpf, endereco, parseStringToDate(hireDate), Cargo.OPERADOR, salario, cargaHoraria);
    }

    public static Produto cadastrarProduto(Scanner scanner) {

        // String nome, int quantidade, double preco, Setor setor, Date dataVencimento

        System.out.println("Digite o nome do produto: ");
        String nome = scanner.nextLine();
        nome = scanner.nextLine();

        System.out.println("Digite a quantidade do produto: ");
        int quantidade = integerValidate(scanner);

        System.out.println("Digite o preço do produto: ");
        double preco = doubleValidate(scanner);

        System.out.println("Setores (1 - MERCEARIA / 2 - BAZAR / 3 - ELETRONICOS / 4 - COMERCIAL) ");
        int setor = opcaoMenu(scanner, 1, 4);

        Setor setorEnum = Setor.findByCd(setor);

        System.out.println("Digite a data de vencimento do produto: ");
        String dataDeVencimento = scanner.next();

        while(true) {
            if(!dateValidate(dataDeVencimento)) {
                System.out.println("\n============================================================ \n" +
                        "Insira a data no formato correto (xx/xx/xxxx)! \n" +
                        "============================================================ \n");

                System.out.println("Data de vencimento: ");
                dataDeVencimento = scanner.nextLine();
                continue;
            }
            break;
        }

        Produto produto = new Produto(nome, quantidade, preco, setorEnum, parseStringToDate(dataDeVencimento));

        return produto;

    }

    public static Produto alterarProduto(Produto produto, Scanner scanner) {

        System.out.println("1 - Alterar a quantidade: " +
                         "\n2 - Alterar o preço: " +
                         "\n3 - Voltar");
        int opcao = opcaoMenu(scanner, 1, 3);

        if(opcao == 1) {
            System.out.println("\nDigite a nova quantidade do estoque: ");
            int quantidade = integerValidate(scanner);

            Setor setorEnum = Setor.findByCd(produto.getSetor());

            Produto produtoAlterado = new Produto(produto.getNome(), quantidade, produto.getPreco(), setorEnum, produto.getDataVencimento());

            return produtoAlterado;
        } else if(opcao == 2) {
            System.out.println("\nDigite o novo preço: ");
            double preco = doubleValidate(scanner);

            Setor setorEnum = Setor.findByCd(produto.getSetor());

            Produto produtoAlterado = new Produto(produto.getNome(), produto.getQuantidade(), preco, setorEnum, produto.getDataVencimento());

            return  produtoAlterado;
        }

        return null;

    }
}
