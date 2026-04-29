# 💰 BudgetMate – Personal Budget Tracker App

## 📌 Overview

BudgetMate is a Kotlin Android application developed for **PROG7313 POE Part 2**.
The app helps users manage their personal finances by tracking income, expenses, budgets, savings goals, and spending reports.

---

## 🚀 Features

### 🔐 User Authentication

* Register a new user account
* Login using email and password
* Basic validation and error handling

### 💸 Transactions

* Add income transactions
* Add expense transactions
* Expense entries include:

    * Amount
    * Description
    * Category
    * Date
    * Start time
    * End time
    * Photo attachment
* Income entries do not require a category

### 📁 Budget Categories / Envelopes

* Create budget categories
* Assign a budget amount to each category
* Expense entries are linked to categories

### 📋 Expense List

* View saved expense entries
* View expense date, time, description, and amount
* Filter expenses by selected start and end date

### 📊 Reports

* View total income
* View total expenses
* View net balance
* View total spending per category
* Filter reports by selected date range

### 🎯 Savings Goals

* Create savings goals
* Track current saved amount against target amount
* View progress percentage

### 💾 Local Storage

* Uses RoomDB for local database storage
* Data is stored locally on the Android device

### 🔙 Navigation

* Back buttons added to screens for improved user experience

### 🛠️ Logging

* Logging added using `Log.d()` for debugging important actions such as registration, login, and transaction saving

---

## 🧱 Tech Stack

* **Language:** Kotlin
* **UI:** XML Layouts
* **Database:** RoomDB
* **Architecture:** MVVM
* **Android Components:** ViewModel, LiveData
* **IDE:** Android Studio
* **Version Control:** Git and GitHub

---

## 📂 Project Structure

```text
com.marcomarais.budgetmate
│
├── data
│   ├── dao
│   ├── entities
│   └── BudgetMateDatabase
│
├── repository
│
├── viewmodel
│
├── ui
│   ├── auth
│   ├── home
│   ├── expenses
│   ├── budgets
│   ├── goals
│   └── reports
```

---

## ▶️ How to Run the App

1. Open the project in Android Studio.
2. Wait for Gradle sync to complete.
3. Run the app on an emulator or physical Android device.
4. Register a new account.
5. Login and start using the app.

---

## 🧪 Manual Testing Completed

The following features were tested manually:

* User registration
* User login
* Adding income
* Adding expenses
* Adding expense photo
* Creating budget categories
* Linking expenses to categories
* Viewing expenses
* Filtering expenses by date range
* Viewing reports
* Filtering reports by date range
* Viewing category totals
* Creating savings goals
* Navigation back to Home screen

---

## 📦 APK Build Instructions

To generate the APK:

1. In Android Studio, go to:

   ```text
   Build > Build Bundle(s) / APK(s) > Build APK(s)
   ```

2. After the build completes, locate the APK at:

   ```text
   app/build/outputs/apk/debug/app-debug.apk
   ```

---

## 🎥 Demo Video

Paste demo video link here:

```text
https://youtu.be/pE2ExIEbLls?si=bJvqEyBwtIEjEBCG
```

---

## 👨‍💻 Developer

**Marco Thomas Marais**
**Student Number:** ST10091902
**Module:** PROG7313
**Project:** BudgetMate Personal Budget Tracker

---

## 📚 References

Android Developers. (2019). Android Design Guidelines.
BudgetBakers. (2022). Wallet by BudgetBakers.
Goodbudget. (2019). Home.
Mint. (2024). Budget Tracker & Planner.