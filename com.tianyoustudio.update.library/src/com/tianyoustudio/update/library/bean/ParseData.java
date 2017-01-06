package com.tianyoustudio.update.library.bean;


public interface ParseData {
    <T> T parse(String httpResponse);
}
