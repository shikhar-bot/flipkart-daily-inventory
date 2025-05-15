# 🛒 Flipkart Daily Inventory - Thinkify.io Assignment

This is a Java-based console application built as a **mock assignment for Thinkify.io labs**. It simulates Flipkart’s planned service to deliver groceries 
and daily essentials by providing a way for users to **browse inventory** using filters and sorting.

---

## ✅ Features

- Add new grocery items with brand, category, and price
- Add stock to existing items
- Search inventory with filters:
  - By Brand
  - By Category
  - By Price Range
  - Any combination of the above
- Sort search results:
  - By Price (ascending / descending)
  - By Quantity (ascending / descending)
- Extensible design
- Clean service and model separation
- Custom exception handling for errors like:
  - Duplicate item
  - Non-existing item
  - Invalid quantity/price

---

## 🏗️ Tech Stack

- Java 8+
- No external libraries
- No database (pure in-memory)

---

## 🚀 How to Run

1. Clone this repository:
   ```bash
   git clone https://github.com/shikhar-bot/flipkart-daily-inventory.git
   cd flipkart-daily-inventory

2. Compile the code:
   javac Main.java
   
3. Run the program:
   java Main
