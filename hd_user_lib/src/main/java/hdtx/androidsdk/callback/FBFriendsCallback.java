package hdtx.androidsdk.callback;

import java.util.List;

import hdtx.androidsdk.data.FBUser;

public interface FBFriendsCallback {

    void success(List<FBUser> users);

    //1004宜搜下单失败
    void fail(int code,String message);

}
