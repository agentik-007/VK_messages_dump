import java.io.File;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

public class parsMess {

    private static final String fileName = "C://mes/name.txt"; //куда сохряняем переписку
    //https://oauth.vk.com/authorize?client_id=4796859&scope=messages&redirect_uri=https://oauth.vk.com/blank.html&response_type=token

    public static void main(String[] args) throws URISyntaxException, IOException, ParseException, InterruptedException {
        String ACCESS_TOKEN = "09bc46c13aad67cde8d8cf22bbeb7a9e7052c69aac84d35a3459d46da623e2d044bdb1b1fa2ceb0b4d50a"; //токен , ссылка для получения выше
        String USER_ID = "12345"; //id пользователя с которым мы хотим сохранить переписку

        URIBuilder builder = new URIBuilder();
        builder.setScheme("https").setHost("api.vk.com").setPath("/method/messages.getHistory")
                .setParameter("user_id", USER_ID)
                .setParameter("count", "200") // число загружаемых сообщений НЕ БОЛЕЕ 200
                //.setParameter("offset", "2000") // смещение, необходимое для выборки определенного количества сообщений
                .setParameter("access_token", ACCESS_TOKEN);
        URI uri = builder.build();
        HttpGet httpget = new HttpGet(uri);

        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response = httpclient.execute(httpget);
        HttpEntity entity = response.getEntity();

        if (entity != null) {
            try (InputStream instream = entity.getContent()) {
                String responseAsString = IOUtils.toString(instream);
                parse(responseAsString, USER_ID);
            }
        }

    }

    private static void parse(String resp, String USER_ID) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        JSONObject jsonResponse = (JSONObject) parser.parse(resp);
        JSONArray mesList = (JSONArray) jsonResponse.get("response");
        File file = new File(fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        PrintWriter out = new PrintWriter(new File(fileName).getAbsoluteFile());
        for (int i = 1; i < mesList.size(); i++) {
            JSONObject mess = (JSONObject) mesList.get(i);
            String pathname = (String) mess.get("body");
            Object otpravka = mess.get("from_id").toString();
            Object date = mess.get("date");
            long timeStamp = (long) date * 1000;
            Date d = new Date(timeStamp);
            if (otpravka.equals(USER_ID)) {
                otpravka = "Собеседник:";
            } else {
                otpravka = "Я:";
            }
            out.print(d + " ");
            out.print(otpravka + " " + pathname + "\n");
            System.out.print(d);
            System.out.println(otpravka + pathname);
        }
        out.close();
    }
}
