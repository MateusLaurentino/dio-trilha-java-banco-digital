package domain.entities;

import domain.enums.ETipoDocumento;
import domain.extensions.Validador;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod({Validador.class})
public class Cliente {
    private String nomeCompleto;
    private ETipoDocumento tipoDocumento;
    private String documento;

    public Cliente(String nomeCompleto, String documento) {
        this.nomeCompleto = nomeCompleto;
        this.documento = documento;
        this.tipoDocumento = documento.verificarCpf() ? ETipoDocumento.CPF : ETipoDocumento.CNPJ;
    }

    public String buscarNome() {
        return this.nomeCompleto;
    }
}
