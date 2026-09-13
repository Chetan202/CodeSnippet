# CodeHub
**LIVE 🔥🐦‍🔥**
https://codehub-8apt.onrender.com/login

<img width="1908" height="878" alt="image" src="https://github.com/user-attachments/assets/a5e1e2bd-ed09-40ca-bb07-ea7b63b065df" />

<img width="1919" height="876" alt="image" src="https://github.com/user-attachments/assets/28464118-4dfb-4152-992b-b26bab7600f6" />

# Admin Home page

<img width="1919" height="875" alt="image" src="https://github.com/user-attachments/assets/8b3c4ece-2dfb-48b4-b2b8-df2b9abc9f2f" />


<img width="1919" height="870" alt="image" src="https://github.com/user-attachments/assets/6b5fec95-7021-4780-bfb5-dda15c4dd871" />

# Search snippet Functionality

<img width="1919" height="882" alt="image" src="https://github.com/user-attachments/assets/c5fc38e3-702e-42bd-af0a-ca249e6763da" />

# Create Snippet Page

<img width="1919" height="875" alt="image" src="https://github.com/user-attachments/assets/ef3133ec-3e0b-4e79-b02c-648b2ab6f7dc" />

# Edit Snippet Page

<img width="1919" height="871" alt="image" src="https://github.com/user-attachments/assets/96e5aae5-1be7-4def-b079-bf318007340d" />

# Share feature

<img width="1919" height="884" alt="image" src="https://github.com/user-attachments/assets/686d442e-cdc0-445f-ae6d-bb966797c94b" />

Anonymous user can view with the share link

<img width="1912" height="738" alt="image" src="https://github.com/user-attachments/assets/bf312096-2e6e-425c-a44a-df5a26d3ef61" />

# View Snippet Page
<img width="1919" height="827" alt="image" src="https://github.com/user-attachments/assets/5a9e9451-ee40-4c24-ae1b-ab3ba40bd1cc" />

# Bookmark feature

<img width="1214" height="604" alt="image" src="https://github.com/user-attachments/assets/f5697089-bfcf-453c-a819-b8881b65b82c" />


# Version history

<img width="1428" height="855" alt="image" src="https://github.com/user-attachments/assets/6f2cc830-8097-4a12-859b-dc2af730f5f9" />

# Import/Export Feature

<img width="943" height="840" alt="image" src="https://github.com/user-attachments/assets/5e429212-6d41-4b4d-8269-ed66c2fb1b79" />


# Notification
<!-- Example: All four images, resized to 300px wide, centered in a row -->
<p align="center">
  <img src="https://github.com/user-attachments/assets/f7ab560f-ee2b-473b-a764-6060572f47c6" width="300" alt="Image 1" />
  <img src="https://github.com/user-attachments/assets/b224c506-7a46-432b-8acf-8391f487c3c9" width="300" alt="Image 2" />
  <img src="https://github.com/user-attachments/assets/e17a1f5f-58f4-4433-9f1c-eb2323880d0a" width="300" alt="Image 3" />
  <img src="https://github.com/user-attachments/assets/29c5a256-b67e-4fb0-88c5-077068ee4951" width="300" alt="Image 4" />
</p>

---
Title: CodeSnippet
Description: A web-based application to create, store, manage, with role-based access control and real-time notifications.
---

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)  
[![Build Status](https://img.shields.io/github/actions/workflow/status/Chetan202/CodeSnippet/maven.yml?branch=main)](https://github.com/Chetan202/CodeSnippet/actions)  
[![Issues](https://img.shields.io/github/issues/Chetan202/CodeSnippet)](https://github.com/Chetan202/CodeSnippet/issues)  

# CodeSnippet

> Centralize and collaborate on your most-used code blocks.

---

## Table of Contents

1. [Description](#description)  
2. [Features](#features)  
3. [Installation](#installation)  
   - [Prerequisites](#prerequisites)  
   - [Clone & Build](#clone--build)  
   - [Database Setup](#database-setup)  
   - [Configuration](#configuration)  
   - [Run the Application](#run-the-application)  
4. [Usage](#usage)  
   - [Registration & Login](#registration--login)  
   - [Dashboard Overview](#dashboard-overview)  
   - [Create, Edit & Delete Snippets](#create-edit--delete-snippets)  
   - [Search & Filter](#search--filter)  
   - [Notifications](#notifications)  
   - [Admin Actions](#admin-actions)  
5. [Contributing](#contributing)  
6. [License](#license)  
7. [Contact](#contact)  

---

## Description

CodeSnippet is a web application that allows developers to create, store, and manage code snippets. It supports role-based dashboards, full-text search and real-time notifications—enabling user to quickly find and code blocks.

---

## Features

- **User Authentication & Authorization**  
  - **Admin Role**: Full access to user management, snippet approvals, and system-wide settings.  
  - **User Role**: Create, edit, search, and delete personal snippets; view all snippets.

- **Snippet Management**  
  - Create code snippets with title, description, programming language, tags, and code content.  
  - Edit or delete snippets you own.  
  - Detail page showing code and title and other details.

- **Search & Filtering**  
  - Full-text search across titles, descriptions, and code.  
  - Filter snippets by programming language.

- **Dashboards**  
  - **Admin Dashboard**: Overview of all users, snippet statistics, and pending approvals.  
  - **User Dashboard**: Personalized view of owned snippets, recent activity, and notifications.

- **Real-Time Notifications**  
  - Alerts for snippet comments, approvals, and admin announcements.  
  - Notification panel accessible from the top-right bell icon.

- **Security & Validation**  
  - Server-side input validation and sanitization.  
  - Secure password hashing (bcrypt).  

---

## Installation

### Prerequisites

1. **Java Development Kit (JDK)** ≥ 1.8  
2. **Apache Maven** ≥ 3.6.x  
3. **Relational Database** (MySQL 5.7+, PostgreSQL 9.x+, or any JDBC-compatible)  
4. *(Optional)* **Apache Tomcat** (for WAR deployment)  

Ensure `java` and `mvn` executables are on your system PATH.

### Clone & Build

```bash
git clone https://github.com/Chetan202/CodeSnippet.git
cd CodeSnippet

```


