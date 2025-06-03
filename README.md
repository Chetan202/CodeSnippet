# CodeSnippet



https://github.com/user-attachments/assets/434a3a48-9698-43be-a564-f3b0393d1b69


![image](https://github.com/user-attachments/assets/84d69faf-de2d-4a4b-87ca-459e686d0cd8)

![image](https://github.com/user-attachments/assets/23d53268-3031-4e37-a7d0-b8cb9d983d19)

# Admin Home page

![FireShot Webpage Capture 019 - 'CodeHub - Home' - localhost](https://github.com/user-attachments/assets/cd90731e-0b41-4868-a0d7-84e7502e94c1)

![image](https://github.com/user-attachments/assets/e9a09925-9d13-4099-938d-0f9f25d08f24)


# Normal User Home page

![image](https://github.com/user-attachments/assets/cf848c3e-1ac5-4604-a169-f52e0e065522)

# Search snippet Functionality
![image](https://github.com/user-attachments/assets/e0b960d7-67d0-4b15-b88e-21bbf1319e17)

# Create Snippet Page
![image](https://github.com/user-attachments/assets/5925e6b4-f52c-45f2-a19d-51884f25f72f)

# Edit Snippet Page
![image](https://github.com/user-attachments/assets/b3730ed1-5911-44c5-9e7c-936f7c043183)

# Detail Snippet Page
![image](https://github.com/user-attachments/assets/aebff188-8d71-43cc-9060-ec3cec195753)

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

CodeSnippet is a web application that allows developers to create, store, and manage reusable code snippets. It supports role-based dashboards, full-text search, syntax highlighting, and real-time notifications—enabling teams to quickly find and reuse code blocks, track contributions, and collaborate efficiently.

---

## Features

- **User Authentication & Authorization**  
  - **Admin Role**: Full access to user management, snippet approvals, and system-wide settings.  
  - **User Role**: Create, edit, search, and delete personal snippets; view all snippets.

- **Snippet Management**  
  - Create code snippets with title, description, language, tags, and code content.  
  - Edit or delete snippets you own.  
  - Detail page showing syntax-highlighted code and metadata.

- **Search & Filtering**  
  - Full-text search across titles, descriptions, and tags.  
  - Filter snippets by programming language.

- **Dashboards**  
  - **Admin Dashboard**: Overview of all users, snippet statistics, and pending approvals.  
  - **User Dashboard**: Personalized view of owned snippets, recent activity, and notifications.

- **Real-Time Notifications**  
  - Alerts for snippet comments, approvals, and admin announcements.  
  - Notification panel accessible from the top-right bell icon.

- **Responsive User Interface**  
  - Mobile-friendly layout built with Bootstrap (or similar) for consistent experience across devices.

- **Tagging & Organization**  
  - Assign multiple tags per snippet to categorize and simplify retrieval.

- **Security & Validation**  
  - Server-side input validation and sanitization.  
  - Secure password hashing (bcrypt).  
  - Session management with auto-logout on inactivity.

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

### Design
![image](https://github.com/user-attachments/assets/9f68c149-348f-4fab-96d1-c8f3569763fc)

