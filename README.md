# q-backend

This document provides the user and developer documentations for q-backend.

The code on the ```final-release``` branch is the latest released code.

## User Documentation

### Description
This repository contains all the code for Q's backend service. On a high level, it has 3 services: 
- Employee service for the employee front-end application
- Student service for the student front-end application
- Queue service for everything related to queuing, that both the front-end apps may use

The backend service is a Spring Boot Application that exposes API endpoints. A user (e.g. a front-end developer) can use these APIs by running the Spring Boot Application, thereby exposing the API endpoints to their own application. These APIs may be used to allow students and employees to create profiles and simulate virtual queuing for career fairs.

### User Setup
- Set up an environment of your choice using the detailed environment setup instructions [here](#detailed-env).
- Get the code using the ['Getting the code'](#detailed-code) section of this document.
- Set up a database on Firebase using [this firebase setup](#detailed-firebase) section.

### Running the Spring Boot Application
- After setting everything up, use the terminal to cd into the code repository.
- Use the terminal to run: ```mvn install```
- Now run ```mvn spring-boot:run```
- Maven will run all the tests and you will see a message saying that the spring boot application has started on port 8080.
- While this is running, open another terminal (or SSH into your instance using another terminal if you set everything up remotely) and run Redis using: ```redis-server```

### Using the Spring Boot Application

#### Get the address and port
- If you're running the application locally, any API calls made on localhost:8080 will be accepted.

- If you're running the application remotely, you should be able to make calls using the Public DNS (IPv4) of your instance. Go to the instances page on AWS, and it should be mentioned there. It will be something like, ec2-x-x-x-x.us-west-2.compute.amazonaws.com (where x is a number with up to 3 or 4 digits). Any API calls made on ec2-x-x-x-x.us-west-2.compute.amazonaws.com:8080 will be accepted.

#### Using the APIs
To be able to use the API, you will have to look at the Javadocs available here: https://elegant-queueing.github.io/q-docs

Notice the classes in ```package com.careerfair.q.controller```. The *implementation* classes of all the controllers should provide information as to how the API call should be structured. In particular, look at the method details to know the exact mappings.

Just for a sanity check, run the the app and do the following:

Enter the following URI and make a GET API call to {address}:8080/employee/get/employee-id/{employee_id}
Replace the {address} with localhost or your public DNS. Replace the {employee_id} with the id of an employee that you created when you added a document in the ```employees``` collection.

- If everything works, you should get the employee document on the database:
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
### Work in progress

Authorization
Resume upload </br>
Image upload </br>
Resume tagging </br>

### Reporting a bug

Please use GitHub issues on this repository to report any bugs. Make sure all your software is up to date and you followed all the setup steps. 

In your report, be sure to add the error message, the versions of the prerequisite softwares you are using, along with your operating system and IDE (if applicable). If possible, please provide the precise steps that resulted in the issue. Also mention what was expected to happen, and what happened. 

To report a bug: 
- Go to the issues tab of this repository
- Click on New Issue and write a report of the issue
- Add the label 'bug' and submit the report

## Developer Documentation

### Obtaining the source code
Get the code using the ['Getting the code'](#detailed-code) section of this document.

### Layout of the directory structure

#### Source files

Any source files can be found under src/ folder. Inside the src/ folder, there will multiple packages as the following:

configuration: This is the package where all configurations for the building this project will be present. It currently holds two configuration packages, one for redis/ and the other for web/.

controller: This is the package where all the Spring’s RestControllers will be present. These classes sole responsibility is to expose endpoints to the front end. Each sub-package directly corresponds to a package in the service/ package.

database: Any initialization of the database would be present in this directory. It currently contains only one class which sets up a connection to Firebase.

model: This package contains any representation of objects (POJO) that are needed by services to store information. It currently contains two packages db/ and redis/ with each containing the models stored in respective storage units.

service: This package contains all the functionality of the endpoints that the controller/ package is exposing or services that are used internally by other services. Each sub-service package may contain a request/ and response/ package which signify the request and response objects to be received from and sent to the front end respectively. The logic for the core functionality is present in these packages.

util: The util package consists of 3 packages - enum/, constant/, and exception/.
    	enum/ stores any enums that are used throughout the whole system.
	constant/ stores any constants that are used throughout the whole system.
	exception/ stores any custom exception that need to be thrown in the whole system

workflow: This package contains any subtask the service might have. Each sub-package should have a corresponding service/ associated with it.

test: This package contains all the tests for all the classes.

#### Test files / Adding tests

Any test files can be found under test/ folder. The directory under this folder should exactly match that in src/ with each test file in those packages testing the corresponding source file. This where the developer must add any test files. Please look at the test/ folder and existing tests for reference.

### Developer Setup
- Set up an environment of your choice using the detailed environment setup instructions [here](#detailed-env)
- Get the code using the ['Getting the code'](#detailed-code) section of this document.
- Set up a database on Firebase using [this firebase setup](#detailed-firebase) section.
- Install the Lombok plugin in your IDE so that the tags like @Data work.

### Build, Run
- After setting everything up, use the terminal to cd into the code repository.
- To build the project, use the terminal to run: ```mvn clean install```
- To run the app, run: ```mvn spring-boot:run```
- Maven will run all the tests and you will see a message saying that the spring boot application has started on port 8080.
- While this is running, open another terminal (or SSH into your instance using another terminal if you set everything up remotely) and run Redis using: ```redis-server```

### Test
To run the tests, run: ```mvn clean test```

We are using Github Actions to have a CI setup where tests are run on each push to master. For more details about the setup, look into the folder: ```q-backend/.github/workflows```. To see the build histories, use the Actions tab on GitHub (next to Issues, Pull requests)

### Release process
The latest commit on the ```final-release``` branch is considered our latest release.

To release a build (assuming the PR for the code was approved and passed all tests):
- From within your repository, run ```mvn clean install```. This will create the Javadocs for the project.
- Clone the repository that is hosting q-backend's Javadocs using: ```git clone https://github.com/Elegant-Queueing/q-docs.git```
- Replace all the current files with the new files created by your lates ```mvn clean install``` command.
- Push to the repository, and make sure the website ```https://elegant-queueing.github.io/q-docs/``` reflects the changes.
- Merge your PR.

## Detailed Setup Instructions

### <a name="detailed-env"></a> Environment
You may set up the environment required to run this Spring Boot app in 2 ways:
- Set up an instance remotely on AWS using our public AMI
- Set up locally by installing all the dependencies and prerequisites

#### Setting up a remote instance on AWS
- Create an new account on AWS if you don't have one already: https://aws.amazon.com/
- Sign in to your account.
- Click on Services (top left) and select EC2. If you can't find EC2, search for it and it should show up.
- Click on AMI. It should be on the left panel, under 'Images'.
- Next to the search bar, should be a filter saying 'Owned by me'. Change that to public images.
- Search for ```q-public-ami``` and press enter.
- You should see 1 AMI with that name. Make sure your location (top right) is set to Oregon.
- Select the AMI and click on Launch.
- Choose an instance setting that suits your needs and budget (there should be a free one too) and launch the instance.
- Once the instance is running, you can see it if you go to Service -> EC2 -> Instances.
- To SSH into this instance, select the instance and click on connect. Then, simply follow the instructions to SSH into it.
- Go to AWS -> Services -> EC2 -> Instances. You should see your instance running there.
- In the same row, you should find the security group column. Click on the security group for that instance.
- Select the security group, click on Actions, and then, Edit Inbound Rules
- Add a rule that accepts Type:All traffic, and Source:Anywhere.
DISCLAIMER: This^ exposes the instance to the public. We still need to work on security and none of us have experience with it. This might be a security risk. Please don't add this rule if you're not comfortable and set the environment up locally. If you do, please refrain from sharing information about this instance with anyone else.

This instance will have all the prerequisite software set up for you.

#### Setting up a local environment
- Download and install a version of Java that is greater or equal to Java8: https://java.com/en/download/
- Make sure to test the installation using: ```java -version```
- Download and install Redis: https://redis.io/download
- Test the installation by running: ```redis-server``` on the terminal
- Download and install Maven v3.6.0: https://maven.apache.org/install.html
- Make sure to test the installation using: ```mvn -version```

### <a name="detailed-code"></a> Getting the code

- Clone the repo using: ```git clone https://github.com/Elegant-Queueing/q-backend.git```
- Make sure you're on ```final-release``` since it has the code from the latest release

### <a name="detailed-firebase"></a> Firebase
NOTE: The Q team already has a dev database. To get access to it, please contact the team on CSE 403's Slack workspace. Once you have access to it, make changes to the code using the video: https://drive.google.com/file/d/1eiZPQq5OnAkD-kf628ZCYe6tTYlACvDB/view

Unfortunately, we do not give public access to people who are not a part of the Q team, or are not enrolled in CSE 403, Spring 2020.
