/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package proxyserver5;

import java.util.Map;
import java.util.Set;

/**
 *
 * @author ASUS
 */
class OnShutdown extends Thread {
    private Map<String, Integer> requests;

    public OnShutdown(Map<String, Integer> requests) {
        setRequests(requests);
    }

    public Map<String, Integer> getRequests() {
        return requests;
    }

    public void setRequests(Map<String, Integer> requests) {
        this.requests = requests;
    }
    @Override
    public void run() {
        int requestSum = 0;
        Set<Map.Entry<String, Integer>> set = requests.entrySet();
        for(Map.Entry<String, Integer> entry: set) {
            requestSum += entry.getValue();
        }
        System.out.println("Requet statistics");
        System.out.printf("%50s %5s %5s\n", "Host", "Count", "%");
        for(Map.Entry<String, Integer> entry: set) {
            System.out.printf("%50s %5s %5s\n", entry.getKey(), entry.getValue(), entry.getValue()*100/requestSum);
        }
    }

    
    
}
