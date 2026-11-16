import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { Home } from './home';
import { StadiumSummary } from '../stadiums/stadium-summary';

describe('Home', () => {
  let http: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Home],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  async function render(answer: (request: ReturnType<HttpTestingController['expectOne']>) => void) {
    const fixture = TestBed.createComponent(Home);
    fixture.detectChanges();
    answer(http.expectOne('api/stadiums'));
    await fixture.whenStable();
    fixture.detectChanges();
    return fixture.nativeElement as HTMLElement;
  }

  it('has a single level-one heading', async () => {
    const page = await render((request) => request.flush([]));

    expect(page.querySelectorAll('h1')).toHaveLength(1);
  });

  it('lists the stadiums returned by the API', async () => {
    const stadiums: StadiumSummary[] = [
      { id: '1', name: 'Stade Bollaert-Delelis', city: 'Lens', residentClub: 'RC Lens' },
      { id: '2', name: 'Stade Pierre-Mauroy', city: "Villeneuve-d'Ascq", residentClub: null },
    ];

    const page = await render((request) => request.flush(stadiums));

    const items = Array.from(page.querySelectorAll('li')).map((item) => item.textContent?.trim());
    expect(items).toEqual([
      'Stade Bollaert-Delelis, Lens (RC Lens)',
      "Stade Pierre-Mauroy, Villeneuve-d'Ascq",
    ]);
    expect(page.querySelector('[role="status"]')?.textContent).toContain('2 stades référencés.');
  });

  it('uses the singular for a single stadium', async () => {
    const page = await render((request) =>
      request.flush([
        { id: '1', name: 'Stade Bollaert-Delelis', city: 'Lens', residentClub: null },
      ]),
    );

    expect(page.querySelector('[role="status"]')?.textContent).toContain('1 stade référencé.');
  });

  it('says when no stadium is referenced yet', async () => {
    const page = await render((request) => request.flush([]));

    expect(page.querySelector('[role="status"]')?.textContent).toContain(
      "Aucun stade n'est encore référencé.",
    );
    expect(page.querySelector('ul')).toBeNull();
  });

  it('announces a loading failure', async () => {
    const page = await render((request) =>
      request.flush('', { status: 503, statusText: 'Service Unavailable' }),
    );

    expect(page.querySelector('[role="status"]')?.textContent).toContain(
      "Les stades n'ont pas pu être chargés.",
    );
  });
});
