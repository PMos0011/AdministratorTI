package TransferWindow;

import Common.Logs;
import javafx.scene.control.Alert;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class PHPConnections {

    private static String returnStringFromHttpResponse(BufferedReader rd) throws IOException {

        StringBuilder sb = new StringBuilder();
        String line = "";
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }

        return sb.toString();
    }

    static boolean checkPHPSessionIfValidReturnTrue(BasicCookieStore cookieStore, String serverAddress) throws IOException {

        HttpClient client = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).setDefaultCookieStore(cookieStore).build();
        HttpPost request = new HttpPost("http://" + serverAddress + "/TI/APP_PHP/login.php");

        HttpResponse httpResponse = client.execute(request);
        BufferedReader rd = new BufferedReader
                (new InputStreamReader(
                        httpResponse.getEntity().getContent()));

        String response = returnStringFromHttpResponse(rd);

        return response.equals("true");
    }

    static boolean authorizeUserPHPSession(BasicCookieStore cookieStore, String serverAddress, String user, String password) {

        HttpClient client = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).setDefaultCookieStore(cookieStore).build();
        HttpPost request = new HttpPost("http://" + serverAddress + "/TI/APP_PHP/login.php");

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("log", user));
        params.add(new BasicNameValuePair("pass", password));


        try {
            request.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse httpResponse = client.execute(request);
            BufferedReader rd = new BufferedReader
                    (new InputStreamReader(
                            httpResponse.getEntity().getContent()));

            String response = returnStringFromHttpResponse(rd);

            if (response.equals("true"))
                return true;
            else
                new Alert(Alert.AlertType.INFORMATION, response).showAndWait();

        } catch (IOException e) {
            Logs.saveLog(e.toString(), "PHPConnections");
            new Alert(Alert.AlertType.INFORMATION, e.toString()).showAndWait();

            return false;
        }
        return false;
    }

    static String returnStringFromHttpResponse(BasicCookieStore cookieStore, String serverAddress, String groupId) throws IOException {
        HttpClient client = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).setDefaultCookieStore(cookieStore).build();
        HttpPost request = new HttpPost("http://" + serverAddress + "/TI/APP_PHP/getGRP.php");

        if (groupId != null) {
            request = new HttpPost("http://" + serverAddress + "/TI/APP_PHP/getSaves.php");
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("GRP", groupId));
            request.setEntity(new UrlEncodedFormEntity(params));
        }

        HttpResponse httpResponse = client.execute(request);

        BufferedReader rd = new BufferedReader
                (new InputStreamReader(
                        httpResponse.getEntity().getContent()));

        return returnStringFromHttpResponse(rd);
    }

    static void addPublicationLogs(BasicCookieStore cookieStore, String serverAddress, String groupId, String fileName) {

        HttpClient client = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).setDefaultCookieStore(cookieStore).build();
        HttpPost request = new HttpPost("http://" + serverAddress + "/TI/APP_PHP/logs.php");

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("GRP", groupId));
        params.add(new BasicNameValuePair("fileName", fileName));
        try {
            request.setEntity(new UrlEncodedFormEntity(params));
            client.execute(request);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static boolean changeUserPassword(CookieStore cookieStore, String serverAddress, String userName, String oldPassword, String newPassword) {

        HttpClient client = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).setDefaultCookieStore(cookieStore).build();
        HttpPost request = new HttpPost("http://" + serverAddress + "/TI/APP_PHP/changePassword.php");

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("log", userName));
        params.add(new BasicNameValuePair("pass", oldPassword));
        params.add(new BasicNameValuePair("newPassword", newPassword));
        try {
            request.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse httpResponse = client.execute(request);

            BufferedReader rd = new BufferedReader
                    (new InputStreamReader(
                            httpResponse.getEntity().getContent()));

            String response = returnStringFromHttpResponse(rd);

            if (!response.equals("true"))
                new Alert(Alert.AlertType.INFORMATION, "dop: " + response).showAndWait();
            else return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public static void logoutPHPSession(CookieStore cookieStore, String serverAddress){
        HttpClient client = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).setDefaultCookieStore(cookieStore).build();
        HttpPost request = new HttpPost("http://" + serverAddress + "/TI/APP_PHP/logout.php");

        try {
            client.execute(request);
        } catch (IOException e) {
            e.printStackTrace();}
    }
}
