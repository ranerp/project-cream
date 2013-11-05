package controllers;

import play.*;
import play.libs.F;
import play.mvc.*;

import views.html.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;


public class Application extends Controller {

    public static WebSocket<byte[]> index() {

        return new WebSocket<byte[]>() {

            @Override
            public void onReady(WebSocket.In<byte[]> in, WebSocket.Out<byte[]> out) {
                in.onMessage(new F.Callback<byte[]>() {
                    @Override
                    public void invoke(byte[] inPayload) throws Throwable {

                        byte[] header = new byte[256];
                        for(int i = 0; i < header.length; i++) {
                            header[i] = inPayload[i];
                        }

                        String headerStr = new String(header, "UTF-8");

                        byte[] imgBytes = new byte[inPayload - header.length];
                        for(int i = 0; i < inPayload.length; i++) {
                            imgBytes[0] = inPayload[i + header.length];
                        }

                        BufferedImage img = ImageIO.read(new ByteArrayInputStream(imgBytes));
                        System.out.println(headerStr);
                    }
                });

                in.onClose(new F.Callback0() {
                    @Override
                    public void invoke() throws Throwable {
                        System.out.println("Disconnected");
                    }
                });

                String str = "Hello android";

                out.write(str.getBytes());

            }
        };
    }

}
