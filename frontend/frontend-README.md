# Motorcycle Service - Frontend

This is the frontend (what the user sees) of the Motorcycle Service Center
system. It is plain HTML, CSS, and JavaScript - no framework, no build
step, no npm install needed.

## What This Project Does

This is the website part of the system. It lets a logged-in user (ADMIN
or USER) manage:

- Customers, motorcycles and motorcycle brands
- Appointments and service orders
- Spare parts, inventory and suppliers
- Purchases, invoices and payments
- Users (ADMIN only)

It talks to the backend (Spring Boot API) using AJAX calls, and keeps the
user logged in using a JWT token saved in `localStorage`.

## Tech Stack

- HTML5 - one file per page
- CSS3 - one shared stylesheet (`css/style.css`)
- JavaScript (jQuery) - `$.ajax` calls to the backend
- No build tools - just open the files in a browser

## Project Structure

```
frontend/
  index.html          -> Login page
  register.html        -> Registration page
  dashboard.html        -> Dashboard after login
  customers.html, motorcycles.html, brands.html, ...
                        -> one HTML page per feature/module
  css/
    style.css          -> all the styling
  js/
    ui.js              -> shared UI code: sidebar, topbar, tables, popups
    ajax/
      http.js          -> base helper for making AJAX requests
      auth-api.js      -> login / register calls
      customers-api.js, motorcycles-api.js, ...
                        -> one file per module, each talking to its
                           matching backend controller
    pages/
      dashboard.js, customers.js, motorcycles.js, ...
                        -> one file per page: loads data and connects
                           it to the HTML page
```

Each feature (like "Customers") usually has 3 matching files:
1. `<feature>.html` - the page itself
2. `js/ajax/<feature>-api.js` - functions that call the backend
3. `js/pages/<feature>.js` - connects the page to those functions

## Before You Run It

You need:

1. A browser (Chrome, Edge, Firefox, etc.)
2. The backend already running at `http://localhost:8080`
   (see the backend's own README for how to start it)

## How the Frontend Talks to the Backend

The backend URL is written directly inside each file in `js/ajax/`, like
this:

```js
url: 'http://localhost:8080/api/appointments'
```

If your backend runs on a different port or address, you will need to
update this URL in each `js/ajax/*.js` file. There is currently no single
shared settings file for this.

## How to Run

Since these are static files, you have two simple options:

**Option A - Just open it**
Double-click `index.html` to open it directly in your browser.

**Option B - Use a local server (recommended)**
This avoids some browser restrictions with local files.

Using VS Code:
- Install the "Live Server" extension
- Right-click `index.html` -> "Open with Live Server"

Using Python (if installed):
```
cd frontend
python3 -m http.server 5500
```
Then open `http://localhost:5500` in your browser.

## Logging In

Log in with a user account created through the backend (either the
default admin account seeded on first run, or one you register through
`register.html`). Registering through the website always creates a
normal `USER` account, not an `ADMIN` account.

## How the Pages Work Together

1. `index.html` calls `AuthApi` (in `auth-api.js`) to log in
2. On success, the JWT token is saved in `localStorage`
3. Every other page checks for this token before loading
4. `js/ui.js` builds the sidebar/topbar and hides admin-only links for
   normal `USER` accounts
5. Each page's own `js/pages/*.js` file loads its data using the
   matching `js/ajax/*-api.js` file and displays it in a table

## Common Issues

| Problem | Likely Fix |
|---|---|
| Nothing loads / network error | Backend is not running, or the URL in `js/ajax/*.js` is wrong |
| Sent back to the login page | Your session/token expired - log in again |
| Some menu items are missing | That page is ADMIN-only, and you are logged in as USER |
| "You do not have permission" message | You tried an ADMIN-only action while logged in as USER |
