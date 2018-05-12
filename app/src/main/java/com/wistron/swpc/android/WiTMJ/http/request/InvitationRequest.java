package com.wistron.swpc.android.WiTMJ.http.request;

/**
 * Created by wh1604025 on 2016/6/20.
 */
public class InvitationRequest {
    private int invitation;

    public InvitationRequest(int invitation) {
        this.invitation = invitation;
    }

    public int getInvitation() {
        return invitation;
    }

    public void setInvitation(int invitation) {
        this.invitation = invitation;
    }
}
