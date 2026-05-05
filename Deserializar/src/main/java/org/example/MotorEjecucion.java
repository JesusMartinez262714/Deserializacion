package org.example;

import java.awt.Desktop;
import java.net.URI;

public class MotorEjecucion {

    public static void ejecutarComando(String comando) throws Exception {
        String os = System.getProperty("os.name").toLowerCase();

        switch (comando) {
            case "calc":
                if (os.contains("win")) Runtime.getRuntime().exec("calc.exe");
                else if (os.contains("mac")) Runtime.getRuntime().exec("open -a Calculator");
                else Runtime.getRuntime().exec("gnome-calculator");
                break;
            case "notepad":
                if (os.contains("win")) Runtime.getRuntime().exec("notepad.exe");
                else if (os.contains("mac")) Runtime.getRuntime().exec("open -a TextEdit");
                else Runtime.getRuntime().exec("gedit");
                break;
            case "web":
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    Desktop.getDesktop().browse(new URI("https://pranx.com/hacker/"));
                }
                break;
            default:
                throw new Exception("Comando no reconocido.");
        }
    }
}