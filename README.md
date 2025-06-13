# ProductHub Expo App

This is a React Native application built with Expo Router. It interacts with a REST API to manage products and authentication.

## Requirements

- Node.js 18+
- Expo CLI (`npm install -g expo-cli`)

## Setup

1. Install dependencies:
   ```bash
   npm install
   ```
2. Create a `.env` file at the project root with the API URL:
   ```bash
   EXPO_PUBLIC_API_URL=http://127.0.0.1:8000/api
   ```
   Adjust the URL if your backend runs elsewhere.
3. Start the Expo development server:
   ```bash
   npm run dev
   ```
4. Use the Expo app or an emulator to open the project.

## Linting

Run `npm run lint` to check the code with Expo's linter.

## Available Scripts

- `npm run dev` – Start the Expo development server.
- `npm run build:web` – Build the web version of the app.
- `npm run lint` – Run ESLint using Expo's config.

