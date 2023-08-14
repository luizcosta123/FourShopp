package br.com.fourshopp;

import br.com.fourshopp.Util.UtilMenu;
import br.com.fourshopp.entities.*;
import br.com.fourshopp.repository.EnderecoRepository;
import br.com.fourshopp.repository.FuncionarioRepository;
import br.com.fourshopp.repository.ProdutoRepository;
import br.com.fourshopp.service.ClienteService;
import br.com.fourshopp.service.FuncionarioService;
import br.com.fourshopp.service.OperadorService;
import br.com.fourshopp.service.ProdutoService;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import java.io.*;
import java.text.ParseException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static br.com.fourshopp.Util.UtilMenu.*;
import static br.com.fourshopp.Util.UtilValidate.*;

@SpringBootApplication
public class FourShoppApplication implements CommandLineRunner {

    Scanner scanner = new Scanner(System.in);

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private OperadorService operadorService;

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private FuncionarioService funcionarioService;

    private Cliente cliente;

    public FourShoppApplication() {
    }

    public static void main(String[] args) {
        SpringApplication.run(FourShoppApplication.class, args);
    }

    @Override
    public void run(String[] args) throws Exception {

        if(!funcionarioRepository.findByCpfAndPassword("894.197.260-45", "12341234").isPresent()) {
            Endereco endereco = new Endereco("Rua Foursys", "Cidade Foursys", "Cidade Foursys", 100);

            enderecoRepository.save(endereco);

            Funcionario funcionario = new Funcionario("Adm", "adm@gmail.com", "(15)98150-0110",
                    "12341234", "894.197.260-45", endereco, parseStringToDate("22/03/2020"),
                    Cargo.ADMINISTRADOR, Setor.COMERCIAL, 10000.0, new ArrayList<>(), new ArrayList<>());

            funcionarioRepository.save(funcionario);
        }

        System.out.println("====== BEM-VINDO AO FOURSHOPP ======");
        System.out.println("1- Sou cliente " +
                         "\n2- Área do ADM " +
                         "\n3- Seja um Cliente " +
                         "\n4- Login funcionário" +
                         "\n5- Encerrar ");
        int opcao = opcaoMenu(scanner, 1, 6);

        menuInicial(opcao);
    }

    public void menuInicial(int opcao) throws CloneNotSupportedException, IOException, ParseException {
        if(opcao == 1){

            System.out.println("Insira seu cpf (xxx.xxx.xxx-xx): ");
            String cpf = scanner.next();

            while(true) {
                if(!cpfValidate(cpf)) {
                    System.out.println("\n============================================================ \n" +
                            "Insira um cpf válido e no formato correto (xxx.xxx.xxx-xx)! \n" +
                            "============================================================ \n");
                    System.out.println("Insira seu cpf: ");
                    cpf = scanner.next();
                    continue;
                }
                break;
            }

            System.out.println("Insira sua password: ");
            String password = scanner.next();
            password = passwordValidate(password, scanner);

            try {
                this.cliente = clienteService.loadByEmailAndPassword(cpf, password).orElseThrow(() -> new ObjectNotFoundException(1L, "Cliente"));
            } catch (ObjectNotFoundException e) {
                System.err.println("Cliente não encontrado!\n");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                menuInicial(home(scanner));
            } catch (ClassCastException e) {
                System.err.println("Este usuário não é um cliente!\n");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                menuInicial(home(scanner));
            }

            int contador = 1;
            while (contador == 1) {
                System.out.println("Escolha o setor: ");
                int setor = menuSetor(scanner);

                List<Produto> collect = produtoService.listaProdutosPorSetor(setor).stream().filter(x -> x.getSetor() == setor).collect(Collectors.toList());
                collect.forEach(produto -> {
                    System.out.println(produto.getId()+"- "+produto.getNome()+" Preço: "+produto.getPreco()+" Estoque = "+produto.getQuantidade());
                });

                System.out.println("Informe o número do produto desejado: ");
                Long produto = scanner.nextLong();

                System.out.println("Escolha a quantidade");
                int quantidade = integerValidate(scanner);

                // Atualiza estoque
                Produto foundById = produtoService.findById(produto);

                while(true) {
                    if(produtoService.diminuirEstoque(quantidade, foundById)) {
                        System.out.println("Produto comprado com sucesso!");
                        break;
                    }
                    System.out.println("Escolha uma quantidade igual ou menor que o estoque!");
                    quantidade = integerValidate(scanner);
                }

                Produto clone = foundById.clone();
                System.out.println(clone.toString());
                clone.getCalculaValor(quantidade, clone);
                cliente.getProdutoList().add(clone);
                System.out.println("Deseja outro produto S/N ?");
                String escolha  = scanner.next();

                if(!escolha.equalsIgnoreCase("S")) {
                    contador = 2;
                    gerarCupomFiscal(cliente);
                    System.out.println("Gerando nota fiscal...");
                    System.err.println("Fechando a aplicação...");
                }
            }
        }

        if(opcao == 2){
            System.out.println("INTRANET FOURSHOPP....");

            System.out.println("Insira as credenciais do usuário administrador: ");

            System.out.println("Insira seu cpf: ");
            String cpf = scanner.next();

            if(!cpfValidate(cpf)) {
                System.out.println("\n============================================================ \n" +
                        "Insira um cpf válido e no formato correto (xxx.xxx.xxx-xx)! \n" +
                        "============================================================ \n");
                menuInicial(2);
            }

            System.out.println("Insira sua password: ");
            String password = scanner.next();

            password = passwordValidate(password, scanner); // novo

            try {
                Optional<Funcionario> admnistrador = this.funcionarioService.loadByEmailAndPassword(cpf, password);
                if(admnistrador.get().getCargo().getCd() != Cargo.ADMINISTRADOR.getCd()) {
                    System.out.println("Você não possui cargo de administrador!");
                    menuInicial(2);
                }
            } catch (NoSuchElementException e) {
                System.err.println("Administrador não encontrado!");
                menuInicial(2);
            } catch (ClassCastException e) {
                System.err.println("Este usuário não é um administrador!\n");
                menuInicial(home(scanner));
            }

            System.out.println("1 - Cadastrar funcionários" +
                             "\n2 - Cadastrar Operador" +
                             "\n3 - Demitir funcionário" +
                             "\n4 - Demitir Operador");
            int escolhaAdm = opcaoMenu(scanner, 1, 4);
            if(escolhaAdm == 1) {
                Funcionario funcionario = cadastrarFuncionario(scanner);
                this.funcionarioService.create(funcionario);
                System.out.println("Funcionário cadastrado com sucesso");
                menuInicial(home(scanner));
            } else if(escolhaAdm == 2){
                Operador operador = UtilMenu.menuCadastrarOperador(scanner);

                List<Funcionario> listaChefesSecao = funcionarioService.listAllByCargo(Cargo.CHEFE_SECAO);
                listarChefesDeSecao(listaChefesSecao);

                System.out.println("Digite o id do chefe deste funcionário: ");
                Long idChefe = longValidate(scanner);

                operador.setFuncionario(this.funcionarioService.findById(idChefe));
                this.operadorService.create(operador);
                System.out.println("Operador cadastrado com sucesso");
                menuInicial(2);
            } else if(escolhaAdm == 3) {
                List<Funcionario> listaChefesSecao = funcionarioService.listAllByCargo(Cargo.CHEFE_SECAO);
                listarChefesDeSecao(listaChefesSecao);

                System.out.println("Digite o número do funcionário a ser desligado: ");
                Long opcaoFunc = longValidate(scanner);

                while(true) {
                    try {
                        funcionarioService.findById(opcaoFunc);
                        break;
                    } catch (ResourceNotFoundException e) {
                        System.out.println("Digite uma opção válida!");
                        opcaoFunc = longValidate(scanner);
                    } catch (ConstraintViolationException e) {
                        System.out.println("O funcionário não pode ser excluído!");
                        opcaoFunc = longValidate(scanner);
                    }
                }

                try {
                    funcionarioService.remove(opcaoFunc);
                } catch (DataIntegrityViolationException e) {
                    System.err.println("Este funcionário possui outros operários vinculados em sua lista, por favor retire estes antes de excluir este funcionário");
                }

                menuInicial(home(scanner));
            } else if(escolhaAdm == 4) {
                List<Operador> listaOperario = operadorService.listAll();
                listarOperadores(listaOperario);

                System.out.println("Digite o número do operador a ser desligado: ");
                Long opcaoOp = longValidate(scanner);

                while (true) {
                    try {
                        operadorService.findById(opcaoOp);
                        break;
                    } catch (ResourceNotFoundException e) {
                        System.out.println("Digite uma opção válida!");
                        opcaoOp = longValidate(scanner);
                    } catch (ConstraintViolationException e) {
                        System.out.println("O operador não pode ser excluído!");
                        opcaoOp = longValidate(scanner);
                    }
                }

                try {
                    operadorService.remove(opcaoOp);
                } catch (DataIntegrityViolationException e) {
                    System.err.println("Este funcionário possui outros operários vinculados em sua lista, por favor retire estes antes de excluir este funcionário");
                }

                menuInicial(home(scanner));
            } else {
                System.out.println("Opção inválida");
                menuInicial(home(scanner));
            }

        }

        if(opcao == 3) {
            Cliente cliente = menuCadastroCliente(scanner);
            this.clienteService.create(cliente);
            System.out.println("Bem-vindo, " + cliente.getNome());
            menuInicial(1);
        }

        if(opcao == 4){
            System.out.println("Área do funcionário");

            System.out.println("1- Chefe  \n2- Operador ");
            int escolhaCargo = opcaoMenu(scanner, 1, 2);

            System.out.println("Insira seu cpf: ");
            String cpf = scanner.next();

            while(true) {
                if(!cpfValidate(cpf)) {
                    System.out.println("\n============================================================ \n" +
                            "Insira um cpf válido e no formato correto (xxx.xxx.xxx-xx)! \n" +
                            "============================================================ \n");
                    System.out.println("Insira seu cpf: ");
                    cpf = scanner.next();
                    continue;
                }
                break;
            }

            System.out.println("Insira sua password: "); // novo
            String password = scanner.next();

            password = passwordValidate(password, scanner);

            if (escolhaCargo == 1) {

                try {
                    Optional<Funcionario> chefe = this.funcionarioService.loadByEmailAndPassword(cpf, password);
                    if((chefe.get().getCargo().getCd() != Cargo.CHEFE_SECAO.getCd()) && (chefe.get().getCargo().getCd() != Cargo.ADMINISTRADOR.getCd())) {
                        System.err.println("Você não possui cargo de chefe de seção ou superior!");
                        menuInicial(home(scanner));
                    }
                } catch (NoSuchElementException e) {
                    System.err.println("Chefe não encontrado!");
                    menuInicial(home(scanner));
                } catch (ClassCastException e) {
                    System.err.println("Você não possui cargo de chefe de seção!");
                    menuInicial(home(scanner));
                }

                System.out.println("1 - Cadastrar produto" +
                                 "\n2 - Alterar produto" +
                                 "\n3 - Excluir produto" +
                                 "\n4 - Cadastrar operadores");
                int opcaoCadastro = opcaoMenu(scanner, 1, 4);

                    if(opcaoCadastro == 1) {

                        Produto produto = cadastrarProduto(scanner);
                        this.produtoService.create(produto);

                        menuInicial(home(scanner));

                    } else if (opcaoCadastro == 2) {

                        System.out.println("\n" + produtoService.listAll());
                        System.out.println("Escolha o produto a ser alterado: ");
                        Long opcaoProduto = longValidate(scanner);

                        while(true) {
                            try {
                                produtoService.findById(opcaoProduto);
                                break;
                            } catch (ResourceNotFoundException e) {
                                System.out.println("Digite um produto válido!");
                                opcaoProduto = longValidate(scanner);
                            }
                        }

                        produtoService.update(alterarProduto(produtoService.findById(opcaoProduto), scanner), opcaoProduto);
                        System.out.println("Produto alterado com sucesso!");
                        menuInicial(home(scanner));

                    } else if (opcaoCadastro == 3) {
                        System.out.println("\n" + produtoService.listAll());
                        System.out.println("Escolha o produto a ser excluido: ");
                        Long opcaoProduto = longValidate(scanner);

                        while(true) {
                            try {
                                produtoService.findById(opcaoProduto);
                                break;
                            } catch (ResourceNotFoundException e) {
                                System.out.println("Digite um produto válido!");
                                opcaoProduto = longValidate(scanner);
                            }
                        }

                        System.err.println("Você realmente quer excluir este produto (1 - SIM / 2 - Não) ?");
                        int confirma = opcaoMenu(scanner, 1, 2);

                        if(confirma == 1) {
                            produtoService.remove(opcaoProduto);
                            System.out.println("Produto excluído com sucesso!");
                            menuInicial(home(scanner));
                        } else if(confirma == 2) {
                            menuInicial(4);
                        }

                    } else if(opcaoCadastro == 4) {
                        Operador operador = menuCadastrarOperador(scanner);
                        operador.setFuncionario(this.funcionarioService.loadByEmailAndPassword(cpf,password).get());
                        operadorService.create(operador);
                        System.out.println("Operador cadastrado!");
                        menuInicial(home(scanner));
                    }

            } else if(escolhaCargo == 2) {
                try {
                    Optional<Operador> operador = this.operadorService.loadByEmailAndPassword(cpf, password);
                    if(operador.get().getCargo().getCd() != Cargo.OPERADOR.getCd()) {
                        System.err.println("Você não possui cargo de operador!");
                        menuInicial(home(scanner));
                    }
                } catch (NoSuchElementException e) {
                    System.err.println("Operador não encontrado!");
                    menuInicial(home(scanner));
                } catch (ClassCastException e) {
                    System.err.println("Você não possui cargo de operador!");
                    menuInicial(home(scanner));
                }
                
                System.err.println("Área do operador em construção... =)");

            }
        }

        if(opcao == 5) {
            System.out.println("Sistema finalizado!");
            System.exit(0);
        }
    }


}
