CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE stadium (
    id            UUID PRIMARY KEY,
    name          VARCHAR(120) NOT NULL,
    city          VARCHAR(120) NOT NULL,
    country       CHAR(2)      NOT NULL,
    resident_club VARCHAR(120),
    location      GEOGRAPHY(POINT, 4326),
    capacity      INTEGER CHECK (capacity > 0)
);

CREATE INDEX stadium_location_idx ON stadium USING GIST (location);
