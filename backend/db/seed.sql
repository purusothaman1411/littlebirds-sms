-- Reference data only (subjects + curriculum). Safe to re-run.
-- No users are seeded here: the first HEADMASTER is created by the app on first run (Phase 9)
-- from environment variables, so no password lives in this repository.

USE little_birds_sms;

INSERT IGNORE INTO subjects (name) VALUES
    ('Tamil'), ('English'), ('Mathematics'), ('Science'), ('Social Science'),
    ('Physics'), ('Chemistry'), ('Biology'), ('Computer Science'),
    ('Accountancy'), ('Commerce'), ('Economics'), ('Computer Applications'),
    ('History'), ('Political Science');

-- Standards 1-10 (no group): 5 subjects
INSERT IGNORE INTO curriculum (level, group_name, subject_id)
SELECT 'STD_1_10', 'NONE', id FROM subjects
WHERE name IN ('Tamil', 'English', 'Mathematics', 'Science', 'Social Science');

-- Standards 11-12 : Bio-Maths
INSERT IGNORE INTO curriculum (level, group_name, subject_id)
SELECT 'STD_11_12', 'Bio-Maths', id FROM subjects
WHERE name IN ('Tamil', 'English', 'Physics', 'Chemistry', 'Mathematics', 'Biology');

-- Standards 11-12 : Computer Science
INSERT IGNORE INTO curriculum (level, group_name, subject_id)
SELECT 'STD_11_12', 'Computer Science', id FROM subjects
WHERE name IN ('Tamil', 'English', 'Physics', 'Chemistry', 'Mathematics', 'Computer Science');

-- Standards 11-12 : Commerce
INSERT IGNORE INTO curriculum (level, group_name, subject_id)
SELECT 'STD_11_12', 'Commerce', id FROM subjects
WHERE name IN ('Tamil', 'English', 'Accountancy', 'Commerce', 'Economics', 'Computer Applications');

-- Standards 11-12 : Humanities (5 subjects)
INSERT IGNORE INTO curriculum (level, group_name, subject_id)
SELECT 'STD_11_12', 'Humanities', id FROM subjects
WHERE name IN ('Tamil', 'English', 'History', 'Economics', 'Political Science');
