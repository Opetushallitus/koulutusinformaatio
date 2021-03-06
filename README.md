#koulutusinformaatio

### Getting Started

You need the following files in your `~/oph-configuration` directory: common.properties, koulutusinformaatio.properties, security-context-backend.xml and ehcache.xml.

Then you can run the application:

    mvn tomcat:run
    OR with KIJetty class run

And go to http://localhost:8080/koulutusinformaatio-app/

### Karma js tests

Go to

`koulutusinformaatio-app/src/test/webapp/scripts`

Start karma server and run all tests automatically after changes on chrome:

`test.sh`

or run all tests once against PhantomJS (like CI):

`test.sh --single-run --browsers PhantomJS`

### Installing Solr

The easiest way to run the application is to point it to use external Solr and Mongo. However, if you want to run Solr locally, you need to do the following.


#### Clone Module-install-parent 
This guide is possibly outdated. New repo for solr is https://github.com/Opetushallitus/solr, but running solr locally
might still be possible/required by doing the module-install-parent trick below. If you need to change the sold schema,
the schema file that you must change is in the solr repository, not the schema.xml shown below.

    https://github.com/Opetushallitus/module-install-parent

#### Intalling Voikko
*If you don't want to install Voikko libraries for local development* you can go to 

    module-install-parent/config/common/solr/collections/oppija/learning_opportunity/conf/schema.xml

and remove all lines containing "VoikkoFilterFactory".

*Otherwise* if you really want to install Voikko: https://github.com/KDK-Alli/SolrPlugins/wiki/Voikko-plugin

Set environment variable solr.data.dir, for example somewhere inside the target:

    -Dsolr.data.dir=/Users/username/path/to/project/koulutusinformaatio/koulutusinformaatio-app/target/classes/solr/data

#### Run Solr from module-install-parent
You can use solr from git repository module-install-parent/solr and start it using command

    mvn clean install jetty:run -Pjetty

#### Solr failing to start
If the solr server starts, but the localhost:8312/solr shows error message, check that file

    module-install-parent/config/common/solr/home/solr.xml

is unchanged (TODO: Check WHY the file changes sometimes). If the file is chaged, revert changes.

### Start education update (indexing) manually
To see status of indexing process visit

    localhost:8080/admin/status

To begin full update (several hours) visit

    localhost:8080/admin/update

To index only single learning opportunity / application option / application system visit

    localhost:8080/admin/partial/lo/[oid]
    localhost:8080/admin/partial/ao/[oid]
    localhost:8080/admin/partial/as/[oid]

To index facet and provider information visit

    localhost:8080/admin/partial/general

The Solr admin ui can be found at:
    
    http://localhost:{solr-port}/solr/#/

### Prerender

Documentation: https://prerender.io/documentation

Installing and starting:

    git clone https://github.com/prerender/prerender.git
    cd prerender
    npm install
    node server.js

