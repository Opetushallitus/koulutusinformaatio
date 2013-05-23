basePath = '../';
appPath = '../../main/webapp/';

files = [
  JASMINE,
  JASMINE_ADAPTER,
  appPath + 'app/lib/angular/angular.js',
  appPath + 'app/lib/angular/angular-*.js',
  'test/lib/angular/angular-mocks.js',
  appPath + 'app/lib/jquery/jquery-1.8.0.min.js',
  appPath + 'app/lib/modernizr/modernizr-2.6.2.min.js',
  appPath + 'app/lib/jstorage.js',
  appPath + 'app/lib/i18next-1.6.0.js',
  appPath + 'app/js/**/*.js',
  'test/unit/**/*.js'
];

autoWatch = true;

browsers = ['Chrome'];

junitReporter = {
  outputFile: 'test_out/unit.xml',
  suite: 'unit'
};
