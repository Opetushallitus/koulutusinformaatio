#koulutusinformaatio

### Getting Started

You need the following files in your `~/oph-configuration` directory: common.properties, koulutusinformaatio.properties, security-context-backend.xml and ehcache.xml.

Then you can run the application:

    mvn tomcat:run

And go to http://localhost:8080/koulutusinformaatio-app/

### Installing Solr

Install Voikko: https://github.com/KDK-Alli/SolrPlugins/wiki/Voikko-plugin

Set environment variable solr.data.dir, for example somewhere inside the target:

    -Dsolr.data.dir=/Users/username/path/to/project/koulutusinformaatio/koulutusinformaatio-app/target/classes/solr/data