# Wright Cut Barbershop

Kotlin/XML Android app for KG and Boikhutso's barbershop. No C# or ASP.NET code is included.

## Android Studio
Open the `WrightCut` folder in Android Studio Koala 2024.1.2 Patch 1 or newer. The app uses Kotlin, XML layouts, and a warm gold/near-black visual language. The home screen demonstrates booking, style gallery, service search, walk-in wait time, and loyalty streak.

## Kotlin API contract
The Android project contains `WrightCutApi.kt`, a Kotlin endpoint contract for registration, login, Google sign-in, services, barbers, availability, bookings, gallery, profile settings, FCM token registration, and loyalty. Connect these constants to your chosen REST service with Retrofit or another Kotlin HTTP client.

## Production checklist
Configure your selected Kotlin-compatible REST host, secure password hashing/JWT validation, Firebase Cloud Messaging, Google Credential Manager verification, Room offline cache/sync, multilingual `values-af` and `values-st` resources, and HTTPS hosting.

## Testing and release
Use the included GitHub Actions workflow to build the API and Android debug APK. Add unit/instrumentation tests before Play Store submission. Record the required voice-over demonstration and add its unlisted YouTube link here.

## Release notes
- Initial Wright Cut booking prototype.
- Added service catalogue, barber profiles, gallery, loyalty, availability and booking endpoints.
- Added Android Kotlin/XML home experience and GitHub CI build configuration.
