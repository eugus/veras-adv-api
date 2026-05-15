package br.com.adv.veras_api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.time.LocalDate;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Intimacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long comunicacaoDjenId;

    private String siglaTribunal;
    private String tipoComunicacao;
    private String numeroProcesso;
    private String numeroProcessoComMascara;
    private String nomeOrgao;
    private String tipoDocumento;
    private String nomeClasse;



    private LocalDate dataDisponibilizacao;

    @Column(columnDefinition = "TEXT")
    private String texto;

    @Column(columnDefinition = "TEXT")
    private String link;

    private String nomeAdvogado;
    private String numeroOab;
    private String ufOab;

    private String hash;
}
