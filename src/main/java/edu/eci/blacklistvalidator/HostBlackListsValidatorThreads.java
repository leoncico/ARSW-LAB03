package edu.eci.blacklistvalidator;

import java.util.LinkedList;
import java.util.List;

import edu.eci.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;

public class HostBlackListsValidatorThreads extends Thread {

    private int ocurrences;
    private int checkedListsCount;
    private String ipaddress;
    private int start;
    private int end;
    private LinkedList<Integer> blackListOcurrences;
    private int globalCount;
    private Object lock;

    public HostBlackListsValidatorThreads(String ipaddress, int start, int end, int globalCount){
        this.ipaddress = ipaddress;
        this.start = start;
        this.end = end;
        this.ocurrences = 0;
        blackListOcurrences=new LinkedList<>();
        this.globalCount = globalCount;
        this.lock = new Object();
    }

    public void run(){
        this.checkHost(ipaddress, start, end);
    }

    public void checkHost(String ipaddress, int start, int end){
        HostBlacklistsDataSourceFacade skds=HostBlacklistsDataSourceFacade.getInstance();
        for (int i=start;i<end;i++){
            checkedListsCount += 1;
            if (skds.isInBlackListServer(i, ipaddress)){
                synchronized(lock){
                    globalCount++;
                }
                System.out.println(globalCount);
                blackListOcurrences.add(i);
                ocurrences++;
            }
        }
    }

    public List<Integer> getBlackListOcurrences(){
        return blackListOcurrences;
    }

    public int getOcurrences(){
        int actualOcurrences = ocurrences;
        ocurrences = 0;
        return actualOcurrences;
    }

    public int getCheckedListsCount(){
        return checkedListsCount;
    }

}
