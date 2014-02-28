var appPath = 'main/webapp/';
var testPath = 'test/webapp/';

module.exports = function(config) {
    config.set({
        basePath: '../../../',
        frameworks: ['jasmine'],
        plugins: [
            'karma-jasmine', 
            'karma-ng-html2js-preprocessor',
            'karma-chrome-launcher',
            'karma-junit-reporter'
        ],
        preprocessors: {
            'main/webapp/app/templates/*.html': 'ng-html2js'
        },
        ngHtml2JsPreprocessor: {
            stripPrefix: 'main/webapp/app/'
        },
        files: [
            appPath + 'app/lib/angular/1.2.13/angular.min.js',
            appPath + 'app/lib/angular/1.2.13/angular-resource.min.js',
            appPath + 'app/lib/angular/1.2.13/angular-route.min.js',
            appPath + 'app/lib/angulartics/*.js',
            appPath + 'app/lib/jquery/jquery-1.8.0.min.js',
            appPath + 'app/lib/modernizr/modernizr-2.6.2.min.js',
            appPath + 'app/lib/jquery.enhanced.cookie.js',
            appPath + 'app/lib/i18next-1.6.0.js',
            appPath + 'app/lib/angular.treeview.js',
            appPath + 'app/js/**/*.js',
            appPath + 'app/templates/*.html',

            testPath + 'test/lib/angular/angular-mocks.js',
            testPath + 'test/unit/**/*.js',
        ],
        browsers: ['Chrome'],
        logLevel: config.LOG_DEBUG,
        autoWatch: true,
        singleRun: false,
        reporters: ['progress', 'junit'],
        junitReporter: {
            outputFile: testPath + 'test_out/unit.xml'
        },
    });
};
