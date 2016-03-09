using Toybox.WatchUi as Ui;

class logsomeDelegate extends Ui.BehaviorDelegate {

    function initialize() {
        BehaviorDelegate.initialize();
    }

    function onMenu() {
        Ui.pushView(new Rez.Menus.MainMenu(), new logsomeMenuDelegate(), Ui.SLIDE_UP);
        return true;
    }

}