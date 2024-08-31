package edu.eci.blacklistvalidator;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import edu.eci.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;

public class HostBlackListsValidatorThreads extends Thread {

    private int ocurrences;
    private int checkedListsCount;
    private String ipaddress;
    private int start;
    private int end;
    private final LinkedList<Integer> blackListOcurrences;
    private AtomicInteger globalCount = new AtomicInteger(0);

    public HostBlackListsValidatorThreads(String ipaddress, int start, int end, AtomicInteger globalCount){
        this.ipaddress = ipaddress;
        this.start = start;
        this.end = end;
        this.ocurrences = 0;
        blackListOcurrences=new LinkedList<>();
        this.globalCount = globalCount;
    }

    public void run(){
        this.checkHost(ipaddress, start, end);
    }

    public void checkHost(String ipaddress, int start, int end){
        HostBlacklistsDataSourceFacade skds=HostBlacklistsDataSourceFacade.getInstance();
        for (int i=start;i<end;i++){
            checkedListsCount += 1;
            if (skds.isInBlackListServer(i, ipaddress)){
                //System.out.println(globalCount);
                blackListOcurrences.add(i);
                globalCount.incrementAndGet();
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
