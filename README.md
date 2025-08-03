# Off the Beaten Path

Discover and share hidden gems — local landmarks, forgotten monuments, and untold stories — through an Android app and companion web platform.

# Introduction

Off the Beaten Path is an Android app and web platform designed to help users explore lesser-known locations like abandoned buildings, sculptures, or legendary local sites. Users can upload and discover Points of Interest (POIs), creating a community-driven map of hidden treasures.

# Features
 * Google Maps integration for POI visualization

 * Add new POIs with name, category, region, and coordinates

 * Filter POIs by category and region

 * Edit and delete owned POIs

 * User registration, login, and profile management

 * Auth token-based session management

 * REST API integration using Retrofit

 * Multilingual support (English/Hungarian) – language persistence not yet implemented

 * Admin features for moderating content

# Installation
### Prerequisites
* Android Studio Koala (2024.1.2)

* Android SDK 35 (min SDK 28)

* Kotlin 1.9.0

* Google Maps API Key

### Android Studio

1. Open the project in Andriod Studio
```bash
git clone https://github.com/Krisztian0801/Off-the-Beaten-Path.git
```
2. Add your API key in `local.properties`:

```ini
MAPS_API_KEY=your_key_here
```
3. Connect a device or emulator.

4. Run the project:  `Run > Run 'app'`

### APK Installation
1. Download the `.apk` file.
2. Transfer it to your Android device.
3. Open the file and allow "Install from unknown sources".
4.  Launch the app from your home screen.

#

### Usage
Key Screens
* Login/Register: Email/password-based authentication.
* Home Screen: POIs listed with filtering options.
* Map View: Visual POI markers on Google Maps.
* POI Details: View POI name, description, category, region.
* Add/Edit/Delete POIs: Modify personal entries.
* Profile Management: Update info, reset password, delete account.

#

### Architecture
* MVVM pattern with LiveData
* PHP REST API + MySQL backend
* RecyclerViews + Fragments for screen organization
* Manual network handling via Retrofit & OkHttp

#

### Known Issues
* Language switch is not persistent (no SharedPreferences)
* Rating system is not implemented
* Profile pictures not displayed
* Image upload for POIs not implemented
#
### Contributors
* [Krisztián Bordács](https://github.com/Krisztian0801)
* [Melinda Csík](https://github.com/MelindaCsik)
#
 # Links
* Android App Source: github.com/Krisztian0801/Off-the-Beaten-Path
* Web Source: github.com/MelindaCsik/Off-the-Beaten-Path
* Live Demo (Web): [Off the Beaten Path Web App](https://banki13.komarom.net/2024/off-the-beaten-path/)
#
### License

This project is licensed under the [MIT License](LICENSE).  
See the `LICENSE` file for more details.
