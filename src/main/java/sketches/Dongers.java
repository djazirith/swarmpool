package sketches;

import geomerative.RFont;
import geomerative.RG;
import geomerative.RShape;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PShape;

import java.util.Arrays;

public class Dongers extends PApplet {
    private RShape donger;


    public void setup() {
        super.setup();
        PFont dokchamp = createFont("dokchampa", 10);
//        String[] fontList = PFont.list();
//        Arrays.stream(fontList).forEach(s -> System.out.println(s));
    }
    PShape shape;
    public void settings() {
        setup();
//        RG.init(this);
//        RG.setPolygonizer(RG.UNIFORMLENGTH);
//        RG.loadFont("msgothic.ttc");
//        RG.loadFont("meiryob.ttc");
//        RG.loadFont("himalaya.ttf");
//        RG.loadFont("meiryo.ttc");
//        RG.loadFont("euphemia.ttf");
//        RG.loadFont("ebrima.ttf");
//        RG.loadFont("dokchamp.ttf");
//        loadFont("dokchamp");

//        donger = RG.getText("━╤デ╦︻(▀̿̿Ĺ̯̿̿▀̿ ̿)");
//        shape = dokchamp.getShape('╤');
        //, "../../resources/marianna.ttf", 100, RFont.CENTER
        size(1 << 11, 1 << 11-1, FX2D);
    }

    public void draw() {
        background(225);
        noFill();
        strokeWeight(1);
        translate(width * 0.5f, height * 0.5f);
//        donger.draw();
        shape.draw(this.g);
    }

    }
