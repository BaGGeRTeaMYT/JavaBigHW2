-- Create databases
CREATE DATABASE textscanner_files;
CREATE DATABASE textscanner_analysis;

-- Update collation version for postgres database
\c postgres

-- Connect to textscanner_files and set up extensions if needed
\c textscanner_files

-- Create extensions if needed (uncomment if you need them)
-- CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
-- CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Connect to textscanner_analysis and set up extensions if needed
\c textscanner_analysis

-- Create extensions if needed (uncomment if you need them)
-- CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
-- CREATE EXTENSION IF NOT EXISTS "pgcrypto"; 