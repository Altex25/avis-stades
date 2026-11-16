import { Component } from '@angular/core';
import { httpResource } from '@angular/common/http';
import { StadiumSummary } from '../stadiums/stadium-summary';

/** Home page: presents the platform and the stadiums already referenced. */
@Component({
  selector: 'app-home',
  templateUrl: './home.html',
})
export class Home {
  protected readonly stadiums = httpResource<StadiumSummary[]>(() => 'api/stadiums', {
    defaultValue: [],
  });

  /** Resident club shown after the city, in parentheses, or nothing when the stadium has none. */
  protected club(stadium: StadiumSummary): string {
    return stadium.residentClub ? ` (${stadium.residentClub})` : '';
  }
}
