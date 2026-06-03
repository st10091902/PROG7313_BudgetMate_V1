# 💰 BudgetMate – Personal Budget Tracker App

## 📌 Overview

BudgetMate is a Kotlin Android application developed for **PROG7313 POE Part 3**.

The app helps users manage their personal finances by tracking income, expenses, budget categories, savings goals, monthly min/max spending goals, reports, and cloud-based data storage. The application uses **RoomDB** for local offline storage and **Firebase Firestore** for online database storage.

BudgetMate was tested on both an Android emulator and a physical Android phone.

---

## 🚀 Features

### 🔐 User Authentication

* Register a new user account
* Login using email and password
* Basic validation and error handling
* User details are stored locally using RoomDB

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
    * Camera photo attachment
* Income entries do not require a category
* Transaction data is stored locally using RoomDB
* Transaction data is uploaded to Firebase Firestore

### 📷 Camera Photo Capture

* Users can capture a photo using the phone camera when creating an expense
* The captured photo is previewed in the app
* The photo URI is stored with the transaction entry

### 📁 Budget Categories / Envelopes

* Create budget categories
* Assign a budget amount to each category
* Expense entries are linked to categories
* Category data is stored locally using RoomDB
* Category data is uploaded to Firebase Firestore

### 📋 Expense List

* View saved expense entries
* View expense date, time, description, and amount
* Filter expenses by selected start and end date
* Date selection is done using a date picker

### 📊 Reports

* View total income
* View total expenses
* View net balance
* View total spending per category
* Filter reports by selected date range
* Date selection is done using a date picker
* View a bar graph showing the amount spent per category over the selected period
* View monthly min/max goal status
* Read transaction data back from Firebase Firestore

### 📈 Spending Graph

* Displays category spending in a visual bar graph
* Graph updates based on the selected report date range
* Helps users understand where most of their money is being spent

### 🎯 Savings Goals

* Create savings goals
* Track current saved amount against target amount
* View progress percentage
* Savings goal data is stored locally using RoomDB
* Savings goal data is uploaded to Firebase Firestore

### 🎯 Monthly Min/Max Spending Goals

* Set a minimum monthly spending goal
* Set a maximum monthly spending goal
* View current spending compared to the goal range
* Receive feedback showing whether spending is:
    * Below the minimum goal
    * Within the goal range
    * Above the maximum goal

### ☁️ Firebase Online Storage

BudgetMate uses Firebase Firestore to store app data online.

The app can:

* Write transactions to Firebase
* Write categories to Firebase
* Write savings goals to Firebase
* Read transaction data from Firebase
* Display Firebase transaction data inside the app

Firestore collections used:

```text
transactions
categories
goals
```

### 💾 Local Storage

* Uses RoomDB for local database storage
* Data is stored locally on the Android device
* Local storage allows the app to keep working even before Firebase data is checked

### 🎨 App Icon and Assets

* A custom BudgetMate app icon was added
* The icon uses a finance theme with a wallet and growth graph design
* This improves the professional appearance of the app

### 🔙 Navigation

* Back buttons added to screens for improved user experience
* Home screen navigation connects the main features of the app

### 🛠️ Logging

* Logging added using `Log.d()` for debugging important actions such as registration, login, and transaction saving

---

## 🧱 Tech Stack

* **Language:** Kotlin
* **UI:** XML Layouts
* **Local Database:** RoomDB
* **Online Database:** Firebase Firestore
* **Architecture:** MVVM
* **Android Components:** ViewModel, LiveData
* **Storage:** RoomDB, SharedPreferences, Firebase Firestore
* **Graph:** Custom Canvas-based bar graph
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
├── firebase
│   └── FirebaseService
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

## 🗄️ Database Structure

### RoomDB Local Database

The local database stores:

* Users
* Categories
* Transactions
* Savings goals

### Firebase Firestore Database

The online Firebase database stores:

* Transactions
* Categories
* Savings goals

Firestore collections:

```text
transactions
categories
goals
```

---

## 🔥 Firebase Setup

To use Firebase with BudgetMate:

1. Create a Firebase project in the Firebase Console.
2. Register the Android app using the package name:

   ```text
   com.marcomarais.budgetmate
   ```

3. Download the `google-services.json` file.
4. Place the file inside:

   ```text
   app/google-services.json
   ```

5. Enable Firestore Database in Firebase Console.
6. Run the app and test Firebase read/write functionality.

---

## ▶️ How to Run the App

1. Open the project in Android Studio.
2. Make sure `google-services.json` is inside the `app` folder.
3. Wait for Gradle sync to complete.
4. Run the app on an emulator or physical Android device.
5. Register a new account.
6. Login and start using the app.

---

## 🧪 Manual Testing Completed

The following features were tested manually:

* User registration
* User login
* Adding income
* Adding expenses
* Capturing an expense photo using the camera
* Creating budget categories
* Linking expenses to categories
* Viewing expenses
* Filtering expenses by date range
* Viewing reports
* Filtering reports by date range
* Viewing category totals
* Viewing category spending graph
* Creating savings goals
* Creating monthly min/max spending goals
* Viewing goal status feedback
* Uploading transactions to Firebase
* Uploading categories to Firebase
* Uploading savings goals to Firebase
* Reading transactions from Firebase
* Navigation back to Home screen
* Running the app on an Android emulator
* Running the app on a physical Android phone
* Building the APK successfully

---

## ✅ Part 3 Improvements Added

The following improvements were added for the final POE:

* Firebase Firestore database integration
* Online transaction storage
* Online category storage
* Online savings goal storage
* Firebase read functionality
* Category spending graph
* Min/max monthly spending goal tracking
* Reports showing goal status
* Camera-based photo capture
* Custom app icon
* Date picker and time picker improvements
* Physical phone testing
* Final APK build

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

Paste final demo video link here:

```text
https://www.youtube.com/watch?v=LhN5oMSBW1I
```
---

## 🤖 AI and Code Attribution

AI assistance was used during the development of BudgetMate to support planning, debugging, code correction, Firebase integration, UI improvements, README preparation, and troubleshooting.

The final application was reviewed, tested, and adapted by the student to ensure that the implemented features met the POE requirements.

---

## 👨‍💻 Developer

**Marco Thomas Marais**  
**Student Number:** ST10091902  
**Module:** PROG7313  
**Project:** BudgetMate Personal Budget Tracker

---

## 📚 References

Android Developers. (2026). *Android Developers Documentation*. Available at: https://developer.android.com/.

Firebase. (2026). *Firebase Documentation*. Available at: https://firebase.google.com/docs.

Google. (2026). *Cloud Firestore Documentation*. Available at: https://firebase.google.com/docs/firestore.

JetBrains. (2026). *Kotlin Documentation*. Available at: https://kotlinlang.org/docs/home.html.

OpenAI. (2026). *ChatGPT*. Available at: https://chat.openai.com/.

BudgetBakers. (2022). *Wallet by BudgetBakers*. Available at: https://budgetbakers.com/.

Goodbudget. (2019). *Goodbudget*. Available at: https://goodbudget.com/.

Intuit Mint. (2024). *Mint Budget Tracker and Planner*. Available at: https://mint.intuit.com/.