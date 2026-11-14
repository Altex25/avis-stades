/** Stadium as returned by `GET /api/stadiums`. */
export interface StadiumSummary {
  id: string;
  name: string;
  city: string;
  residentClub: string | null;
}
