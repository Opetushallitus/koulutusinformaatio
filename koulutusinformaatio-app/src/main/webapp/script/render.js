var page = require("webpage").create(),
  fs = require("fs"),
  system = require("system"),
  scriptTagRegex = /<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi,
  url, filename;

var stripScriptTags = function(html) {
  return html.replace(scriptTagRegex, '');
}

if (system.args.length < 3) {
  console.log("Invalid params");
  phantom.exit()
}

url = system.args[1];
filename = system.args[2];

page.open(url, function() {
    var html = stripScriptTags(page.content);
    fs.write(filename, html);
    phantom.exit();
});
