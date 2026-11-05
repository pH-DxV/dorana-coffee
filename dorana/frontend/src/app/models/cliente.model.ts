export interface TelefonePayloadDTO {
  ddd: string;
  numero: string;
}

export interface EnderecoPayloadDTO {
  cep: string;
  rua: string;
  numero: string;
  complemento?: string;
  bairro: string;
  idMunicipio: number;
}

export interface ClienteDTO {
  nome: string;
  email: string;
  login: string;
  senha?: string;
  cpf: string;
  dataNascimento: string;
  telefones: TelefonePayloadDTO[];
  enderecos: EnderecoPayloadDTO[];
}

export interface AlterarSenhaDTO {
  senhaAntiga: string;
  novaSenha: string;
}
