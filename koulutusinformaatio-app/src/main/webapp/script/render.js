var page = require("webpage").create(),
  fs = require("fs"),
  system = require("system"),
  url, filename;

if (system.args.length < 3) {
  console.log("Invalid params");
  phantom.exit()
}

url = system.args[1]
filename = system.args[2]

page.open(url,
    function() {
      fs.write(filename, page.content);
      phantom.exit();
    });


