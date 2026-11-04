import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { Marca } from '../models/marca.model';
import { MarcaService } from '../services/marca.service';

export const marcaResolver: ResolveFn<Marca> = (
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot
): Observable<Marca> => {
  return inject(MarcaService).findById(+route.paramMap.get('id')!);
};
