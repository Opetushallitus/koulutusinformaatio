#koulutusinformaatio

### Getting Starter

Install Voikko: https://github.com/KDK-Alli/SolrPlugins/wiki/Voikko-plugin

Set environment variable solr.data.dir, for example somewhere inside the target:

    -Dsolr.data.dir=/Users/username/path/to/project/koulutusinformaatio/koulutusinformaatio-app/target/classes/solr/data

Then run

    mvn clean install
    cd koulutusinformaatio-app
    mvn tomcat:run
