package internet;

import java.io.*;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;


public class HttpUtil {
    private static final int TIME_OUT = 20000;

    public static final int EXCEPTION = -1;
    public static final int RESPONSE_OK = 200;

    private static HttpUtil INSTANCE = null;

    private HttpUtil() {

    }

    public static HttpUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HttpUtil();
        }

        return INSTANCE;
    }

    public void request(String  urlStr,
                        String httpMethod,
                        HashMap<String, String> headers,
                        byte[] body,
                        HttpResponseListener listener,
                        int timeout) {
        URL url;
        try {
            url = new URL(urlStr);
        } catch (MalformedURLException e) {
            sendResponseListener(listener, EXCEPTION, getExceptionMsg(e), null);
            return;
        }

        HttpsURLConnection connection;
        try {
            connection = (HttpsURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
            sendResponseListener(listener, EXCEPTION, getExceptionMsg(e), null);
            return;
        }

        try {
            connection.setRequestMethod(httpMethod);
        } catch (ProtocolException e) {
            sendResponseListener(listener, EXCEPTION, getExceptionMsg(e), null);
            return;
        }

        connection.setUseCaches(false);
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setConnectTimeout(timeout);
        connection.setReadTimeout(timeout);

        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }

        try {
            connection.connect();
        } catch (IOException e) {
            sendResponseListener(listener, EXCEPTION, getExceptionMsg(e), null);
            return;
        }

        try {
            if (body != null && body.length > 0) {
                DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                out.write(body);
                out.flush();
                out.close();
            }
        } catch (IOException e) {
            sendResponseListener(listener, EXCEPTION, getExceptionMsg(e), null);
            return;
        }

        try {
            int code = connection.getResponseCode();
            String message = connection.getResponseMessage();
            String responseData = null;
            if (code == RESPONSE_OK) {
                InputStream in = connection.getInputStream();
                responseData = getResponseData(in);
            }
            sendResponseListener(listener, code, message, responseData);
        } catch (IOException e) {
            sendResponseListener(listener, EXCEPTION, getExceptionMsg(e), null);
        }
    }

    public void request(String  urlStr,
                        String httpMethod,
                        HashMap<String, String> headers,
                        byte[] body,
                        HttpResponseListener listener) {
        request(urlStr, httpMethod, headers, body, listener, TIME_OUT);
    }

    public void request(String  urlStr,
                        String httpMethod,
                        HashMap<String, String> headers,
                        byte[] body) {
        request(urlStr, httpMethod, headers, body, null, TIME_OUT);
    }

    private String getResponseData(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String line;

        try {
            while((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            throw new IOException();
        } finally {
            reader.close();
        }
        return sb.toString();
    }

    private String getExceptionMsg(Exception e) {
        return "class: " + new Throwable().getStackTrace()[2].getClassName() + ", " +
                "line: " + new Throwable().getStackTrace()[2].getLineNumber() + ", " +
                e.getMessage();
    }

    private void sendResponseListener(HttpResponseListener listener, int code, String message, String data) {
        if (listener != null) {
            listener.onResponseResult(code, message, data);
        }
    }
}

