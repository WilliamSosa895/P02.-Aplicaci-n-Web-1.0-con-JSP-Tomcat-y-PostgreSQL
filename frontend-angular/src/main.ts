import { Component, OnInit, OnDestroy } from '@angular/core';
import { bootstrapApplication } from '@angular/platform-browser';
import { HttpClient, provideHttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';

interface DeviceStatus {
  deviceId: string; variable: string; unit: string; value: number;
  quality: string; capturedAt: string; conexion: string; segundosDesdeUltima: number;
}
interface Reading {
  deviceId: string; variable: string; unit: string; value: number;
  quality: string; capturedAt: string;
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule],
  template: `
    <main>
      <header>
        <h1>PR10 — Monitoreo IoT (Web 4.0)</h1>
        <p>Dashboard de telemetría simulada. Actualiza cada 3 s desde la API REST.</p>
      </header>

      <p class="error" *ngIf="error">{{ error }}</p>

      <section>
        <h2>Dispositivos</h2>
        <div class="tarjetas">
          <article class="tarjeta" *ngFor="let d of dispositivos"
                   [class.sel]="d.deviceId === seleccionado" (click)="verHistorial(d.deviceId)">
            <div class="cab">
              <strong>{{ d.deviceId }}</strong>
              <span class="chip" [ngClass]="claseConexion(d)">{{ etiquetaConexion(d) }}</span>
            </div>
            <div class="valor">{{ d.value }} <span class="unidad">{{ d.unit }}</span></div>
            <div class="meta">{{ d.variable }}</div>
            <span class="estado" [ngClass]="'q-' + d.quality">{{ d.quality }}</span>
          </article>
          <p *ngIf="dispositivos.length === 0" class="vacio">Sin datos. Ejecuta el simulador.</p>
        </div>
      </section>

      <section>
        <h2>Alertas activas <span class="conteo">({{ alertas.length }})</span></h2>
        <table *ngIf="alertas.length > 0">
          <tr><th>Dispositivo</th><th>Variable</th><th>Valor</th><th>Calidad</th><th>Hora</th></tr>
          <tr *ngFor="let a of alertas" [ngClass]="'fila-' + a.quality">
            <td>{{ a.deviceId }}</td><td>{{ a.variable }}</td>
            <td>{{ a.value }} {{ a.unit }}</td><td>{{ a.quality }}</td>
            <td>{{ a.capturedAt | date:'HH:mm:ss' }}</td>
          </tr>
        </table>
        <p *ngIf="alertas.length === 0" class="vacio">Sin alertas: todas las lecturas están en rango normal.</p>
      </section>

      <section *ngIf="seleccionado">
        <h2>Historial — {{ seleccionado }}</h2>
        <svg *ngIf="historial.length > 1" class="spark" viewBox="0 0 320 80" preserveAspectRatio="none">
          <polyline [attr.points]="puntos()" fill="none" stroke="#2563eb" stroke-width="2"/>
        </svg>
        <table>
          <tr><th>Valor</th><th>Calidad</th><th>Fecha-hora</th></tr>
          <tr *ngFor="let r of historial" [ngClass]="'fila-' + r.quality">
            <td>{{ r.value }} {{ r.unit }}</td><td>{{ r.quality }}</td>
            <td>{{ r.capturedAt | date:'yyyy-MM-dd HH:mm:ss' }}</td>
          </tr>
        </table>
      </section>
    </main>
  `,
})
class App implements OnInit, OnDestroy {
  dispositivos: DeviceStatus[] = [];
  alertas: Reading[] = [];
  historial: Reading[] = [];
  seleccionado = '';
  error = '';
  private timer: any;

  constructor(private http: HttpClient) {}

  ngOnInit(): void { this.refrescar(); this.timer = setInterval(() => this.refrescar(), 3000); }
  ngOnDestroy(): void { if (this.timer) clearInterval(this.timer); }

  refrescar(): void {
    this.http.get<DeviceStatus[]>('/api/telemetry/latest').subscribe({
      next: d => { this.dispositivos = d; this.error = ''; },
      error: () => { this.error = 'No fue posible consultar la API de telemetría.'; },
    });
    this.http.get<Reading[]>('/api/telemetry/alerts').subscribe({ next: a => this.alertas = a });
    if (this.seleccionado) this.verHistorial(this.seleccionado, false);
  }

  verHistorial(deviceId: string, marcar = true): void {
    if (marcar) this.seleccionado = deviceId;
    this.http.get<Reading[]>('/api/telemetry/history/' + deviceId)
      .subscribe({ next: h => this.historial = h });
  }

  claseConexion(d: DeviceStatus): string {
    return d.conexion === 'DESCONECTADO' ? 'off' : 'on';
  }
  etiquetaConexion(d: DeviceStatus): string {
    return d.conexion === 'DESCONECTADO'
      ? 'Desconectado (' + d.segundosDesdeUltima + 's)' : 'En línea';
  }

  // Sparkline del historial (más antiguo a la izquierda).
  puntos(): string {
    const datos = [...this.historial].reverse();
    const vals = datos.map(r => r.value);
    const min = Math.min(...vals), max = Math.max(...vals);
    const rango = max - min || 1;
    return datos.map((r, i) => {
      const x = (i / (datos.length - 1)) * 320;
      const y = 76 - ((r.value - min) / rango) * 72;
      return x.toFixed(1) + ',' + y.toFixed(1);
    }).join(' ');
  }
}

bootstrapApplication(App, { providers: [provideHttpClient()] }).catch(console.error);
