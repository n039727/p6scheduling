#FMS Integration#

Application that provides integartion between Yambay - "mDrover" and Ellipse 


#Compile#

As we can't store on Git passwords and sensitive data, Unit and Integration tests need to load passwords from external properties files. To do so, the Spring PropertyPlaceholderConfigurer in the test application contexts loads properites files from a location declared as `file:${properties.dir}/*.properties`. This will load all the files with extension *.properties* in the folder declared by the system property `${properties.dir}`. When we run our tests, we just need to inform spring where is this folder.

Suggested location for  your test-override.properties files is the folder `C:/test-configs`

*Command Line*
      
To expose the web service we started from the existing WSDL and it has been used the JAXB2 maven plugin to generate the java classes. 
The plugin re-generates by default all the necessary classes,  the maven goal xjc:xjc runs by default.

To compile you can run the command 

``
compile
``

that executes the command:

``
mvn clean install -Dproperties.dir=C:/test-configs
``

If you want to run Integration tests:

``
compile i
``

that executes:

``
mvn clean install -Pintegration_tests -Dproperties.dir=C:/test-configs
``
### How do I get set up? ###

* Summary of set up
* Configuration
* Dependencies
* Database configuration
* How to run tests
* Deployment instructions

### Contribution guidelines ###

* Writing tests
* Code review
* Other guidelines

### Who do I talk to? ###

* Repo owner or admin
* Other community or team contact