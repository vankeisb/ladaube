package com.ladaube.upload

/**
 * Created by IntelliJ IDEA.
 * User: vankeisb
 * Date: 08/06/12
 * Time: 18:14
 * To change this template use File | Settings | File Templates.
 */
class DataUploadedEvent extends BaseEvent {

    long totalSize
    long uploaded
    int postEventId
    String fileName

}
