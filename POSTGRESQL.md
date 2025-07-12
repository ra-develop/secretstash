Setup the PostgreSQL RDBMS for the project
==========================================

### Option 1: Install via Homebrew (Recommended)

1.  **Install Homebrew** (if you don't have it):

```bash

/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```

2.  **Install PostgreSQL**:

```bash

brew install postgresql
````

3.  **Start PostgreSQL service**:

```bash

brew services start postgresql
```

    (This will auto-start PostgreSQL on boot)

4.  **Verify installation**:

```bash

psql --version
```

5.  **Access PostgreSQL**:

```bash

psql postgres
```


### Option 2: Install PostgreSQL.app (GUI)

1.  Download from [PostgreSQL.app](https://postgresapp.com/)

2.  Drag to Applications folder

3.  Double-click to start the server

4.  Click "Open psql" to access the CLI


### Basic PostgreSQL Commands

Once connected (`psql postgres`):

Command

Description

`\l`

List databases

`\c database_name`

Connect to database

`\dt`

List tables

`\du`

List users

`\q`

Quit psql

### Create Database and User for Your App

```sql

CREATE DATABASE secretstash;
CREATE USER stashuser WITH PASSWORD 'yourpassword';
GRANT ALL PRIVILEGES ON DATABASE secretstash TO stashuser;
```

### Configure Your Spring Boot Application

Update `application.properties`:

```properties

spring.datasource.url=jdbc:postgresql://localhost:5432/secretstash
spring.datasource.username=stashuser
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
```

### Troubleshooting Tips

1.  **If you get "connection refused"**:

    bash

    brew services restart postgresql

2.  **Reset password**:

    sql

    ALTER USER postgres WITH PASSWORD 'newpassword';

3.  **Check if PostgreSQL is running**:

    bash

    brew services list

4.  **Default credentials**:

    *   User: `postgres`

    *   Password: Usually empty, or `postgres` if you set one


### Useful Tools

1.  **pgAdmin** (GUI):

```bash

brew install --cask pgadmin4
```

2.  **Alternative CLI tools**:

    *   `psql` (included)

    *   `DBeaver` (multi-database GUI)


### Uninstalling

```bash

brew services stop postgresql
brew uninstall postgresql
rm -rf /usr/local/var/postgres
```

This setup gives you a complete PostgreSQL environment ready for your Kotlin Spring Boot application. The Homebrew method is preferred as it's easier to manage and update.