# q-backend

## Javadocs
- Javadocs available here: https://elegant-queueing.github.io/q-docs

# Set up

You may set up the environment required to run this spring boot app in 2 ways:
- Set up an instance remotely on AWS using our public AMI (RECOMMENDED)
- Set up locally by installing all the dependencies (NOT recommended)

## Setting up a remote instance on AWS (RECOMMENDED)


## Setting up locally

### Get the code
- Clone the repo using: ```git clone https://github.com/Elegant-Queueing/q-backend.git```
- Checkout the branch cross-origin using: ```git checkout cross-origin```

### Setting up Redis

- Download and install Redis: https://redis.io/download
- Test the installation by running redis-server and redis-cli
- Further test the installation using commands mentioned here: https://redis.io/commands#hash . This will also make you familiar with the functions that Redis provides.

### Setting up Firebase

We recommend using IntelliJ, Postman and Maven! Please install these if you haven't already.

Clone the repo using: ```git clone https://github.com/Elegant-Queueing/q-backend.git```
Checkout the branch cross-origin using: ```git checkout cross-origin```
NOTE: master does NOT have code for beta release. The code for beta releas is on cross-origin!

- First you'll need access to our database on the cloud. Ask one of the team members for access. Unfortunately, we can't given public access to our database because of various obvious reasons.
- Once you have access, you'll download an account key. To get that, open google.firebase.com, then go to Q's Firebase console -> project settings, and download your service account key. This will be a .json file
- Rename this file to service_account_key.json
- WORD OF CAUTION, DEVELOPERS! Move this json file at the same directory level as src/. This file is NOT to be pushed to Github. .gitignore has it mentioned in it, so it's imperitive that you get the directory level right and double check using git status before you push.
- Open terminal, go to the project repository you cloned and run: ```mvn install```
- Then, run: ```spring-boot:run```
- You should have a springboot app working on port 8080 now!
- Open Postman. We will now test the Firebase connection.
- Make an GET API call to the student/ endpoint using the following URI: ```localhost:8080/queue/student```
It should return 'Pong'. If you receive this, then that means that the Spring setup is fine and you're calling the right endpoint to test the connection.
- Now, enter the following URI and make a GET API call: 
```localhost:8080/employee/get/employee-id/bfkE7Q0k9obASshNApN3```
- If everything works, you should get a response like this:
```
{
    "employee": {
        "employee_id": "bfkE7Q0k9obASshNApN3",
        "name": "John Doe",
        "company_id": "A51NaN8uB9GEViJZHbuo",
        "role": "SWE",
        "bio": "I code. A lot",
        "email": "codealot@code.com",
        "students": [
            "oFEdwg9lnWPPVjHPL0Nk"
        ]
    }
}
```

### Other setups
- Install the Lombok plugin so that the @Data tag works

## Running locally
Close the springboot app if it's running already.
- Open a terminal and run Redis using: ```redis-server```
- Open another terminal, go to the project repo and run: ```mvn install```
- Then run: ```mvn spring-boot:run```

Your backend should be good to go now! Any API calls made to localhost:8080 by the client-side apps will be attended by the code.

