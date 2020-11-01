import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class Weather {


    public static String getWeather(String message, Model model) throws IOException {
        URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q=" + message + "&units=metric&appid=bf1cbc4281bf53199817d734f5d5bec3");
        Scanner in = new Scanner((InputStream)url.getContent());
        String result = "";

        while (in.hasNext()) {
            result += in.nextLine();

        }


        JSONObject object = new JSONObject(result);
        model.setName(object.getString("name"));

        JSONObject main = object.getJSONObject("main");
        model.setTemp(main.getDouble("temp"));
        model.setHumidity(main.getDouble("humidity"));

        JSONObject wind = object.getJSONObject("wind");
        model.setSpeed(wind.getDouble("speed"));

        JSONArray getArray = object.getJSONArray("weather");
        for (int i = 0; i < getArray.length(); i++) {
            JSONObject obj = getArray.getJSONObject(i);
            model.setIconPath((String)obj.get("icon"));
            model.setMain((String)obj.get("main"));

        }

        return "City - " + model.getName() + "\n" +
                "Temperature - " + model.getTemp() + "C" + "\n" +
                "Humidity - " + model.getHumidity() + "%" + "\n" +
                "Wind - " + model.getSpeed() + "m/c" + "\n" +
                "Main - " + model.getMain()  + "\n" ;

    }


}
