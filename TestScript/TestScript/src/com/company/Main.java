package com.company;

import internet.webapi.soap.MemberRegisterAPI;

public class Main {

    public static void main(String[] args) {
	    // write your code here
        MemberRegisterAPI memberRegisterAPI = new MemberRegisterAPI("gshtest845","1234567");
        memberRegisterAPI.callGetUserInfo();
    }
}
