# HexVault - Split Inventory Management System

HexVault is a Spring Boot application designed to help manage inventory and customer information.

## Features

- **Device Management**: Track device repairs
- **Inventory Management**: Keep track of parts and SKUs in your inventory
- **Purchase Order System**: Create and manage purchase orders for new parts
- **Customer Management**: Store and retrieve customer information
- **User Authentication**: Secure login and registration for staff
- **Notes System**: Add and track notes for each repair job

## Requirements

- Java 17 or higher
- Maven
- Xdata

## Setup and Running

1. **Clone the repository**
   ```bash
   git clone https://github.com/MxAriadne/HexVault.git
   cd hexvault
   ```

2. **Configure database**
   
   TODO

3. **Build the application**
   ```bash
   mvn package
   ```

4. **Run the application**
   ```bash
   java ???
   ```
   
   Alternatively, you can use Spring Boot Maven plugin:
   ```bash
   mvn spring-boot:run
   ```

5. **Access the application**
   
   Navigate to `http://localhost:8080` in your web browser

## Project Structure

The application follows a standard Spring MVC structure:

- `controllers`: Handle HTTP requests and direct traffic
- `entities`: JPA entities representing database tables
- `repos`: Spring Data repositories for database operations
