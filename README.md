#koulutusinformaatio

### Getting Started

You need the following files in your `~/oph-configuration` directory: common.properties, koulutusinformaatio.properties, security-context-backend.xml and ehcache.xml.

Then you can run the application:

    mvn tomcat:run

And go to http://localhost:8080/koulutusinformaatio-app/

### Karma js tests

Go to

`koulutusinformaatio-app/src/test/webapp/scripts`

Start karma and run all tests on chrome and PhantomJS with

`test.sh`

or run all tests against PhantomJS and stop with

`test.sh --single-run --browsers PhantomJS`

### Installing Solr

The easiest way to run the application is to point it to use external Solr and Mongo. However, if you want to run Solr locally, you need to do the following.

Install Voikko: https://github.com/KDK-Alli/SolrPlugins/wiki/Voikko-plugin

Set environment variable solr.data.dir, for example somewhere inside the target:

    -Dsolr.data.dir=/Users/username/path/to/project/koulutusinformaatio/koulutusinformaatio-app/target/classes/solr/data