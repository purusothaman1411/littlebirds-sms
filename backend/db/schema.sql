-- Little Birds School Management System: MySQL 8.0.16+ schema (Phase 3)
-- Run on an empty database:  mysql -u root -p < backend/db/schema.sql
-- Then load reference data:  mysql -u root -p < backend/db/seed.sql

CREATE DATABASE IF NOT EXISTS little_birds_sms
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;   -- case-insensitive, so IDs match like the console app's equalsIgnoreCase

USE little_birds_sms;

-- ---------------------------------------------------------------
-- subjects: every subject name used anywhere in the curriculum
-- ---------------------------------------------------------------
CREATE TABLE subjects (
    id    BIGINT       NOT NULL AUTO_INCREMENT,
    name  VARCHAR(50)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_subjects_name UNIQUE (name)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------
-- curriculum: which subjects a student takes (replaces the hardcoded
-- getSubjects() in MarksOperations)
--   level       STD_1_10 or STD_11_12
--   group_name  'NONE' for standards 1-10 (sentinel, so the unique key works;
--               MySQL treats NULLs as distinct in unique indexes)
-- ---------------------------------------------------------------
CREATE TABLE curriculum (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    level       VARCHAR(10)  NOT NULL,
    group_name  VARCHAR(30)  NOT NULL DEFAULT 'NONE',
    subject_id  BIGINT       NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_curriculum UNIQUE (level, group_name, subject_id),
    CONSTRAINT fk_curriculum_subject FOREIGN KEY (subject_id) REFERENCES subjects (id),
    CONSTRAINT ck_curriculum_level CHECK (level IN ('STD_1_10', 'STD_11_12')),
    CONSTRAINT ck_curriculum_group CHECK (
        (level = 'STD_1_10'  AND group_name = 'NONE') OR
        (level = 'STD_11_12' AND group_name IN ('Bio-Maths', 'Computer Science', 'Commerce', 'Humanities'))
    )
) ENGINE=InnoDB;

-- ---------------------------------------------------------------
-- staff_users: login accounts (from staff.txt). Password is a BCrypt hash.
-- class_range / group_name are stored as in the console app but not enforced yet.
-- ---------------------------------------------------------------
CREATE TABLE staff_users (
    staff_id       VARCHAR(20)  NOT NULL,
    name           VARCHAR(100) NOT NULL,
    username       VARCHAR(50)  NOT NULL,
    password_hash  VARCHAR(100) NOT NULL,
    role           VARCHAR(20)  NOT NULL,
    class_range    VARCHAR(10)  NOT NULL DEFAULT 'ALL',
    group_name     VARCHAR(30)  NULL,
    created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (staff_id),
    CONSTRAINT uq_staff_username UNIQUE (username),
    CONSTRAINT ck_staff_role CHECK (role IN ('HEADMASTER', 'SUBJECT_STAFF', 'WORKING_STAFF', 'MANAGEMENT_STAFF')),
    CONSTRAINT ck_staff_class_range CHECK (class_range IN ('ALL', '1-10', '11-12'))
) ENGINE=InnoDB;

-- subjects assigned to a staff member (many-to-many)
CREATE TABLE staff_subjects (
    staff_id    VARCHAR(20) NOT NULL,
    subject_id  BIGINT      NOT NULL,
    PRIMARY KEY (staff_id, subject_id),
    CONSTRAINT fk_staffsub_staff   FOREIGN KEY (staff_id)   REFERENCES staff_users (staff_id) ON DELETE CASCADE,
    CONSTRAINT fk_staffsub_subject FOREIGN KEY (subject_id) REFERENCES subjects (id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------
-- students. Age is NOT stored: it is computed from dob.
-- group_name is NULL for standards 1-10 and required for 11-12.
-- ---------------------------------------------------------------
CREATE TABLE students (
    student_id      VARCHAR(20)  NOT NULL,
    name            VARCHAR(100) NOT NULL,
    dob             DATE         NOT NULL,
    gender          VARCHAR(10)  NOT NULL,
    standard        INT          NOT NULL,
    group_name      VARCHAR(30)  NULL,
    address         VARCHAR(255) NOT NULL,
    contact_number  VARCHAR(10)  NOT NULL,
    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (student_id),
    CONSTRAINT ck_student_gender   CHECK (gender IN ('Male', 'Female', 'Other')),
    CONSTRAINT ck_student_standard CHECK (standard BETWEEN 1 AND 12),
    CONSTRAINT ck_student_group    CHECK (
        (standard <= 10 AND group_name IS NULL) OR
        (standard >= 11 AND group_name IN ('Bio-Maths', 'Computer Science', 'Commerce', 'Humanities'))
    ),
    CONSTRAINT ck_student_contact  CHECK (contact_number REGEXP '^[6-9][0-9]{9}$')
) ENGINE=InnoDB;

CREATE INDEX idx_students_standard ON students (standard);
CREATE INDEX idx_students_name     ON students (name);

-- ---------------------------------------------------------------
-- teachers (profile records; separate from staff_users login accounts)
-- ---------------------------------------------------------------
CREATE TABLE teachers (
    teacher_id      VARCHAR(20)  NOT NULL,
    name            VARCHAR(100) NOT NULL,
    dob             DATE         NOT NULL,
    gender          VARCHAR(10)  NOT NULL,
    qualification   VARCHAR(100) NOT NULL,
    email           VARCHAR(100) NOT NULL,
    contact_number  VARCHAR(10)  NOT NULL,
    address         VARCHAR(255) NOT NULL,
    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (teacher_id),
    CONSTRAINT ck_teacher_gender  CHECK (gender IN ('Male', 'Female', 'Other')),
    CONSTRAINT ck_teacher_contact CHECK (contact_number REGEXP '^[6-9][0-9]{9}$')
) ENGINE=InnoDB;

-- teacher subjects stay free text, as in the console app (no FK to subjects)
CREATE TABLE teacher_subjects (
    teacher_id    VARCHAR(20) NOT NULL,
    subject_name  VARCHAR(50) NOT NULL,
    PRIMARY KEY (teacher_id, subject_name),
    CONSTRAINT fk_teachersub_teacher FOREIGN KEY (teacher_id) REFERENCES teachers (teacher_id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_teacher_subjects_name ON teacher_subjects (subject_name);

-- ---------------------------------------------------------------
-- marks: one row per student per subject. Deleting a student deletes marks.
-- ---------------------------------------------------------------
CREATE TABLE marks (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    student_id  VARCHAR(20)  NOT NULL,
    subject_id  BIGINT       NOT NULL,
    mark        INT          NOT NULL,
    entered_by  VARCHAR(20)  NULL,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uq_marks_student_subject UNIQUE (student_id, subject_id),
    CONSTRAINT fk_marks_student FOREIGN KEY (student_id) REFERENCES students (student_id) ON DELETE CASCADE,
    CONSTRAINT fk_marks_subject FOREIGN KEY (subject_id) REFERENCES subjects (id),
    CONSTRAINT fk_marks_staff   FOREIGN KEY (entered_by) REFERENCES staff_users (staff_id) ON DELETE SET NULL,
    CONSTRAINT ck_marks_range   CHECK (mark BETWEEN 0 AND 100)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------
-- attendance: one row per student per date. Deleting a student deletes it.
-- "No future dates" is enforced in the service (CHECK cannot use CURDATE()).
-- ---------------------------------------------------------------
CREATE TABLE attendance (
    id               BIGINT      NOT NULL AUTO_INCREMENT,
    student_id       VARCHAR(20) NOT NULL,
    attendance_date  DATE        NOT NULL,
    status           VARCHAR(1)  NOT NULL,
    marked_by        VARCHAR(20) NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_attendance_student_date UNIQUE (student_id, attendance_date),
    CONSTRAINT fk_attendance_student FOREIGN KEY (student_id) REFERENCES students (student_id) ON DELETE CASCADE,
    CONSTRAINT fk_attendance_staff   FOREIGN KEY (marked_by)  REFERENCES staff_users (staff_id) ON DELETE SET NULL,
    CONSTRAINT ck_attendance_status  CHECK (status IN ('P', 'A'))
) ENGINE=InnoDB;

CREATE INDEX idx_attendance_date ON attendance (attendance_date);
