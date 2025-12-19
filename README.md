# 🏦 Osryn Bank – Bank Management System

## 👋 Welcome

Welcome to **Osryn Bank**! This project is our take on what a modern banking experience *should* feel like. The goal was not only functionality, but also visual appeal.

We blended futuristic security layers (because logging in should feel cool) with a clean, professional interface. This is a **single-file Java Swing application** designed to demonstrate how far Swing styling can be pushed with proper design and structure.

---

## ✨ Features

### 🔐 Security Meets Style

* **Themed Login System**
  A dark-mode interface with neon accents that feels like a secure terminal rather than a boring form.
* **Input Validation**
  Ensures smooth data entry and catches errors early.
* **Easy User Registration**
  New users can register instantly and receive a unique **9-digit Account ID**.

---

### 📊 Financial Dashboard

* **Live Account Overview**
  Displays Account ID, account status, and current balance (BDT ৳) at a glance.
* **Dynamic Balance Graph**
  A custom-rendered graph showing balance history over time using Java 2D.
* **Activity Feed**
  Auto-refreshing transaction table tracking deposits, withdrawals, transfers, and bill payments.

---

### 💸 Banking Operations

* **Deposit & Withdraw**
  Instant balance updates with validation.
* **Wire Transfer**
  Securely send money to other users within the system.
* **Utility Bill Payments**
  Pay Electricity, Internet, and Mobile bills with receipt notifications.

---

### ⚙️ System Utilities

* **Account Settings**
  Update passwords and security credentials.
* **High Performance**
  Uses an efficient in-memory data structure for fast operations.
  *(Note: Data resets when the application is closed—ideal for testing and demos.)*

---

## 👥 Team Members

* **Abdullah Al Fahim** – Project Lead & System Architect
  *(System architecture, model design, documentation, Git management)*
* **Md Sumon Hossain Pra** – Backend Developer
  *(Business logic, controllers, search system, OOP implementation, Git management)*
* **Md Rayhan Ali** – Frontend Developer
  *(Complete GUI design using Java Swing)*
* **Md. Shamim Reza** – Integration Specialist
* **Md. Abdul Motin** – Quality Assurance Lead
  *(Testing and validation)*

---

## 🛠️ Tech Stack

* **Language:** Java (JDK 8+)
* **Framework:** Swing & AWT
* **Graphics:** Java 2D (custom graphs, rounded buttons, UI effects)
* **Architecture:** MVC-inspired design using `CardLayout` for smooth navigation

---

## 🚀 How to Run

### Prerequisites

* Java Development Kit (JDK) 8 or higher

### Steps

1. **Download the Source**
   Save the file as:

   ```
   BankManagementSystem.java
   ```

2. **Compile**

   ```bash
   javac BankManagementSystem.java
   ```

3. **Run**

   ```bash
   java BankManagementSystem
   ```

---

## 📖 Quick Start Guide

### 🔑 Admin/Test Login

To explore the system without registering:

* **Username:** `admin`
* **Password:** `admin`

### 🆕 Create a New Account

1. Click **NEW USER REGISTRATION** on the login screen.
2. Enter your details and choose a unique username.
3. Receive a **9-digit Account ID** upon successful registration.
4. Log in and start using the system.

---

## 🤝 Contributing

Contributions are welcome!

1. Fork the repository
2. Create a new branch

   ```bash
   git checkout -b feature/YourFeatureName
   ```
3. Commit your changes
4. Push to your branch
5. Open a Pull Request with a clear description

---

## 📄 License

This project is open-source and licensed under the **MIT License**.

You are free to use, modify, and learn from this project.

---

