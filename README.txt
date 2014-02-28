ASSUMPTIONS

1. The Application is accomodating one user.
2. The Application allows the user to pick his products.
3. Since the priceOff is not increasing to >=20 on subsequent API calls, I have hardcoded the discount for products 1,2 as 21,22 respectively.
4. API call is being made by letting the current thread sleep for 6 seconds and then a call is made again.
5. Email sending process is not implemented in the code. It just gives an alert message on the console.
6. User need to enter prodcut ID as shown on the UI and should enter choice 10 to freeze his choices.
7. At any point a user can exit the application.
8. API call is made 3 times. This is configurable along with the time, the thread should sleep before making another call.



HOW TO RUN THE CODE

1. Install Maven.
2. Import the project as an existing maven project in Eclipse.
3. On the command prompt:
	a. Navigate to the directory where project is present.
	b. Write "mvn clean install -Dmaven.test.skip=true"
	c. The above line compiles the code.After compilation write "java -cp target/zappos-1.0-SNAPSHOT-jar-with-dependencies.jar com/zappos/runner/CommandLineRunner"
	d. The above statement runs the code and displays the result on the UI.
4. CommandLineRunner is the class which has the main() function.