package br.unitins.topicos1.lgc.usuario.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import br.unitins.topicos1.lgc.administrador.model.Administrador;
import br.unitins.topicos1.lgc.cliente.model.Cliente;
import br.unitins.topicos1.lgc.endereco.dto.EnderecoDTO;
import br.unitins.topicos1.lgc.endereco.model.Endereco;
import br.unitins.topicos1.lgc.exception.ValidationException;
import br.unitins.topicos1.lgc.hash.service.HashService;
import br.unitins.topicos1.lgc.municipio.model.Municipio;
import br.unitins.topicos1.lgc.municipio.repository.MunicipioRepository;
import br.unitins.topicos1.lgc.perfil.model.Perfil;
import br.unitins.topicos1.lgc.telefone.dto.TelefoneDTO;
import br.unitins.topicos1.lgc.telefone.model.Telefone;
import br.unitins.topicos1.lgc.usuario.dto.UsuarioDTO;
import br.unitins.topicos1.lgc.usuario.dto.UsuarioDTOResponse;
import br.unitins.topicos1.lgc.usuario.model.Usuario;
import br.unitins.topicos1.lgc.usuario.repository.UsuarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class UsuarioServiceImpl implements UsuarioService {

    @Inject
    UsuarioRepository repository;

    @Inject
    HashService hashService; // Injeta o serviço de criptografia

    @Inject 
    MunicipioRepository municipioRepository; // Necessário para buscar município

    @Override
    @Transactional
    public UsuarioDTOResponse create(UsuarioDTO dto) {
        // Validações usando o método genérico do Panache (já que o Repository não tem o método específico)
        if (repository.find("cpf", dto.cpf()).firstResult() != null) {
            throw new ValidationException("CPF já cadastrado.");
        }
        if (repository.find("login", dto.login()).firstResult() != null) {
            throw new ValidationException("Login já cadastrado.");
        }

        // Instancia a subclasse correta baseada no perfil
        Usuario entity;
        if (dto.idPerfil() != null && dto.idPerfil() == 1) {
            entity = new Administrador();
        } else {
            entity = new Cliente();
        }
        entity.setNome(dto.nome());
        entity.setEmail(dto.email() != null && !dto.email().isBlank() ? dto.email() : dto.login() + "@coffehouse.com");
        entity.setCpf(dto.cpf());
        entity.setLogin(dto.login());
        
        // Criptografia
        entity.setSenha(hashService.getHashSenha(dto.senha()));

        // Perfil (Definido pelo DTO, pois é criação administrativa)
        entity.setPerfis(Set.of(Perfil.valueOf(dto.idPerfil().longValue())));

        entity.setDataNascimento(dto.dataNascimento());
        
        // --- 1. PROCESSAR TELEFONES ---
        List<Telefone> telefones = new ArrayList<>();
        for (TelefoneDTO telDto : dto.telefones()) {
            Telefone telefone = new Telefone();
            telefone.setDdd(telDto.ddd());
            telefone.setNumero(telDto.numero());
            telefone.setUsuario(entity); // Vínculo
            telefones.add(telefone);
        }
        entity.setTelefones(telefones);

        // --- 2. PROCESSAR ENDEREÇOS ---
        List<Endereco> enderecos = new ArrayList<>();
        for (EnderecoDTO endDto : dto.enderecos()) {
            Municipio municipio = municipioRepository.findById(endDto.idMunicipio());
            if (municipio == null) {
                throw new NotFoundException("Município não encontrado (ID: " + endDto.idMunicipio() + ")");
            }

            Endereco endereco = new Endereco();
            endereco.setCep(endDto.cep());
            endereco.setRua(endDto.rua());
            endereco.setNumero(endDto.numero());
            endereco.setComplemento(endDto.complemento());
            endereco.setBairro(endDto.bairro());
            endereco.setMunicipio(municipio);
            endereco.setUsuario(entity); // Vínculo
            enderecos.add(endereco);
        }
        entity.setEnderecos(enderecos);
        
        // Salva tudo em cascata
        repository.persist(entity);
        
        return UsuarioDTOResponse.valueOf(entity);
    }

    @Override
    @Transactional
    public UsuarioDTOResponse update(Long id, UsuarioDTO dto) {
        Usuario entity = repository.findById(id);
        if (entity == null) {
            throw new NotFoundException("Usuário não encontrado.");
        }
        entity.setNome(dto.nome());
        entity.setCpf(dto.cpf());
        entity.setLogin(dto.login());
        
        // Atualização de senha deve ser feita com cuidado. 
        // Aqui estamos permitindo atualizar direto, mas o ideal é um endpoint separado.
        String senhaHash = hashService.getHashSenha(dto.senha());
        entity.setSenha(senhaHash);

        // Atualiza o perfil
        entity.setPerfis(Set.of(Perfil.valueOf(dto.idPerfil().longValue())));

        entity.setDataNascimento(dto.dataNascimento());
        
        return UsuarioDTOResponse.valueOf(entity);
    }

    // --- Método Auxiliar para o Login (que você pediu antes) ---
    @Override
    public Usuario findByLoginAndSenha(String login, String senhaHash) {
        // A senha que chega aqui JÁ DEVE ESTAR hashada pelo AuthResource
        return repository.findByLoginAndSenha(login, senhaHash);
    }

    // ... (métodos delete, findById, findAll, findByNome mantêm-se iguais)
    
    @Override
    public void delete(Long id) {
        if (!repository.deleteById(id)) throw new NotFoundException("Usuário não encontrado.");
    }

    @Override
    public UsuarioDTOResponse findById(Long id) {
        Usuario entity = repository.findById(id);
        if (entity == null) throw new NotFoundException("Usuário não encontrado.");
        return UsuarioDTOResponse.valueOf(entity);
    }

    @Override
    public List<UsuarioDTOResponse> findAll() {
        return repository.listAll().stream().map(UsuarioDTOResponse::valueOf).collect(Collectors.toList());
    }

    @Override
    public List<UsuarioDTOResponse> findByNome(String nome) {
        return repository.findByNome(nome).stream().map(UsuarioDTOResponse::valueOf).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateSenha(Long idUsuario, String senhaAntiga, String novaSenha) {
        Usuario usuario = repository.findById(idUsuario);
        if (usuario == null) {
            throw new NotFoundException("Usuário não encontrado.");
        }

        String hashSenhaAntiga = hashService.getHashSenha(senhaAntiga);
        if (!usuario.getSenha().equals(hashSenhaAntiga)) {
            throw new ValidationException("Senha antiga incorreta.");
        }

        usuario.setSenha(hashService.getHashSenha(novaSenha));
    }
}