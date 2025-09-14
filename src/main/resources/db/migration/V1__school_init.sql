-- 1) Enum
create type enrollment_status as enum ('ENROLLED', 'COMPLETED', 'DROPPED');

-- 2) Users
create table users (
    id uuid primary key,
    email varchar(255) unique not null,
    password varchar(255) not null,
    full_name varchar(255) not null,
    roles varchar(255) not null,
    created_at timestamptz not null default now()
);

-- 3) Courses
create table courses (
    id uuid primary key,
    code varchar(50) unique not null,
    title varchar(255) not null,
    description text,
    capacity int not null check (capacity > 0),
    teacher_id uuid not null references users(id),
    created_at timestamptz not null default now()
);

-- 4) Enrollments
create table enrollments (
    id uuid primary key,
    student_id uuid not null references users(id),
    course_id uuid not null references courses(id),
    status enrollment_status not null default 'ENROLLED'::enrollment_status,
    enrolled_at timestamptz not null default now(),
    dropped_at timestamptz,
    completed_at timestamptz,
    unique (student_id, course_id)
);

-- 5) Grades
create table grades (
    id uuid primary key,
    enrollment_id uuid not null references enrollments(id) on delete cascade,
    numeric_grade numeric(5,2) check (numeric_grade >= 0 and numeric_grade <= 100),
    letter_grade varchar(2),
    graded_at timestamptz not null default now(),
    grader_id uuid not null references users(id)
);

-- 6) Indexes
create index enrollments_course_idx
  on enrollments(course_id)
  where status = 'ENROLLED'::enrollment_status;

create index courses_teacher_idx on courses(teacher_id);
