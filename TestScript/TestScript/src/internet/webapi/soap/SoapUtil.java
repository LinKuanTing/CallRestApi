package internet.webapi.soap;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.Marshal;
import org.ksoap2.serialization.MarshalDate;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.ServiceConnection;
import org.ksoap2.transport.Transport;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import internet.HttpResponseListener;
import internet.HttpUtil;


public class SoapUtil extends Transport {

    private static final int EXCEPTION = -1;

    private static SoapUtil INSTANCE;



    private SoapUtil() {

    }

    public static SoapUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SoapUtil();
        }

        return INSTANCE;
    }

    public void request(String url, String nameSpace,
                        String action, HashMap<String, Object> params,
                        SoapResponseListener listener) {

        SoapObject soapObject = new SoapObject(nameSpace, action);
        for (Map.Entry<String, Object> param : params.entrySet()) {
            soapObject.addProperty(param.getKey(), param.getValue());
        }

        SoapSerializationEnvelope enveloper = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        enveloper.setOutputSoapObject(soapObject);
        enveloper.dotNet = true;

        Marshal dateMarshal = new MarshalDate();
        dateMarshal.register(enveloper);

        byte[] requestData;
        try {
            requestData = createRequestData(enveloper);
        } catch (IOException e) {
            sendResponseResult(listener, EXCEPTION, getExceptionMsg(e), null);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<").append("soap:Envelope").append(" ")
                .append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"").append(" ")
                .append("xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"").append(" ")
                .append("xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"").append(">").append("\n");
        sb.append("<").append("soap:Body").append(">").append("\n");
        sb.append("<").append(action).append(" ").append("xmlns=\"").append(nameSpace).append("\"").append(">").append("\n");
        for (Map.Entry<String, Object> param : params.entrySet()) {
            sb.append("<").append(param.getKey()).append(">")
                    .append(param.getValue())
                    .append("</").append(param.getKey()).append(">").append("\n");
        }
        sb.append("</").append(action).append(">").append("\n");
        sb.append("</").append("soap:Body").append(">").append("\n");
        sb.append("</").append("soap:Envelope").append(">");

        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/xml");
        headers.put("Content-length", String.valueOf(requestData.length));

        HttpUtil.getInstance().request(url, "POST", headers, sb.toString().getBytes(StandardCharsets.UTF_8), new HttpResponseListener() {
            @Override
            public void onResponseResult(int code, String message, String data) {
                try {
                    Object responseObject = null;
                    if (data != null) {
                        SoapUtil.getInstance().parseResponse(enveloper, new ByteArrayInputStream(data.getBytes()));
                        responseObject = enveloper.bodyIn;
                    }
                    sendResponseResult(listener, code, message, responseObject);
                } catch (Exception e) {
                    sendResponseResult(listener, EXCEPTION, getExceptionMsg(e), null);
                }
            }
        });
    }

    private String getExceptionMsg(Exception e) {
        return "class: " + new Throwable().getStackTrace()[2].getClassName() + ", " +
                "line: " + new Throwable().getStackTrace()[2].getLineNumber() + ", " +
                e.getMessage();
    }

    private void sendResponseResult(SoapResponseListener listener, int code, String message, Object data) {
        if (listener != null ) {
            listener.onResponseResult(code, message, data);
        }
    }

    @Deprecated
    @Override
    public List call(String s, SoapEnvelope soapEnvelope, List list) throws IOException, XmlPullParserException {
        return null;
    }

    @Deprecated
    @Override
    public List call(String s, SoapEnvelope soapEnvelope, List list, File file) throws IOException, XmlPullParserException {
        return null;
    }

    @Deprecated
    @Override
    public ServiceConnection getServiceConnection() throws IOException {
        return null;
    }
}
