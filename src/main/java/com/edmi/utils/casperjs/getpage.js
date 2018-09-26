var casper = require('casper').create({
	pageSettings: {
   		loadImages:  false,
  	},
	viewportSize: {    
        	width: 2000,    
        	height: 80000    
    	},
	onTimeout: function(){
		this.echo("Error:Time out").exit();	
	}
});
var url = casper.cli.get("url");
var timeout = casper.cli.get("timeout");
var item = casper.cli.get("item");
//var fs = require('fs'); 
//var data = fs.read("/home/jq/zyi/casperjs/cookie.txt"); 
//phantom.cookies = JSON.parse(data); 
casper.options.timeout = timeout;
casper.start(url);
casper.wait(10000,function() {
	//this.capture("/home/cxfang/software/casperjs/pic/tmp.png");
	//var fs = require('fs'); 
	//var cookies = JSON.stringify(phantom.cookies); 
	//fs.write("/home/jq/zyi/casperjs/cookie.txt", cookies, 644);
	var result = "Error:Item is not supported";
        if(item == "title"){
                result = this.getTitle();
        }else if(item == "content"){
                result = this.getPageContent();
        }else if(item == "url"){
                result = this.getCurrentUrl();
        }else if(item == "tilAndPage"){
		result = this.getTitle()+"$|#" + this.getCurrentUrl() +"$|#"+ this.getPageContent();
	}else if(item == "urlAndtitle"){
		result = this.getCurrentUrl() + ",,,," + this.getTitle() + ",,,,";
		result += this.evaluate(function(){
		 	var aaa=document.getElementsByName("keywords"); 
			aaa = aaa[0].getAttribute("content");
			return aaa;
		});
	}
        this.echo(result);	
});
casper.run();
