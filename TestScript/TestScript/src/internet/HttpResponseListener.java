package internet;

public interface HttpResponseListener {
    void onResponseResult(int code, String message, String data);
}
