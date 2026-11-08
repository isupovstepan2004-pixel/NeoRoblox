# NeoRoblox (independent third-party app, not affiliated with Roblox Corporation)

Stage 1: project skeleton, MVVM, navigation, design system, Home (real Roblox game data),
official "Sign in with Roblox" (OAuth 2.0 + PKCE), Profile, one-tap launch via the official Roblox app.

## Setup
1. create.roblox.com -> Credentials -> OAuth 2.0 Apps: create an app, scopes `openid profile`,
   redirect URI `neoroblox://oauth`. Copy the Client ID.
2. Provide it one of three ways: GitHub secret `ROBLOX_CLIENT_ID`, `ROBLOX_CLIENT_ID=` in gradle.properties,
   or just type it into the app on first launch.
3. Push to `main`; GitHub Actions builds the Debug APK (Artifacts -> NeoRoblox-debug-apk).

CI uses `gradle-version: 8.9` via setup-gradle, so no gradle-wrapper.jar is required.
