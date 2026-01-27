import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../environments/environment';
import {Observable, map} from 'rxjs';
import {PageResponse} from '../models/page.response';


@Injectable({
  providedIn: 'root'
})
export abstract class AppServiceClientService<T> {
  protected readonly http = inject(HttpClient);
  protected readonly baseUrl = environment.apiUrl;

  protected constructor(protected readonly endpoint: string, protected mapFn?: (item: any) => T) {
  }

  getOne(id: number | string): Observable<T> {
    return this.http.get<T>(`${this.baseUrl}/${this.endpoint}/${id}`).pipe(
      map(data => this.mapFn ? this.mapFn(data) : data)
    );
  }

  getMany(): Observable<T[]> {
    return this.http.get<PageResponse<any>>(`${this.baseUrl}/${this.endpoint}`).pipe(
      map(response => {
        const items = response.content || [];
        return items.map(item => this.mapFn ? this.mapFn(item) : item);
      })
    );
  }

  getManyPaged(): Observable<PageResponse<T>> {
    return this.http.get<PageResponse<any>>(`${this.baseUrl}/${this.endpoint}`).pipe(
      map(response => ({
        ...response,
        content: response.content.map(item => this.mapFn ? this.mapFn(item) : item)
      }))
    );
  }

  create(payload: Partial<T>): Observable<T> {
    return this.http.post<T>(`${this.baseUrl}/${this.endpoint}`, payload);
  }

  update(id: number, payload: T) {
    return this.http.put<T>(`${this.baseUrl}/${this.endpoint}/${id}`, payload);
  }

  delete(id: number | string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${this.endpoint}/${id}`);
  }
}
