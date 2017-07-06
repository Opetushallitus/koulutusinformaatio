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
        if (document.querySelectorAll('h1').length || new Date().getTime() - start >= maxInterval) {
            if(document.querySelectorAll('h1').length){
                console.log("Rendering url " + url)
            } else {
                console.log("Timed out url " + url)
            }
            clearInterval(intervalId);
            writeHtmlToFile();
        }
    }, 500);
});

