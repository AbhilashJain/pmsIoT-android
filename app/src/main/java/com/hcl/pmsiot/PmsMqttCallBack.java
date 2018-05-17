package com.hcl.pmsiot;

import java.io.Serializable;

public interface PmsMqttCallBack extends Serializable {

    void messageArrived(String response) ;

}
