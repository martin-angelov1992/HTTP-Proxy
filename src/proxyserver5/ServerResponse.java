/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package proxyserver5;

/**
 *
 * @author ASUS
 */
public class ServerResponse {
    private String responseRaw;

    public ServerResponse(String responseRaw) {
        setResponseRaw(responseRaw);
    }

    public String getResponseRaw() {
        return responseRaw;
    }

    public void setResponseRaw(String responseRaw) {
        this.responseRaw = responseRaw;
    }
    
}
