using Toybox.Application as App;
using Toybox.WatchUi as Ui;
using Toybox.System as Sys;

class logsomeApp extends App.AppBase {

    function initialize() {
        AppBase.initialize();       	    	
    }

    //! onStart() is called on application start up
    function onStart() {
        Sys.println("-->This line should be picked up");
        Sys.println("-->EOF");
    }

    //! onStop() is called when your application is exiting
    function onStop() {
    }

    //! Return the initial view of your application here
    function getInitialView() {
        return [ new logsomeView(), new logsomeDelegate() ];
    }

}
