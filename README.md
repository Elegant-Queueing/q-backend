# q-backend

## Javadocs
- Javadocs available here: https://elegant-queueing.github.io/q-docs

## Setting up Redis

- Download and install Redis: https://redis.io/download
- Test the installation by running redis-server and redis-cli
- Further test the installation using commands mentioned here: https://redis.io/commands#hash . This will also make you familiar with the functions that Redis provides.

## Setting up Firebase

Assuming you've already cloned the repo:
- Go to Q's Firebase console -> project settings, and download your service account key. This will be a .json file
- Rename this file to service_account_key.json
- CAUTIOUS! Move this json file at the same directory level as src/. This file is NOT to be pushed to Github. .gitignore has it mentioned in it, so it's imperitive that you get the directory level right and double check using git status before you push.
- Open StudentFirebaseImpl.java (it's in service/database/implementation)
- We will now try to add an object (or in Firebase's lingo, a document) with an attribute 'name' to the 'test' collection in Firebase.
- Open Firebase console and make sure there's a test collection.
- Set String name to something that's not already present in the test collection.
- Run the Spring server and open Postman. We will now test the Firebase connection.
- Make an API call to the student/ endpoint. It should return 'Pong'. If you receive this, then that means that the Spring setup is fine and you're calling the right endpoint to test the connection.
- Open the test collection. You should now see a document with the name that you gave in it. If so, the test passed, otherwise, it failed.

## Other setups
- Install the Lombok plugin so that the @Data tag works
