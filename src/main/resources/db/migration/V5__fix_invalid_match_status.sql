-- V2 seeded one match with the status 'COMPLETED', a value that never existed in
-- the MatchStatus enum (SCHEDULED, LIVE, FINISHED, CANCELLED). Listing matches
-- failed with a 500 as soon as Hibernate tried to map that row.
--
-- V2 itself cannot be edited: Flyway stores a checksum for every applied
-- migration, so the correction is made forward.

UPDATE matches SET status = 'FINISHED' WHERE status = 'COMPLETED';
