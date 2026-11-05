import { Cafe } from './cafe.model';

export type TipoMoagem = 'GRAOS' | 'ESPRESSO' | 'PRENSA_FRANCESA' | 'COADOR_V60';

export interface OpcaoMoagem {
  id: TipoMoagem;
  label: string;
  sublabel: string;
  icon: string;
}

export const OPCOES_MOAGEM: OpcaoMoagem[] = [
  {
    id: 'GRAOS',
    label: 'Em Grãos',
    sublabel: 'Preserva aromas originais',
    icon: 'grain'
  },
  {
    id: 'ESPRESSO',
    label: 'Moído Fino (Espresso)',
    sublabel: 'Extração sob pressão',
    icon: 'coffee_maker'
  },
  {
    id: 'PRENSA_FRANCESA',
    label: 'Moído Grosso (Prensa)',
    sublabel: 'Prensa Francesa / Cold Brew',
    icon: 'Local_cafe'
  },
  {
    id: 'COADOR_V60',
    label: 'Moído Médio (V60 / Filtro)',
    sublabel: 'Coador tradicional & Hario V60',
    icon: 'filter_alt'
  }
];

export interface CartItem {
  cafe: Cafe;
  quantidade: number;
  moagem: TipoMoagem;
  moagemLabel: string;
}
