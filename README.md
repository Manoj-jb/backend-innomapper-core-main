# Innowell Mapping Project

## Overview

This project focuses on managing user profiles, sites, buildings, floors, and spaces, integrating mapping services, and providing a dashboard for data visualization. The application utilizes Casdoor for user authentication and authorization.

## Use Cases

- **User Management**
  - **User Authentication Service**: Interacts with Casdoor for authentication and authorization.
  - **User Profile Management**: Handles user profile data.
  - **User Creation and Role Assigning**: Manages user creation and role assignment.

- **Site Management**
  - **Site Data Service**: Manages site information.
  - **Mapping Integration**: Integrates with mapping services.

- **Building Management**
  - **Building Data Service**: Manages building information.
  - **Mapping Integration**: Integrates with mapping services.

- **Floor Management**
  - **Floor Data Service**: Manages floor information.
  - **Mapping Integration**: Integrates with mapping services.

- **Space Management**
  - **Space Data Service**: Manages space information.
  - **Space Summary Layover**: Displays space details.

- **Easy Upload (GeoJSON)**
  - **File Upload Component**: Handles GeoJSON file uploads.

- **Search Spaces**
  - **Search Service**: Handles search functionality.
  - **Mapping Integration**: Integrates with mapping services.

- **Dashboard**
  - **Data Service**: Manages data visualization.

## 1. User Management

### 1.1 Components

- **User Authentication Service**
  - **Description**: Handles user login, registration, and logout using Casdoor's OAuth 2.0 authentication.
  - **Functions**:
    - **`registerUser(userData)`**: Registers a new user through Casdoor's API.
    - **`loginUser(credentials)`**: Redirects users to Casdoor for authentication.
      - Flow:
        1. Redirect user to `https:///login/oauth/authorize` with parameters `client_id`, `redirect_uri`, `response_type=code`, and `scope`.
        2. After user authentication, Casdoor redirects back with `code` and `state`.
        3. Exchange the `code` for an `access_token` by making a POST request to `https:///api/login/oauth/access_token`.
    - **`logoutUser(token)`**: Invalidates the user session by revoking the access token.

- **User Authorization Service**
  - **Description**: Manages access control based on user roles using Casdoor's built-in RBAC (Role-Based Access Control).
  - **Functions**:
    - **`checkUserRole(userId)`**: Retrieves user role from Casdoor.
    - **`authorizeAction(userId, action)`**: Checks if the user can perform a specific action based on their role.

- **Profile Management**
  - **Functions**:
    - **`getUserProfile(userId)`**: Fetches user profile from Casdoor.
    - **`updateUserProfile(userId, profileData)`**: Updates user profile via Casdoor's `update-user` API.

- **User Creation and Role Assigning**
  - **Functions**:
    - **`createUser(adminId, userData)`**: Creates a new user in Casdoor.
    - **`assignRole(userId, role)`**: Assigns a role to a user in Casdoor.

### 1.2 Authentication Flow

1. **User Registration**: Users register through the application, which communicates with Casdoor's API to create user accounts.
2. **User Login**: Users are redirected to Casdoor for authentication. Upon successful login, Casdoor provides an authorization code, which the application exchanges for an access token.
3. **User Logout**: The application calls the logout function, which can also revoke the access token if necessary.
4. **Profile Management**: The application can fetch and update user profiles directly through Casdoor's APIs.

### 1.3 Security Considerations

- Use HTTPS to ensure secure communication with Casdoor APIs.
- Implement token expiration handling and refresh token logic if required.
- Ensure proper error handling for API calls to manage authentication failures.

## 2. Site Management

### 2.1 Components

- **Data Service**
  - **Description**: Interacts with the database to manage site data.
  - **Functions**:
    - **`getAllBranches()`**: Retrieves all site information.
    - **`addSite(siteData)`**: Adds a new site.
    - **`editSite(siteId, siteData)`**: Edits existing site information.

- **Mapping Integration**
  - **Description**: Integrates with mapping services.
  - **Functions**:
    - **`displayMap()`**: Displays the world map.
    - **`addMarker(location)`**: Adds markers for sites on the map.
    - **`navigateToSite(siteId)`**: Navigates to a specific site on the map.

## 3. Building Management

### 3.1 Components

- **Data Service**
  - **Description**: Manages building data.
  - **Functions**:
    - **`getAllBuildings(siteId)`**: Retrieves buildings for a specific site.
    - **`addBuilding(buildingData)`**: Adds a new building.
    - **`editBuilding(buildingId, buildingData)`**: Edits building information.

- **Mapping Integration**
  - **Description**: Displays buildings on the map.
  - **Functions**:
    - **`displayBuildingsOnMap(buildings)`**: Displays buildings on the map.
    - **`navigateToFloor(buildingId)`**: Navigates to a specific floor in the building.

## 4. Floor Management

### 4.1 Components

- **Data Service**
  - **Description**: Manages floor data.
  - **Functions**:
    - **`getAllFloors(buildingId)`**: Retrieves floors for a specific building.
    - **`addFloor(floorData)`**: Adds a new floor.
    - **`editFloor(floorId, floorData)`**: Edits floor information.

- **Mapping Integration**
  - **Description**: Displays floors on the map.
  - **Functions**:
    - **`navigateToSpace(floorId)`**: Navigates to a specific space on the floor.

## 5. Space Management

### 5.1 Components

- **Data Service**
  - **Description**: Manages space data.
  - **Functions**:
    - **`getAllSpaces(floorId)`**: Retrieves spaces for a specific floor.
    - **`addSpace(spaceData)`**: Adds a new space.
    - **`editSpace(spaceId, spaceData)`**: Edits space information.

- **Space Summary Layover**
  - **Description**: Displays key information about spaces.
  - **Functions**:
    - 
# backend-innomapper-core-main
