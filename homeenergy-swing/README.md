# Home Energy Monitoring System

This project is a Home Energy Monitoring System implemented in Java using Swing for the graphical user interface (GUI). The application allows users to manage their energy consumption by registering appliances, estimating bills, and viewing bill history. It separates functionalities for admin and regular users.

## Project Structure

```
homeenergy-swing
├── src
│   └── homeenergy
│       ├── Main.java
│       ├── gui
│       │   ├── MainGUI.java
│       │   ├── AdminPanel.java
│       │   └── UserPanel.java
│       ├── models
│       │   ├── Appliance.java
│       │   ├── EnergyConsumable.java
│       │   ├── HeavyAppliance.java
│       │   ├── LightAppliance.java
│       │   └── User.java
│       ├── services
│       │   ├── AuthService.java
│       │   ├── BillEstimator.java
│       │   └── EnergyManager.java
│       └── utils
│           └── FileUtil.java
└── README.md
```

## Features

- **Admin Panel**: Admin users can register appliances, view appliance data, delete appliances, and view bill history.
- **User Panel**: Regular users can estimate their bills based on registered appliances and view their bill history.

## Setup Instructions

1. **Clone the Repository**: 
   ```
   git clone <repository-url>
   ```

2. **Navigate to the Project Directory**:
   ```
   cd homeenergy-swing
   ```

3. **Compile the Project**:
   Use your preferred IDE or command line to compile the Java files in the `src` directory.

4. **Run the Application**:
   Execute the `Main.java` file to start the application.

## Database Setup

Ensure you have a MySQL database named `homeenergy` with the following schema:

```sql
CREATE TABLE users (
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(50) NOT NULL,
    role VARCHAR(10) NOT NULL
);

CREATE TABLE appliances (
    username VARCHAR(50),
    name VARCHAR(50),
    power INT,
    hours INT,
    is_heavy BOOLEAN,
    FOREIGN KEY (username) REFERENCES users(username)
);

CREATE TABLE bill_history (
    username VARCHAR(50),
    total_energy DOUBLE,
    bill_amount DOUBLE,
    FOREIGN KEY (username) REFERENCES users(username)
);
```

## Usage Guidelines

- Upon launching the application, users will be prompted to register or log in.
- Admin users will have access to additional functionalities for managing appliances.
- Regular users can estimate their bills and view their history.

## Dependencies

- Java Development Kit (JDK) 8 or higher
- Swing library (included in the JDK)

## License

This project is licensed under the MIT License. See the LICENSE file for more details.