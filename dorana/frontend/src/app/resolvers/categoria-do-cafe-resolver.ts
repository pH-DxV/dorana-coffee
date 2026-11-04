import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { CategoriaDoCafe } from '../models/categoria-do-cafe.model';
import { CategoriaDoCafeService } from '../services/categoria-do-cafe.service';

export const categoriaDoCafeResolver: ResolveFn<CategoriaDoCafe> = (
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot
): Observable<CategoriaDoCafe> => {
  return inject(CategoriaDoCafeService).findById(+route.paramMap.get('id')!);
};
