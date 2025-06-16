# 📱 Mobile Auction App – Jetpack Compose Android Project

An Android application for an online auction platform where users can list items, place bids, and manage their purchases and sales.  
Built as a complete mobile development exercise using modern Android development tools and architecture.

## ✅ What’s Implemented

- 🔐 **Login / Logout** with JWT authentication
- 🛍️ **View auction items**
  - Filter by category
  - Filter by max price
- ➕ **Add new auction items**
  - Dynamic rules based on account type (Free, Gold, Platinum)
- 📄 **Item details view**
  - See current bids
  - Place a new bid (unless you're the seller)
  - Cancel sale if you’re the seller
- 💸 **View purchases** (items bought)
- 📦 **View sales** (items sold)
- 🔁 **Refresh content** with proper loading and error states
- 📱 **Share item links**
- 💾 **User preferences stored** using SharedPreferences

## 🧰 Tech Stack

- Kotlin
- Jetpack Compose
- Retrofit
- JWT Authentication
- SharedPreferences
- MVVM Architecture
- Material Design 3

## 🚀 How to Run

--bash
# Clone and open in Android Studio
git clone https://github.com/<your-username>/<repo-name>.git

# Build and run on emulator or physical device
