package sketches;

import javafx.util.Pair;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Meow extends PApplet {

    private List<Kitten> kittens;

    public void settings() {
        size(1<<10, 1<<9, P2D);
        noSmooth();

        kittens = IntStream.range(0, 1<<6)
                .mapToObj(i -> {
                    int w = i*2 + 80;
                    int h = i*4 + 120;
                    String url = String.format("http://placekitten.com/%d/%d", w, h);
                    return requestImage(url, "png");
                })
                .map(Kitten::new)
                .collect(Collectors.toList());

        pairUp();
    }

    private void pairUp() {
        List<Pair<Kitten, Kitten>> pairs = toPairs(kittens.stream());
        pairs.forEach(pair -> {
            pair.getKey().setDest(pair.getValue().loc);
            pair.getValue().setDest(pair.getKey().loc);
        });
    }

    public void draw() {
        surface.setTitle(
                String.format("%2.0f fps  %dx%d  %s", frameRate, width, height, getClass().getSimpleName()));
        background(55);
        noStroke();
        noFill();
        imageMode(CENTER);
        kittens.forEach(k -> {
            k.step();
            tint(255, k.alpha);
            image(k.img, k.loc.x, k.loc.y, k.dw, k.dh);
        });
    }

    class Kitten {
        final PImage img;
        final PVector initLoc;
        final int phase;
        PVector loc, dest;
        int alpha, dw, dh;
        float angle, factor;

        Kitten(PImage img) {
            this.img = img;
            phase = (int) random(0, 360);
            float x = random(img.width*.5f, width - img.width*.5f);
            float y = random(img.height*.5f, height - img.height*.5f);
            initLoc = new PVector(x, y);
            loc = initLoc.copy();
        }

        void step() {
            angle = radians((frameCount + phase) % 360);
            alpha = (int) map(sin(angle), -1, 1, 25, 205);

            if (frameCount > 5 && PVector.sub(initLoc, loc).mag() < 1) {
                Kitten other;
                while ((other = kittens.get((int) random(0, kittens.size()))) == this);
                dest = other.initLoc.copy();
            } else if (PVector.sub(dest, loc).mag() < 1)
                dest = initLoc.copy();

            PVector vel = PVector.sub(dest, loc);
            vel.normalize();
            loc.add(vel);

            angle = radians((frameCount + (360 - phase)) % 360);
            factor = map(sin(angle), -1, 1, 0.8f, 1.25f);
            dw = (int) (img.width * factor);
            dh = (int) (img.height * factor);
        }

        void setDest(PVector loc) {
            dest = loc.copy();
        }
    }

    void loadResources() throws IOException, URISyntaxException {
        ClassLoader classLoader = this.getClass().getClassLoader();
        Path folder = Paths.get(Objects.requireNonNull(
                classLoader.getResource("../resources/kittens/")).toURI());
        kittens = Files.list(folder)
                .map(path -> loadImage(path.toString()))
                .map(Kitten::new)
                .collect(Collectors.toList());
    }

    private <T> List<Pair<T, T>> toPairs(final Stream<T> s) {
        final AtomicInteger counter = new AtomicInteger(0);
        return s.collect(
                Collectors.groupingBy(item -> {
                    final int i = counter.getAndIncrement();
                    return (i % 2 == 0) ? i : i - 1;
                }))
                .values().stream()
                .map(a -> new Pair<>(a.get(0), (a.size() == 2 ? a.get(1) : null)))
                .collect(Collectors.toList());
    }
}
