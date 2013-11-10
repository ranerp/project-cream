package controllers;

import models.ImageReceiver;
import play.*;
import play.mvc.*;

public class Application extends Controller {


    public static Result index() {
        return ok("It Works!");
    }

    public static WebSocket<byte[]> receiveImage() {
        return new WebSocket<byte[]>() {

            @Override
            public void onReady(WebSocket.In<byte[]> in, WebSocket.Out<byte[]> out) {
                try {
                    ImageReceiver.start(in, out);
                } catch (Exception e) {
                    Logger.debug(e.toString());
                }
            }
        };
    }

}
