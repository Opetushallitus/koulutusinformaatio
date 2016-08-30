var appPath = 'main/webapp/';
var testPath = 'test/webapp/';

module.exports = function(config) {
    config.set({
        basePath: '../../../',
        frameworks: ['jasmine', 'sinon'],
        plugins: [
            'karma-jasmine',
            'karma-sinon',
            'karma-ng-html2js-preprocessor',
            'karma-chrome-launcher',
            'karma-phantomjs-launcher',
            'karma-junit-reporter',
            'karma-coverage'
        ],
        preprocessors: {
            'main/webapp/app/templates/*.html': 'ng-html2js',
            'main/webapp/app/partials/**/*.html': 'ng-html2js',
            'main/webapp/app/js/directives/AppBasket/**/*.html': 'ng-html2js',
            'main/webapp/app/js/directives/SearchResult/**/*.html': 'ng-html2js',
            'main/webapp/app/js/**/*.js': ['coverage']
        },
        ngHtml2JsPreprocessor: {
            stripPrefix: 'main/webapp/app/',
            moduleName: 'kiTemplates'
        },
        files: [
            appPath + 'app/lib/oph_urls.js/index.js',
            appPath + 'app/koulutusinformaatio-app-oph.js',
            appPath + 'app/lib/angular/1.2.13/angular.min.js',
            //testPath + 'test/lib/angular/angular.js',
            appPath + 'app/lib/angular/1.2.13/angular-resource.min.js',
            appPath + 'app/lib/angular/1.2.13/angular-route.min.js',
            appPath + 'app/lib/angular/1.2.13/angular-sanitize.min.js',
            appPath + 'app/lib/angular/1.2.13/angular-touch.min.js',
            appPath + 'app/lib/angular/1.2.13/angular-animate.min.js',
            appPath + 'app/lib/angular/1.2.13/angular-cookies.min.js',
            appPath + 'app/lib/angular-translate.js',
            appPath + 'app/lib/angular-recaptcha/2.1.1/angular-recaptcha.min.js',
            appPath + 'app/lib/lodash/lodash-min.js',
            appPath + 'app/lib/underscore/*.js',
            appPath + 'app/lib/angular-bootstrap/*.js',
            //appPath + 'app/lib/angulartics/*.js',
            testPath + 'test/unit/mocks/*.js',
            appPath + 'app/lib/intro/*.js',
            appPath + 'app/lib/jquery/jquery-1.8.0.min.js',
            appPath + 'app/lib/modernizr/modernizr-2.6.2.min.js',
            appPath + 'app/lib/jquery.enhanced.cookie.js',
            appPath + 'app/lib/i18next-1.6.0.js',
            appPath + 'app/lib/angular.treeview.js',
            appPath + 'app/lib/keyboardnavigation/TabsKeyboardNavigation.js',
            appPath + 'app/js/**/*.js',
            appPath + 'calendar/*.js',
            appPath + 'app/templates/*.html',
            appPath + 'app/partials/**/*.html',
            appPath + 'app/js/directives/AppBasket/**/*.html',
            appPath + 'app/js/directives/SearchResult/**/*.html',
            testPath + 'test/lib/angular/angular-mocks.js',
            testPath + 'test/unit/**/*.js',
        ],
        browsers: ['Chrome'],
        logLevel: config.LOG_DEBUG,
        autoWatch: true,
        singleRun: false,
        reporters: ['progress', 'junit', 'coverage'],
        junitReporter: {
            //outputFile: 'test_out/unit.xml',
	    outputDir: testPath + '../../test_out'
        },
        coverageReporter: {
            type: 'html',
            dir: testPath + 'coverage/'
        }
    });
};
