# Todo App

A full-featured Todo List Application built with Java Spring Boot, Thymeleaf, and Docker.

## Features

- ✅ User Registration
- ✅ User Authentication (Login/Logout)
- ✅ Forgot Password with Email Reset
- ✅ Create, Read, Update, Delete Todos
- ✅ Mark Todos as Complete/Incomplete
- ✅ User-specific Todo Management
- ✅ Modern, Responsive UI with Bootstrap

## Technologies

- **Backend**: Spring Boot 3.2.0
- **Security**: Spring Security
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA / Hibernate
- **Templating**: Thymeleaf
- **Build Tool**: Maven
- **Containerization**: Docker & Docker Compose

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker and Docker Compose (for containerized deployment)
- PostgreSQL (if running without Docker)

## Setup Instructions

### Option 1: Using Docker Compose (Recommended)

1. **Clone the repository** (if applicable)

2. **Configure Email Settings** (for forgot password functionality):
   - Edit `src/main/resources/application.properties`
   - Update the following properties:
     ```properties
     spring.mail.username=your-email@gmail.com
     spring.mail.password=your-app-password
     ```
   - For Gmail, you'll need to generate an [App Password](https://support.google.com/accounts/answer/185833)

3. **Build and Run with Docker Compose**:
   ```bash
   docker-compose up --build
   ```

   The application will be available at: `http://localhost:8080`

### Option 2: Local Development

1. **Start PostgreSQL Database**:
   ```bash
   # Using Docker
   docker run -d --name todoapp-postgres \
     -e POSTGRES_DB=todoapp \
     -e POSTGRES_USER=postgres \
     -e POSTGRES_PASSWORD=postgres \
     -p 5432:5432 \
     postgres:15-alpine
   ```

2. **Configure Database and Email**:
   - Edit `src/main/resources/application.properties`
   - Update database connection if needed
   - Configure email settings for password reset

3. **Build the Application**:
   ```bash
   mvn clean package
   ```

4. **Run the Application**:
   ```bash
   mvn spring-boot:run
   ```

   Or run the JAR:
   ```bash
   java -jar target/todoapp-1.0.0.jar
   ```

## Usage

1. **Register a New Account**:
   - Navigate to `http://localhost:8080/register`
   - Fill in username, email, and password
   - Click "Register"

2. **Login**:
   - Go to `http://localhost:8080/login`
   - Enter your credentials
   - You'll be redirected to your todos page

3. **Forgot Password**:
   - Click "Forgot Password?" on the login page
   - Enter your email address
   - Check your email for the reset link
   - Click the link and set a new password

4. **Manage Todos**:
   - Create new todos with title and optional description
   - Mark todos as complete/incomplete
   - Edit todos
   - Delete todos

## Docker Commands

```bash
# Build and start all services
docker-compose up --build

# Start in detached mode
docker-compose up -d

# Stop all services
docker-compose down

# Stop and remove volumes (clears database)
docker-compose down -v

# View logs
docker-compose logs -f app

# Rebuild only the app
docker-compose build app
docker-compose up -d app
```

## Project Structure

```
todoappmathproject/
├── src/
│   ├── main/
│   │   ├── java/com/todoapp/
│   │   │   ├── config/          # Security configuration
│   │   │   ├── controller/      # REST controllers
│   │   │   ├── entity/          # JPA entities
│   │   │   ├── repository/      # Data repositories
│   │   │   ├── service/         # Business logic
│   │   │   └── TodoAppApplication.java
│   │   └── resources/
│   │       ├── templates/       # Thymeleaf templates
│   │       └── application.properties
├── Dockerfile
├── docker-compose.yml
└── pom.xml
```

## Configuration

### Email Configuration

For the forgot password feature to work, configure your email settings in `application.properties`:

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### Database Configuration

Default database settings:
- Database: `todoapp`
- Username: `postgres`
- Password: `postgres`
- Port: `5432`

## Security

- Passwords are encrypted using BCrypt
- Spring Security handles authentication and authorization
- User-specific todo access (users can only see/edit their own todos)
- CSRF protection enabled
- Password reset tokens expire after 24 hours

## Troubleshooting

### Email Not Sending

- Verify email credentials in `application.properties`
- For Gmail, ensure you're using an App Password, not your regular password
- Check firewall/network settings

### Database Connection Issues

- Ensure PostgreSQL is running
- Verify connection credentials in `application.properties`
- Check if port 5432 is available

### Docker Issues

- Ensure Docker and Docker Compose are installed and running
- Check logs: `docker-compose logs`
- Try rebuilding: `docker-compose up --build`

## License

This project is open source and available for educational purposes.

