#koulutusinformaatio

### Getting Starter
Set environment variable solr.data.dir, for example somewhere inside the target:

    -Dsolr.data.dir=/Users/username/path/to/project/koulutusinformaatio/koulutusinformaatio-app/target/classes/solr/data

    mvn clean install
    cd koulutusinformaatio-app
    mvn tomcat:run
