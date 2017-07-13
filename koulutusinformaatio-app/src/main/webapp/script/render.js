var page = require("webpage").create(),
    fs = require("fs"),
    system = require("system"),
    scriptTagRegex = /<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi,
    cssTagRegex = /<link(.*?).css(.*?)>/gi,
    url, filename;

var stripScriptTags = function (html) {
    return html.replace(scriptTagRegex, '').replace(cssTagRegex, '');
};

if (system.args.length < 3) {
    console.log("Invalid params");
    phantom.exit()
}

url = system.args[1];
filename = system.args[2];

page.settings.loadImages = false;

page.onResourceRequested = function (request) {
//    console.log('Request ' + JSON.stringify(request, undefined, 4));
};
page.onResourceReceived = function (response) {
//    console.log('Receive ' + JSON.stringify(response, undefined, 4));
};
page.onConsoleMessage = function (msg) {
//    console.log(msg);
};


page.open(url, function (status) {
    var writeHtmlToFile = function () {
        var html = stripScriptTags(page.content);
        fs.write(filename, html);
    };

    if (status !== 'success') {
        console.log("Failed url " + url);
    } else {
        page.evaluate(function () {
            return $("#main-info > h1").text();
        });
//            console.log("Rendering url " + url + " to file " + filename);
        writeHtmlToFile();
    }
    phantom.exit();
});
