//!
//! Copyright 2015 by Garmin Ltd. or its subsidiaries.
//! Subject to Garmin SDK License Agreement and Wearables
//! Application Developer Agreement.
//!

using Toybox.WatchUi as Ui;
using Toybox.Graphics as Gfx;
using Toybox.System as Sys;
using Toybox.Lang as Lang;
using Toybox.Time.Gregorian as Calendar;

class MyWatchView extends Ui.View {

    var train;

    //! Constructor
    function initialize() {
        train = new Rez.Drawables.train();
    }

    //! Load your resources here
    function onLayout(dc) {
    }

    function onShow() {
    }

    //! Update the view
    function onUpdate(dc) {
        dc.setColor(Gfx.COLOR_BLUE, Gfx.COLOR_BLACK);
        dc.fillRectangle(0, 0, dc.getWidth(), dc.getHeight());
        train.draw(dc);
    }

}
