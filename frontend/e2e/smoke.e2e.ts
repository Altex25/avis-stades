import AxeBuilder from '@axe-core/playwright';
import { expect, test } from '@playwright/test';

test.describe('single deployable', () => {
  test('serves the PWA, which reads the stadium catalogue from the API', async ({ page }) => {
    const catalogue = page.waitForResponse('**/api/stadiums');

    await page.goto('./');

    expect((await catalogue).status()).toBe(200);
    await expect(page).toHaveTitle('Accueil · Avis Stades');
    await expect(page.locator('html')).toHaveAttribute('lang', 'fr');
    await expect(page.getByRole('heading', { level: 1 })).toHaveText('Avis Stades');
    await expect(page.getByRole('status')).toContainText(/référencés?\./);
  });

  test('offers a skip link as the first keyboard stop', async ({ page }) => {
    await page.goto('./');

    await page.keyboard.press('Tab');
    const skipLink = page.getByRole('link', { name: 'Aller au contenu' });
    await expect(skipLink).toBeFocused();
    await skipLink.press('Enter');
    await expect(page.getByRole('main')).toBeFocused();
    await expect(page).toHaveURL(/\/avis-stades\/$/);
  });

  test('has no WCAG A or AA violation detected by axe', async ({ page }) => {
    await page.goto('./');
    await expect(page.getByRole('status')).toContainText(/référencé/);

    const results = await new AxeBuilder({ page })
      .withTags(['wcag2a', 'wcag2aa', 'wcag21a', 'wcag21aa', 'wcag22aa'])
      .analyze();

    expect(results.violations).toEqual([]);
  });

  test('exposes only the health probe of Actuator', async ({ request }) => {
    const health = await request.get('actuator/health');
    expect(health.status()).toBe(200);
    expect(await health.json()).toMatchObject({ status: 'UP' });

    expect((await request.get('actuator/env')).status()).toBe(403);
  });
});
