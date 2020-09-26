package sketches;

import geomerative.RFont;
import geomerative.RG;
import geomerative.RShape;
import processing.core.PApplet;

public class K2 extends PApplet {

    RShape text;
    public void settings() {
        size(1<<10, 1<<9, P3D);
        RG.init(this);
        //<link href="https://fonts.googleapis.com/css?family=Anton|Bungee|Bungee+Shade|Monoton|Open+Sans|Roboto&amp;subset=cyrillic" rel="stylesheet">
        RG.loadFont("../../resources/BungeeShade.ttf");
        text = RG.getText("FAIL", "../../resources/Monoton.ttf", 200, RFont.CENTER);
    }

    public void draw() {
        background(255);
        translate(width * 0.25f, height * 0.5f);
        noStroke();
        fill(0);
        text.draw(this);
    }
}
