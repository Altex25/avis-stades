import { Component } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';

/** Application shell: skip link, header and main content area. */
@Component({
  imports: [RouterLink, RouterOutlet],
  selector: 'app-root',
  templateUrl: './app.html',
})
export class App {
  /**
   * Moves the keyboard focus to the main content without navigating, because the `#contenu` fragment would be
   * resolved against `<base href="/">` and reload the home page from any other route.
   */
  protected skipToContent(event: Event, content: HTMLElement): void {
    event.preventDefault();
    content.focus();
  }
}
