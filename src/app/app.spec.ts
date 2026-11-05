import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { LoginPage } from './pages/login/login';
import { TurnosApiService } from './core/turnos-api.service';

describe('TurnosApiService', () => {
  let api: TurnosApiService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    api = TestBed.inject(TurnosApiService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('consulta las canchas activas del backend', () => {
    api.canchas().subscribe((canchas) => expect(canchas[0].nombre).toBe('Cancha 1'));
    const request = http.expectOne('/api/v1/canchas');
    expect(request.request.method).toBe('GET');
    request.flush([{ id: 1, nombre: 'Cancha 1', direccion: 'Centro', tipoSuperficie: 'Sintético', capacidadJugadores: 10, precioPorHora: 25000, activa: true }]);
  });

  it('envía los filtros de cancha y fecha al consultar reservas', () => {
    api.reservas(3, '2026-12-01').subscribe((reservas) => expect(reservas).toHaveLength(0));
    const request = http.expectOne((req) => req.url === '/api/v1/reservas' && req.params.get('canchaId') === '3' && req.params.get('fecha') === '2026-12-01');
    expect(request.request.method).toBe('GET');
    request.flush([]);
  });
});

describe('LoginPage', () => {
  beforeEach(() => TestBed.configureTestingModule({ imports: [LoginPage], providers: [provideRouter([])] }));

  it('valida correo y contraseña antes de habilitar el acceso', () => {
    const fixture = TestBed.createComponent(LoginPage);
    const component = fixture.componentInstance;
    expect(component.form.invalid).toBe(true);
    component.form.setValue({ email: 'admin@cancha.com', password: 'secreto' });
    expect(component.form.valid).toBe(true);
  });
});
