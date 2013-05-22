basePath = '../';

files = [
  JASMINE,
  JASMINE_ADAPTER,
  'app/lib/angular/angular.js',
  'app/lib/angular/angular-*.js',
  'test/lib/angular/angular-mocks.js',
  'app/lib/jquery/jquery-1.8.0.min.js',
  'app/lib/modernizr/modernizr-2.6.2.min.js',
  'app/lib/jstorage.js',
  'app/lib/i18next-1.6.0.js',
  'app/js/**/*.js',
  'test/unit/**/*.js'
];

autoWatch = true;

browsers = ['Chrome'];

junitReporter = {
  outputFile: 'test_out/unit.xml',
  suite: 'unit'
};
