package br.com.fourshopp.repository;

import br.com.fourshopp.entities.Cargo;
import br.com.fourshopp.entities.Cliente;
import br.com.fourshopp.entities.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {

    @Query("Select c From Pessoa c where c.cpf = ?1 and c.password = ?2")
    Optional<Funcionario> findByCpfAndPassword(@Param("cpf") String cpf, @Param("password") String password);

    @Query("Select f from Funcionario f where f.cargo = ?1")
    List<Funcionario> findByCargo(@Param("cargo") Cargo cargo);
}
