export interface PerfilAuthDTO {
  id?: number;
  label?: string;
}

export interface LoginDTO {
  login: string;
  senha: string;
}

export interface AuthDTOResponse {
  id?: number;
  nome?: string;
  login?: string;
  email?: string;
  token?: string;
  perfil?: string;
  perfis?: PerfilAuthDTO[];
}
