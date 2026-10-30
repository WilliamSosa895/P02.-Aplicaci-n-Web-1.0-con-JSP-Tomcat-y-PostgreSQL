import { Component, OnInit } from '@angular/core';
import { bootstrapApplication } from '@angular/platform-browser';
import { HttpClient, HttpHeaders, provideHttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';

interface Organizacion { id: number; nombre: string; sector?: string; }
interface Estudiante { id: number; nombre: string; programa?: string; }
interface Practica {
  id?: number;
  organizacionId: number | null;
  estudianteId: number | null;
  titulo: string;
  estado: string;
  creadaEn?: string;
}

const LAB_KEY = 'lab-only-key'; // clave de laboratorio (no es un secreto real)
const ESTADOS = ['POSTULADA', 'ACEPTADA', 'EN_CURSO', 'CONCLUIDA', 'RECHAZADA'];

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <main>
      <h1>PR10 — Prácticas profesionales</h1>
      <p>Cliente Angular integrado con la API REST (Spring Boot) y PostgreSQL.</p>

      <section>
        <h2>Registrar práctica</h2>
        <form (ngSubmit)="crear()">
          <label>Organización
            <select name="org" [(ngModel)]="nueva.organizacionId" required>
              <option [ngValue]="null" disabled>Seleccione…</option>
              <option *ngFor="let o of organizaciones" [ngValue]="o.id">{{ o.nombre }}</option>
            </select>
          </label>
          <label>Estudiante
            <select name="est" [(ngModel)]="nueva.estudianteId" required>
              <option [ngValue]="null" disabled>Seleccione…</option>
              <option *ngFor="let e of estudiantes" [ngValue]="e.id">{{ e.nombre }}</option>
            </select>
          </label>
          <label>Título <input name="tit" [(ngModel)]="nueva.titulo" required minlength="3"></label>
          <label>Estado
            <select name="edo" [(ngModel)]="nueva.estado" required>
              <option value="" disabled>Seleccione…</option>
              <option *ngFor="let s of estados" [value]="s">{{ s }}</option>
            </select>
          </label>
          <button type="submit">Guardar</button>
        </form>
        <p class="error" *ngIf="error">{{ error }}</p>
        <p class="ok" *ngIf="ok">{{ ok }}</p>
      </section>

      <section>
        <h2>Prácticas registradas</h2>
        <button (click)="load()">Actualizar</button>
        <table>
          <tr>
            <th>ID</th><th>Organización</th><th>Estudiante</th><th>Título</th>
            <th>Estado</th><th>Cambiar estado</th>
          </tr>
          <tr *ngFor="let p of items">
            <td>{{ p.id }}</td>
            <td>{{ nombreOrg(p.organizacionId) }}</td>
            <td>{{ nombreEst(p.estudianteId) }}</td>
            <td>{{ p.titulo }}</td>
            <td>{{ p.estado }}</td>
            <td class="acciones">
              <select [(ngModel)]="seleccion[p.id!]">
                <option *ngFor="let s of estados" [value]="s">{{ s }}</option>
              </select>
              <button (click)="cambiarEstado(p)">Aplicar</button>
            </td>
          </tr>
          <tr *ngIf="items.length === 0"><td colspan="6">Sin prácticas registradas.</td></tr>
        </table>
      </section>
    </main>
  `,
})
class App implements OnInit {
  items: Practica[] = [];
  organizaciones: Organizacion[] = [];
  estudiantes: Estudiante[] = [];
  estados = ESTADOS;
  seleccion: Record<number, string> = {};
  nueva: Practica = { organizacionId: null, estudianteId: null, titulo: '', estado: '' };
  error = '';
  ok = '';

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    forkJoin({
      orgs: this.http.get<Organizacion[]>('/api/organizaciones'),
      ests: this.http.get<Estudiante[]>('/api/estudiantes'),
    }).subscribe({
      next: r => { this.organizaciones = r.orgs; this.estudiantes = r.ests; },
      error: () => { this.error = 'No fue posible cargar los catálogos.'; },
    });
    this.load();
  }

  private headers(): HttpHeaders {
    return new HttpHeaders({ 'X-Lab-Key': LAB_KEY, 'Content-Type': 'application/json' });
  }

  load(): void {
    this.http.get<Practica[]>('/api/practicas').subscribe({
      next: v => {
        this.items = v;
        v.forEach(p => { if (p.id != null) this.seleccion[p.id] = p.estado; });
      },
      error: () => { this.error = 'No fue posible consultar la API.'; },
    });
  }

  crear(): void {
    this.error = '';
    this.ok = '';
    this.http.post<Practica>('/api/practicas', this.nueva, { headers: this.headers() }).subscribe({
      next: () => {
        this.ok = 'Práctica registrada.';
        this.nueva = { organizacionId: null, estudianteId: null, titulo: '', estado: '' };
        this.load();
      },
      error: err => { this.error = this.mensajeError(err, 'registrar la práctica'); },
    });
  }

  cambiarEstado(p: Practica): void {
    this.error = '';
    this.ok = '';
    const nuevoEstado = this.seleccion[p.id!];
    this.http.patch<Practica>(`/api/practicas/${p.id}/estado`, { estado: nuevoEstado },
      { headers: this.headers() }).subscribe({
      next: () => { this.ok = `Estado de la práctica ${p.id} actualizado a ${nuevoEstado}.`; this.load(); },
      error: err => { this.error = this.mensajeError(err, 'cambiar el estado'); },
    });
  }

  private mensajeError(err: any, accion: string): string {
    if (err.status === 401) return 'No autorizado (falta la clave de laboratorio).';
    if (err.status === 400) return 'Datos inválidos: revise los campos y el estado.';
    if (err.status === 404) return 'La práctica indicada no existe.';
    return `Error al ${accion}.`;
  }

  nombreOrg(id: number | null | undefined): string {
    return this.organizaciones.find(o => o.id === id)?.nombre ?? String(id ?? '');
  }
  nombreEst(id: number | null | undefined): string {
    return this.estudiantes.find(e => e.id === id)?.nombre ?? String(id ?? '');
  }
}

bootstrapApplication(App, { providers: [provideHttpClient()] }).catch(console.error);
