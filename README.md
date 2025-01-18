# Bank App

Bank app is a simple banking application that lets you do simple banking
tasks like creating new users and deposit, withdraw and transferring of funds.

the api endpoints are protected with token based authentication

the current token based authentication is very weak, 
it only allows users with username 'user' and password 'password'
to access the private endpoints. an authentication manager can be
added to make it better, but did not add bc of time constraints



## How to use:
### Step 1:
Configure the application.properties file to use your local sql login details

```
spring.jpa.hibernate.ddl-auto=create-drop
spring.datasource.url=jdbc:mysql://localhost:3306/accounts?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=1234
```

### Step 2:

Start the application by running BankMain (Run from intellij/javac)


### Step 3:
Start using.


Call /authenticate endpoint with params in request body
```
{
"username": "user",
"password": "password"
}
```
it will give you a bearer token. 
For all subsequent requests, copy bearer token into Auth header and send requests

I have attached a Postman requests collection that makes it easy to plug in and use. 
Please look for bank_app.postman_collection.json in the project






