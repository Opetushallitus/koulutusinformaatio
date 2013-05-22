'use strict';

describe('IndexController', function() {
    var ctrl, scope;

    beforeEach(module('kiApp'));

    describe('when index page is viewed', function() {
        var $controller_;
        var TitleService_;

        beforeEach(inject(function($rootScope, $controller, TitleService) {
            $controller_ = $controller;
            TitleService_ = TitleService;
            scope = $rootScope.$new();

        }));

        it('should have the correct title', function() {
            ctrl = $controller_(IndexCtrl, {
                $scope: scope, 
                TitleService: TitleService_
            });

            expect(TitleService_.getTitle()).toContain('title-front-page');
        });
    });
});