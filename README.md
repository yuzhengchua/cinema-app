# Cinema Booking Application

This is a simple console-based Cinema Booking Application developed in Java using Maven. It allows users to interact with a cinema booking system via a command-line interface, providing features such as viewing seat maps, booking tickets, and managing bookings.

## 🏗️ Application Architecture

The application follows a modular architecture with clear separation of concerns:

- **`com.yuzhengchua.cinema`** – Entry point (`CinemaApplication.java`) and base configuration.
- **`app`** – Application runner and main execution logic.
- **`service`** – Service layer containing core booking logic and workflow coordination.
- **`models`** – Data models representing domain objects like `SeatMap`.
- **`enums` & `constants`** – Enums for actions and a centralized place for constant values.
- **`util`** – Utility classes like input validation.

## 📌 Assumptions

- The application is intended for command-line usage (no GUI).
- Bookings are managed in-memory (no persistent database used).
- The app is single-user and session-based — all data resets on application restart.
- Assumes a single cinema screen with a fixed seat layout defined in code, i.e each time the application starts, only one cinema can be initialised.

## ⚙️ Environment Requirements

- **Java JDK**: 17 
- **Maven**: 3.8 or newer  
- **Operating System**: Windows 10 or later  

## 🚀 Getting Started

### 1. Go to root of repository
After unzipping the file, go to the root of the project. You should see a file structure similar to below

```cmd
|- src
|- pom.xml
|- README.md
```

### 2. Build the Application

```cmd
mvn clean install
```

### 3. Run the Application
```cmd
java -jar target/cinema-0.0.1-SNAPSHOT.jar
```

### 4. Run Tests
```cmd
mvn test
```

## 📊 Code Quality & Analysis

This project is integrated with **SonarCloud** for continuous code quality analysis and code coverage tracking.

### SonarCloud Configuration

The project includes SonarCloud integration which provides:
- Code quality metrics
- Security vulnerability detection
- Code coverage reports (via JaCoCo)
- Technical debt tracking

**Note:** To enable SonarCloud analysis in GitHub Actions, the following secret must be configured in the repository settings:
- `SONAR_TOKEN` - Your SonarCloud token for authentication

You can manually run SonarCloud analysis locally using:
```cmd
mvn clean verify sonar:sonar -Dsonar.token=YOUR_SONAR_TOKEN
```

### 🛠️ Troubleshooting
Ensure `JAVA_HOME` is properly set and points to JDK 17
```cmd
echo %JAVA_HOME%
```

Check `Java` versions:
```cmd
java -version
mvn -v
```