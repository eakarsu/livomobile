function loadjsfile(filename){
    
   var fileref=document.createElement('script');
   fileref.setAttribute("type","text/javascript");
   fileref.setAttribute("src", filename);
   document.getElementsByTagName("head")[0].appendChild(fileref);
   
}

var currNav = navigator.userAgent.indexOf("Android") > 0 ? 'android' : 'ios';
loadjsfile("cordova." + currNav + ".js");