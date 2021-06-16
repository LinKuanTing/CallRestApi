package internet.webapi.soap;

import java.util.HashMap;



public abstract class SoapAPI {

    protected String domainName;
    protected String serverName;
    protected String serverURL;
    protected String nameSpace;

    public SoapAPI(String webName) {
        HashMap<String, String> apiParam = new HashMap<>();
        apiParam.put("DomainName","https://cloud1.wowgohealth.com.tw");
        apiParam.put("ServerName","WebService");
        apiParam.put("NameSpace","http://tempuri.org/");

        domainName = apiParam.get("DomainName");
        serverName = apiParam.get("ServerName");
        nameSpace = apiParam.get("NameSpace");
        serverURL = domainName + "/" + serverName + "/" + webName;

    }
}
