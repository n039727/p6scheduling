#P6 Tactical Scheduling#

Western Power Asset Operations / Schedulers, Field Supervisors and Field Crews currently use WSMS as primary scheduling tool for job scheduling. However, business identified few limitations with WSMS tool. P6 – Scheduling Work Orders address those limitations by enabling scheduler, depots to have capacity v/s demand view, managing To-Do’s associated with work packages and track work progress. This would not only help improve resource utilization but also ensuring effective job planning and scheduling

#Compile#

As we can't store on Git passwords and sensitive data, Unit and Integration tests need to load passwords from external properties files. To do so, the Spring PropertyPlaceholderConfigurer in the test application contexts loads properites files from a location declared as `file:${properties.dir}/p6*.properties`. This will load all the files with extension *.properties* in the folder declared by the system property `${properties.dir}`. When we run our tests, we just need to inform spring where is this folder.


download the properties file from SVN

http://subversion/svn/westernpower/web/jboss-integration/jboss-domain/trunk/DEV/properties

Suggested location for  your test-override.properties files is the folder `C:/test-configs`

*Command Line*
      
To compile you can run the command 
``
mvn clean install -Dproperties.dir=C:/test-configs
``

If you want to run Integration tests:

``
mvn clean install -Pintegration_tests -Dproperties.dir=C:/test-configs
``

*Configure DEV server*

Download “config-p6portal.cli” file from 
http://subversion/svn/westernpower/web/jboss-integration/jboss-domain/trunk/DEV/scripts
Then open command prompt of windows and run
``
cd %JBOSS_HOME%\bin
jboss-cli.bat --connect --user=<domain_username> --password=<domain_password> --file="../ config-p6portal.cli"

``