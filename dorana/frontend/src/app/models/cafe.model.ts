import { Marca } from './marca.model';
import { CategoriaDoCafe } from './categoria-do-cafe.model';
import { NivelDeTorra } from './nivel-de-torra.model';
import { Tratamento } from './tratamento.model';
import { NotaSensorial } from './nota-sensorial.model';

export class Cafe {
  id!: number;
  nome!: string;
  descricao!: string;
  marca?: Marca;
  categoriaDoCafe?: CategoriaDoCafe;
  nivelDeTorra?: NivelDeTorra;
  tratamento?: Tratamento;
  notasSensoriais?: NotaSensorial[];
  pontuacaoSCA?: number;
  preco!: number;
  peso!: number;
  estoque?: number;
}

export interface CafePayloadDTO {
  nome: string;
  descricao: string;
  idMarca?: number;
  idCategoriaDoCafe?: number;
  idNivelDeTorra?: number;
  idTratamento?: number;
  notasSensoriais: Array<string | { id: number }>;
  pontuacaoSCA?: number;
  preco: number;
  peso: number;
  estoque?: number;
}

export const NIVEIS_DE_TORRA_OPCOES: NivelDeTorra[] = [
  { id: 1, nome: 'Torra Clara' },
  { id: 2, nome: 'Torra Média' },
  { id: 3, nome: 'Torra Escura' }
];

export const TRATAMENTOS_OPCOES: Tratamento[] = [
  { id: 1, nome: 'Natural', descricao: 'Secagem com o fruto inteiro ao sol, ressaltando doçura e corpo.' },
  { id: 2, nome: 'Cereja Descascado', descricao: 'Remoção da casca antes da secagem, equilíbrio entre doçura e acidez.' },
  { id: 3, nome: 'Lavado (Via Úmida)', descricao: 'Fermentação controlada em tanques de água, acidez limpa e brilhante.' },
  { id: 4, nome: 'Honey', descricao: 'Secagem com a mucilagem aderida ao pergaminho, textura licorosa.' },
  { id: 5, nome: 'Fermentado', descricao: 'Fermentação anaeróbica controlada, notas exóticas e complexas.' }
];

export const NOTAS_SENSORIAIS_OPCOES: Array<{ id: number; enumName: string; nome: string; categoria: string }> = [
  { id: 1, enumName: 'FLORAL', nome: 'Floral', categoria: 'Floral' },
  { id: 2, enumName: 'FRUTAS_VERMELHAS', nome: 'Frutas Vermelhas', categoria: 'Frutado' },
  { id: 3, enumName: 'FRUTAS_AMARELAS', nome: 'Frutas Amarelas', categoria: 'Frutado' },
  { id: 4, enumName: 'CITRICO', nome: 'Cítrico', categoria: 'Frutado' },
  { id: 5, enumName: 'CHOCOLATE', nome: 'Chocolate', categoria: 'Adocicado' },
  { id: 6, enumName: 'CARAMELO', nome: 'Caramelo', categoria: 'Adocicado' },
  { id: 7, enumName: 'MEL', nome: 'Mel', categoria: 'Adocicado' },
  { id: 8, enumName: 'NOZES', nome: 'Nozes', categoria: 'Castanhas' },
  { id: 9, enumName: 'AMENDOA', nome: 'Amêndoa', categoria: 'Castanhas' },
  { id: 10, enumName: 'ESPECIARIAS', nome: 'Especiarias', categoria: 'Especiarias' },
  { id: 11, enumName: 'BAUNILHA', nome: 'Baunilha', categoria: 'Adocicado' },
  { id: 12, enumName: 'RAPADURA', nome: 'Rapadura', categoria: 'Adocicado' }
];

export const STITCH_CAFE_IMAGES: string[] = [
  'https://images.unsplash.com/photo-1559056199-641a0ac8b55e?auto=format&fit=crop&w=900&q=85',
  'https://images.unsplash.com/photo-1611854779393-1b2da9d400fe?auto=format&fit=crop&w=900&q=85',
  'https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?auto=format&fit=crop&w=900&q=85',
  'https://images.unsplash.com/photo-1587734195503-904fca47e0e9?auto=format&fit=crop&w=900&q=85',
  'https://images.unsplash.com/photo-1497935586351-b67a49e012bf?auto=format&fit=crop&w=900&q=85',
  'https://images.unsplash.com/photo-1509042239860-f550ce710b93?auto=format&fit=crop&w=900&q=85'
];

export function getCafeImage(cafe?: Cafe | null, index = 0): string {
  const seed = cafe?.id ? cafe.id - 1 : index;
  return STITCH_CAFE_IMAGES[Math.abs(seed) % STITCH_CAFE_IMAGES.length];
}
