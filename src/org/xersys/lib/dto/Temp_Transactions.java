package org.xersys.lib.dto;

import java.util.Date;
import java.util.LinkedList;
import org.json.simple.JSONObject;

public class Temp_Transactions{
    String sSourceCd;
    String sOrderNox;
    Date dCreatedx;
    String sPayloadx;
    
    LinkedList laColumns = null;
    
    public Temp_Transactions(){
        laColumns = new LinkedList();
        laColumns.add("sSourceCd");
        laColumns.add("sOrderNox");
        laColumns.add("dCreatedx");
        laColumns.add("sPayloadx");
        
        sSourceCd = "";
        sOrderNox = "";
        sPayloadx = "";
    }
    
    public Object getValue(int fnColumn) {
        switch(fnColumn){
            case 1: return sSourceCd;
            case 2: return sOrderNox;
            case 3: return dCreatedx;
            case 4: return sPayloadx;
            default: return null;
        }
    }

    public Object getValue(String fsColumn) {
        int lnCol = getColumn(fsColumn);
        
        if (lnCol > 0){
            return getValue(lnCol);
        } else
            return null;
    }

    public String getColumn(int fnCol) {
        if (laColumns.size() < fnCol){
            return "";
        } else 
            return (String) laColumns.get(fnCol - 1);
    }

    public int getColumn(String fsCol) {
        return laColumns.indexOf(fsCol) + 1;
    }

    public void setValue(int fnColumn, Object foValue) {
        switch(fnColumn){
            case 1: sSourceCd = (String) foValue; break;
            case 2: sOrderNox = (String) foValue; break;
            case 3: dCreatedx = (Date) foValue; break;
            case 4: sPayloadx = (String) foValue; break;
        }     
    }

    public void setValue(String fsColumn, Object foValue) {
        int lnCol = getColumn(fsColumn);
        if (lnCol > 0){
            setValue(lnCol, foValue);
        }
    }
    
    public String toJSONString() {
        JSONObject loJSON = new JSONObject();
        
        for(int i = 0; i < laColumns.size(); i++){
            loJSON.put(laColumns.get(i), getValue(getColumn(i + 1)));
        }
        
        return loJSON.toJSONString();
    }
    
    public void setSourceCode(String fsValue){
        sSourceCd = fsValue;
    }
    public String getSourceCode(){
        return sSourceCd;
    }
    
    public void setOrderNo(String fsValue){
        sOrderNox = fsValue;
    }
    public String getOrderNo(){
        return sOrderNox;
    }
    
    public void setDateCreated(Date fdValue){
        dCreatedx = fdValue;
    }
    public Date getDateCreated(){
        return dCreatedx;
    }
    
    public void setPayload(String fsValue){
        sPayloadx = fsValue;
    }
    public String getPayload(){
        return sPayloadx;
    }
}
