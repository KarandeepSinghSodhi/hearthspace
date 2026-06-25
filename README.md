# Shared Love Widget 💕

A home-screen widget shared between two people. One shared note, one shared picture, one tiny pixel pet. That's it.

---

## Setup

### 1. Firebase Project

1. Go to [console.firebase.google.com](https://console.firebase.google.com) → create a project called `shared-love-widget`
2. Enable **Authentication → Google Sign-In**
3. Enable **Firestore Database** (production mode)
4. Enable **Firebase Storage**
5. Add an **Android app** with package `com.lovewidget.shared`
6. Download `google-services.json` → place at `app/google-services.json`
7. Deploy security rules: `firebase deploy --only firestore:rules,storage`

### 2. Generate Signing Keystore

```bash
keytool -genkeypair -v \
  -keystore lovewidget.keystore \
  -alias lovewidget \
  -keyalg RSA -keysize 2048 \
  -validity 10000
```

> ⚠️ **Keep this file safe.** Never commit it to Git.

### 3. Build Debug APK locally

```bash
./gradlew assembleDebug
# APK at: app/build/outputs/apk/debug/app-debug.apk
```

### 4. Build & Release via GitHub Actions

Add these **GitHub repository secrets**:

| Secret | Value |
|--------|-------|
| `KEYSTORE_BASE64` | `base64 -i lovewidget.keystore` |
| `KEYSTORE_PASSWORD` | your keystore password |
| `KEY_ALIAS` | `lovewidget` |
| `KEY_PASSWORD` | your key password |
| `GOOGLE_SERVICES_JSON` | `base64 -i app/google-services.json` |

Then push a version tag:

```bash
git tag v1.0.0
git push origin v1.0.0
```

The APK will be signed and published to **GitHub Releases** automatically.

---

## How to Use

1. Install APK on both phones
2. Open app → Sign in with Google
3. **First phone**: tap "Create Pair" → share the Pair ID (QR or copy)
4. **Second phone**: paste the Pair ID → tap "Join Pair"
5. Add the widget to both home screens
6. Tap the widget to edit the note or picture
7. Both widgets update in real time ❤️

---

## Architecture

```
SharedLoveApp
├── auth/          AuthManager (Google Sign-In)
├── data/
│   ├── model/     SharedItem, Pairing, PetInfo
│   ├── local/     Room DB (SharedItemEntity, SharedItemDao, AppDatabase)
│   └── repository/SharedRepository, PairingRepository
├── di/            Hilt modules (App, Database)
├── domain/pet/    PetState, PetStateMachine
├── ui/
│   ├── MainActivity, EditActivity
│   ├── pairing/   PairingScreen, PairingViewModel
│   └── edit/      EditScreen, EditViewModel
├── widget/        SharedLoveGlanceWidget, GlanceLayouts, WidgetUpdateService, BootReceiver
├── work/          SyncEditWorker
└── utils/         QRCodeGenerator, UpdateChecker
```

## Cost

**₹0 / month** — Firebase Spark (free tier) is more than sufficient for two users.
