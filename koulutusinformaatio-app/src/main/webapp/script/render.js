var page = require("webpage").create(),
    fs = require("fs"),
    system = require("system"),
    scriptTagRegex = /<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi,
    url, filename;

var stripScriptTags = function(html) {
    return html.replace(scriptTagRegex, '');
};

if (system.args.length < 3) {
    console.log("Invalid params");
    phantom.exit()
}

url = system.args[1];
filename = system.args[2];

page.open(url, function() {

    var writeHtmlToFile = function() {
        var html = stripScriptTags(page.content);
        fs.write(filename, html);
        phantom.exit();
    };

    var maxInterval = 10000; // 10 sec
    var start = new Date().getTime();
    var intervalId = setInterval(function() {

        var body = page.evaluate(function(s) {
            return document.querySelector(s).style.display;
        }, 'body');

        if (body !== 'none' || new Date().getTime() - start >= maxInterval) {
            clearInterval(intervalId);
            writeHtmlToFile();
        }

    }, 500);
});

