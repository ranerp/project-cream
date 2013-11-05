package controllers;

import play.*;
import play.libs.F;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {

    public static WebSocket<String> index() {
        return new WebSocket<String>() {

            @Override
            public void onReady(WebSocket.In<String> stringIn, WebSocket.Out<String> stringOut) {
                stringIn.onMessage(new F.Callback<String>() {
                    @Override
                    public void invoke(String s) throws Throwable {
                        System.out.println(s);
                    }
                });

                stringIn.onClose(new F.Callback0() {
                    @Override
                    public void invoke() throws Throwable {
                        System.out.println("Disconnected");
                    }
                });

                stringOut.write("Hello");
            }
        };
    }

}
