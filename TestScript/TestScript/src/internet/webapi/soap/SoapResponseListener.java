package internet.webapi.soap;

public interface SoapResponseListener {
    void onResponseResult(int code, String message, Object data);
}
