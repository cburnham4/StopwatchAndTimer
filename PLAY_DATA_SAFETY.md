# Google Play Data Safety Responses

**App:** Stopwatch and Timer (`letshangllc.timer`)  
**Version:** 3.0.0 (versionCode 100)  
**Last Updated:** November 2026  
**Target SDK:** 36

This document provides responses for the Google Play Console Data Safety section.

---

## Overview

This app is a local stopwatch/timer utility. It does not require sign-in and does not sync user data to a developer backend. It displays Google AdMob banner ads and uses Google’s User Messaging Platform (UMP) for consent where required.

---

## Data Collection Summary

| Data Type | Collected | Shared | Purpose |
|-----------|-----------|--------|---------|
| App interactions | Yes | Yes (AdMob) | Advertising |
| Advertising ID | Yes | Yes (AdMob) | Advertising |
| Device or other IDs | Yes | Yes (AdMob) | Advertising |
| Crash logs | No | No | — |
| Personal info (name/email) | No | No | — |

Timer preferences (hours/minutes/seconds last chosen) are stored only in on-device SharedPreferences and are not transmitted by the app.

---

## Detailed Data Types

### 1. App Activity

#### App Interactions
- **Collected:** Yes (via Google AdMob / advertising SDKs)
- **Shared:** Yes (with Google AdMob for ad serving/personalization)
- **Purpose:** Advertising
- **Optional:** Subject to GDPR/consent via UMP
- **Encrypted in transit:** Yes (HTTPS)

### 2. Device or Other IDs

#### Advertising ID
- **Collected:** Yes (via Google AdMob)
- **Shared:** Yes (with Google for advertising)
- **Purpose:** Advertising
- **Optional:** Users can reset/opt out via device settings; UMP consent in EEA/UK
- **Encrypted in transit:** Yes (HTTPS)

---

## Third-Party SDKs

### Google AdMob (`play-services-ads`)
- **Data collected:** Advertising ID, app interactions, device/ad info as described by Google
- **Purpose:** Display banner advertisements
- **Shared:** Yes, with Google advertising partners
- **User Control:** UMP consent flow for GDPR compliance
- **Privacy Policy:** https://policies.google.com/privacy

### Google User Messaging Platform (UMP)
- **Purpose:** GDPR/consent management before requesting ads
- **Data collected:** Consent preferences
- **Privacy Policy:** https://policies.google.com/privacy

Amazon Mobile Ads has been **removed** from this release.

---

## Permissions Declared

| Permission | Why |
|------------|-----|
| `INTERNET` | Load AdMob ads over HTTPS |
| `ACCESS_NETWORK_STATE` | AdMob / connectivity checks |
| `ACCESS_WIFI_STATE` | Legacy connectivity check used by ads stack |

No `POST_NOTIFICATIONS`, location, microphone, camera, or contacts permissions are used.

**Play Console notes:** Declare advertising ID usage. Confirm Ads / Families policies if the listing targets children (this app is not directed at children).

---

## Data Security

- **In transit:** Yes — `android:usesCleartextTraffic="false"`; AdMob traffic is HTTPS
- **At rest:** Timer prefs stay on device only
- **Deletion:** Users can clear app data / uninstall; no cloud account to delete

---

## Children's Privacy

This app is **not** directed at children under 13 (COPPA) or under 16 (GDPR).

- `setTagForUnderAgeOfConsent(false)` is configured in UMP
- No child-directed ad serving is enabled

---

## Regional Compliance

### GDPR (EU/EEA/UK)
- UMP consent is requested before personalized ads when required
- Non-personalized ads may be served when consent is not given (per AdMob/UMP rules)

### CCPA (California)
- Users can opt out of personalized ads via device-level advertising settings

---

## Play Console Form Responses

### Does your app collect or share any of the required user data types?
**Yes**

### Is all of the user data collected by your app encrypted in transit?
**Yes**

### Do you provide a way for users to request that their data is deleted?
**Yes** (clear app data / uninstall; no account)

### Data Types to Declare

1. **App interactions**
   - Collected: Yes
   - Shared: Yes (Google/AdMob)
   - Purpose: Advertising

2. **Device or other IDs**
   - Collected: Yes
   - Shared: Yes (Google/AdMob)
   - Purpose: Advertising

---

## Contact

For privacy inquiries, contact the developer via the email address listed on the Google Play Store listing.
