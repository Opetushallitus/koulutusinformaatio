'use strict';

var page = require('webpage').create(),
    system = require('system'),
    url,
    oid,
    saveSnapshotUrl,
    scriptTagRegex = /<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi,
    cssTagRegex = /<link(.*?).css(.*?)>/gi,
    imgTagRegex = /<img(.*?)>/gi
;


var removeScriptsCssAndImages = function (html) {
    return html
        .replace(scriptTagRegex, '')
        .replace(imgTagRegex, '')
        .replace(cssTagRegex, '');
};

if (system.args.length < 4) {
    console.error("Invalid params: " + JSON.stringify(system.args));
    phantom.exit();
}

url = system.args[1];
oid = system.args[2];
saveSnapshotUrl = system.args[3];

page.settings.loadImages = false;

page.onResourceRequested = function (requestData, networkRequest) {
    // console.info('ResourceRequested ' + JSON.stringify(requestData) + ', ' + JSON.stringify(networkRequest));
};

page.onResourceReceived = function (response) {
    // console.info('ResourceReceived ' + JSON.stringify(response));
};

page.onConsoleMessage = function (msg) {
    console.info('Console ' + msg);
};

page.onError = function (msg, trace) {
    var msgStack = ['URL: ' + url];
    msgStack.push('ERROR: ' + msg);

    if (trace && trace.length) {
        msgStack.push('TRACE:');
        trace.forEach(function (t) {
            msgStack.push(' -> ' + t.file + ': ' + t.line + (t.function ? ' (in function "' + t.function + '")' : ''));
        });
    }

    console.error(msgStack.join('\n'));
};

page.onResourceError = function (request) {
    // console.error('ResourceError ' + JSON.stringify(request));
};

page.onResourceTimeout = function (request) {
    // console.error('ResourceTimeout ' + JSON.stringify(request));
};

page.onUrlChanged = function (targetUrl) {
    // console.info('UrlChanged ' + targetUrl);
};

page.onLoadStarted = function () {
    // var currentUrl = page.evaluate(function () {
    //     return window.location.href;
    // });
    // console.info('LoadStarted ' + currentUrl);
};

page.onClosing = function (closingPage) {
    // console.info('Closing ' + closingPage.url);
};

page.onInitialized = function () {
    // page.evaluate(function () {
    //     document.addEventListener('DOMContentLoaded', function () {
            // console.info('Initialized');
        // }, false);
    // });
};

page.onNavigationRequested = function(url, type, willNavigate, main) {
    // console.info('NavigationRequested ' + url + ', type ' + type + ', willNavigate ' + willNavigate + ', main ' + main);
};

page.onPageCreated = function(newPage) {
    // console.info('PageCreated');

    // newPage.onClosing = function(closingPage) {
        // console.info('Page Created, child page Closing ' + closingPage.url);
    // };
};

page.open(url, function (status) {
    // console.info('LoadFinished #1 ' + url + ', ' + status);

    if (status !== 'success') {
        console.error('Failed url #1 ' + url);
        page.close();
        phantom.exit(1);
    }

    page.evaluate(function () {
        return $("#main-info > h1").text();
    });

    var html = removeScriptsCssAndImages(page.content);

    var settings = {
        operation: 'POST',
        encoding: 'utf-8',
        headers: {
            'Content-Type': 'application/json'
        },
        data: JSON.stringify({
            'oid': oid,
            'content': html,
            'snapshotCreated': Date.now()
        })
    };

    page.open(saveSnapshotUrl, settings, function(status) {
        // console.info('LoadFinished #2 ' + saveSnapshotUrl + ', ' + status);

        if (status !== 'success') {
            console.error('Failed url #2 ' + saveSnapshotUrl);
            page.close();
            phantom.exit(1);
        }

        page.close();
        phantom.exit(0);
    });
});