package internet.webapi.soap;

import internet.HttpUtil;
import org.ksoap2.serialization.SoapObject;

import java.util.HashMap;


public class MemberRegisterAPI extends SoapAPI {

    private final String account;
    private final String password;

    public MemberRegisterAPI(String account, String password) {
        super("MemberRegister.asmx");   //service name

        this.account = account;
        this.password = password;
    }

    public void callGetUserInfo() {
        HashMap<String,Object> params = new HashMap<>();
        params.put("userAccount",account);
        params.put("password",password);
        SoapUtil.getInstance().request(serverURL, nameSpace, "GetUserInfo", params, new SoapResponseListener() {
            @Override
            public void onResponseResult(int code, String message, Object data) {
                System.out.println("code: "+code+", message: "+message+", data:"+data );
                if (code == HttpUtil.RESPONSE_OK) {
                    if (data instanceof SoapObject) {
                        SoapObject test = (SoapObject) data;
                        System.out.println(test.getProperty("GetUserInfoResult"));
                    }
                }
            }
        });
    }
}
