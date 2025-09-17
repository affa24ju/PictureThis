## PictureThis Backend

This is the backend API for a **multiplayer drawing and guessing game** developed as a school project. Players can register and log in, then join a game lobby together.

**How it works:**

- One player is randomly assigned a secret word by the system and gets to draw it.
- The other players try to guess the word in real-time using the chat function.
- When a player guessed correctly, a new round starts and another player becomes the drawer.
- The game continues untill playes choose to log out.

This backend provides:

- Authentication & user management
- Game logic
- Real-time communikation via WebSocket (drawing & chat)
- REST API for user and game data management

The project also has a stanalone frontend built with React (Link below).

## Tools

- Java 21
- Spring Boot
- MongoDB
- Docker
- Maven
- Spring Security (JWT)
- .env file
- WebSocket

## Installation

Clone the repository:

```bash
git clone git@github.com:affa24ju/PictureThis.git
```

Create a .env file in the root directory and add your credentials:

```env
(for example)
MONGO_USERNAME=yourUserName
MONGO_PASSWORD=yourPassword
jwt.secret=DCeHHXxrCgcPf0slcSdLL7oivWYuCbqJ02nbnYFv8nM=
```

Start the project with:

```bash
docker compose up --build
```

## API Endpoints

# Registration

Register a new user by using Postman (or any other software like this):
POST: http://localhost:8080/api/users/register

Ex JSON object:
`json { "userName": "olle", "password": "olle12345" } `
See all users:
GET: http://localhost:8080/api/users

# Login

Only authorized user can login with his/ her own user name and password. Thats why everyone must need to create an account first.
POST: http://localhost:8080/api/users/login

Ex JSON object:
`json { "userName": "olle", "password": "olle12345" } `

After successfull login the client is going to be connected to WebSocket with a valid JWT token in the header.

## Unit Test

Junit and Mockito has been used in this project and it contains unit tests for:

- /register endpoint. Test includes both valid and invalid user.
- /login endpoint. It tests login with valid and invalid user credentials.
- Test for handleGuess() function. It checks correct guess, incorrect guess and also drawer couldn't participate in guessing.

## Frontend

The frontend for this project can be found here:  
[PictureThisFrontend README](https://github.com/affa24ju/PictureThisFrontend)
