# Finance Manager

A simple Java desktop application for managing personal finance transactions.

The project started as a console-based Java + MySQL application and now includes a Java Swing GUI. It allows users to add, view, search, filter, update, delete, and calculate totals for income and expense transactions.

## Features

- Add income and expense transactions
- View all transactions in a table
- Search transactions by keyword
- Filter transactions by type, account, category, description, and mode
- Update selected transactions
- Delete selected transactions
- Calculate filtered totals in the Reports tab
- Desktop launcher using `run-finance-gui.bat`

## Tech Stack

- Java
- Java Swing
- JDBC
- MySQL

## Project Structure

```text
Finance Application/
├── lib/
│   └── mysql-connector-j-9.7.0/
├── src/
│   ├── DBconnection.java
│   ├── FinanceManagerGUI.java
│   ├── Main.java
│   └── Transaction.java
├── run-finance-gui.bat
└── README.md
```

## Database Setup

The application connects to a local MySQL database named `finance_app`.

Expected database connection:

```java
jdbc:mysql://localhost:3306/finance_app
```

The application expects a table named `transactions`.

Example table:

```sql
CREATE DATABASE finance_app;

USE finance_app;

CREATE TABLE transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    type VARCHAR(20),
    amount DOUBLE,
    account VARCHAR(50),
    category VARCHAR(50),
    description VARCHAR(255),
    mode VARCHAR(20)
);
```

## How To Run

Make sure MySQL is running before starting the application.

### Option 1: Run From VS Code

1. Open the project folder in VS Code.
2. Open `src/FinanceManagerGUI.java`.
3. Click `Run`.

### Option 2: Run With Desktop Launcher

Double-click:

```text
run-finance-gui.bat
```

This script compiles the Java files and opens the Swing GUI.

## GUI Tabs

### Add Transaction

Used to add a new transaction with:

- Type
- Amount
- Account
- Category
- Description
- Mode

### View and Edit

Used to:

- View all saved transactions
- Search transactions
- Apply filters
- Select a transaction from the table
- Update or delete the selected transaction

### Reports

Used to calculate totals using filters.

Example:

```text
Type: expense
Description: Blinkit
Mode: online
```

This shows the total amount spent on online Blinkit expenses and displays the matching rows in a table.

## Notes

- MySQL must be running in the background for the app to work.
- The MySQL connector `.jar` should be inside the `lib` folder.
- Do not upload real database passwords publicly.
- `Main.java` still contains the original console-based menu.
- `FinanceManagerGUI.java` is the main file for the desktop GUI.

Author
Shagnik Bhattacharjee
