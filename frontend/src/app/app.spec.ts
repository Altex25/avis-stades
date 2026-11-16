import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { App } from './app';

describe('App', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [provideRouter([])],
    }).compileComponents();
  });

  it('starts with a skip link to the main content', async () => {
    const fixture = TestBed.createComponent(App);
    await fixture.whenStable();
    const page = fixture.nativeElement as HTMLElement;

    const skipLink = page.querySelector('a');
    expect(skipLink?.textContent?.trim()).toBe('Aller au contenu');
    expect(skipLink?.getAttribute('href')).toBe('#contenu');
    expect(page.querySelector('main#contenu')).not.toBeNull();
  });

  it('moves the focus to the main content without navigating', async () => {
    const fixture = TestBed.createComponent(App);
    await fixture.whenStable();
    const page = fixture.nativeElement as HTMLElement;
    document.body.appendChild(page);

    const click = new MouseEvent('click', { bubbles: true, cancelable: true });
    page.querySelector('a')!.dispatchEvent(click);

    expect(click.defaultPrevented).toBe(true);
    expect(document.activeElement).toBe(page.querySelector('main'));
    page.remove();
  });
});
