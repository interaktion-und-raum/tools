package de.dennisppaul.ollama.examples;

import de.dennisppaul.ollama.Ollama;
import processing.core.PApplet;
import processing.core.PImage;

public class SketchOllamaTextAndImage extends PApplet {

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        /* first install and run [Ollama Server](https://ollama.com) */
        /* find ollama models at [Ollama Library](https://ollama.com/library) */
        Ollama.pull("llama3.1"); // this might take a while … pull downloads many GB of data but it only needs to happen once
        Ollama.model_name = "llama3.1";
        println(Ollama.text("hello how are you?"));

        Ollama.pull("x/llama3.2-vision"); // this might take a while
        Ollama.model_name = "x/llama3.2-vision";
        // 63075ms @ MacBookPro16
        PImage image = loadImage("/Users/dennisppaul/Desktop/test.jpg");
        println(Ollama.image(image));
        // 66527ms @ MacBookPro16
        println(Ollama.image("/Users/dennisppaul/Desktop/test.png"));

        stop();
    }

    public void draw() {
    }

    public static void main(String[] args) {
        PApplet.main(SketchOllamaTextAndImage.class.getName());
    }
}
