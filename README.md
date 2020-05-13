# q-backend

## Javadocs
- Javadocs available here: https://elegant-queueing.github.io/q-docs (only the code for beta release has been documented)

# Set up

You may set up the environment required to run this spring boot app in 2 ways:
- Set up an instance remotely on AWS using our public AMI (RECOMMENDED)
- Set up locally by installing all the dependencies (NOT recommended)

## Setting up a remote instance on AWS (RECOMMENDED)
- Create an new account on AWS if you don't have one already: https://aws.amazon.com/
- Sign in to your account
- Click on Services (top left) and select EC2. If you can't find EC2, search for it and it should show up.
- Click on AMI. It should be on the left panel, under 'Images'.
- Next to the search bar, should be a filter saying 'Owned by me'. Change that to public images.
- Search for ```q-public-ami``` and press enter.
- Select the AMI and click on Launch.
- Choose an instance setting on your own accord and launch the instance.
- Once the instance is running, you can see it if you go to Service -> EC2 -> Instances.
- Select the instance and click on connect.
- Follow the instructions to SSH into that instance.
- Once you're on the instance, clone the repo using: ```git clone https://github.com/Elegant-Queueing/q-backend.git```
- Checkout the branch cross-origin using: ```git checkout cross-origin```
- First we need to get the service account key
- First you'll need access to our database on the cloud. Ask one of the team members for access. Unfortunately, we can't given public access to our database because of various obvious reasons.
- Once you have access, you'll download an account key. To get that, open google.firebase.com, then go to Q's Firebase console -> project settings, and download your service account key. This will be a .json file
- Rename this file to service_account_key.json
- WORD OF CAUTION, DEVELOPERS! Move this json file at the same directory level as src/. This file is NOT to be pushed to Github. .gitignore has it mentioned in it, so it's imperitive that you get the directory level right and double check using git status before you push.
- Go inside the project repo folder, and run: ```mvn install```
- Now run ```mvn spring-boot:run```
- Maven will run all the tests and you will see a message saying that the spring boot application has started on port 8080.
- Now, we need to run Redis on the same instance. Open another terminal and use it to SSH into the instance.
- Once there, run ```redis-server```
- Redis should be running.
- We're almost there. One last thing to do, is to open the instance up to public.
- Go to AWS -> Services -> EC2 -> Instances. You should see your instance running there.
- In the same row, you should find the security group column. Click on the security group for that instance.
- Select the security group, click on Actions, and then, Edit Inbound Rules
- Add a rule that accepts Type:All traffic, and Source:Anywhere.
DISCLAIMER: This^ exposes the instance to the public. We still need to work on security and none of us have experience with it. This might be a security risk. Please don't add this rule if you're not comfortable and set the environment up locally. If you do, please refrain from sharing information about this instance with anyone else.
- Save the rule. The instance is now up and open to the public. Go to the instances page.
- Select the instance, and you should see the Public DNS (IPv4). It will be something like, ec2-x-x-x-x.us-west-2.compute.amazonaws.com (where x is a number with up to 3 or 4 digits).
- Any API calls made on ec2-x-x-x-x.us-west-2.compute.amazonaws.com:8080 will be accepted.
- Go to the Testing section to test and you should be good to go!

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
- Maven will run all the tests and you will see a message saying that the spring boot application has started on port 8080.
- You should have a springboot app working on port 8080 now!

## Testing
Assuming that the springboot application is running, and that redis is running...
- Open Postman. We will now test the Firebase connection.
- Make an GET API call to the student/ endpoint using the following URI:
If local: ```localhost:8080/queue/student```
If on AWS: ```ec2-x-x-x-x.us-west-2.compute.amazonaws.com:8080/queue/student``` (replacing the x's of course)
It should return 'Pong'. If you receive this, then that means that the Spring setup is fine and you're calling the right endpoint to test the connection.
- Now, enter the following URI and make a GET API call: 
If local: ```localhost:8080/employee/get/employee-id/bfkE7Q0k9obASshNApN3```
If on AWS: ```ec2-x-x-x-x.us-west-2.compute.amazonaws.com:8080/employee/get/employee-id/bfkE7Q0k9obASshNApN3``` (replacing the x's of course)
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

