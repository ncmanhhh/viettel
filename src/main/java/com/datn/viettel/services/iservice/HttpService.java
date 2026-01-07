package com.datn.viettel.services.iservice;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

public interface HttpService {

    <T> T get(String url, Class<T> responseType, HttpHeaders headers);

    <T> ResponseEntity<T> getEntity(String url, Class<T> responseType, HttpHeaders headers);

    <T> T post(String url, Object requestBody, Class<T> responseType, HttpHeaders headers);

    <T> ResponseEntity<T> postEntity(String url, Object requestBody, Class<T> responseType, HttpHeaders headers);

    <T> T put(String url, Object requestBody, Class<T> responseType, HttpHeaders headers);

    <T> ResponseEntity<T> putEntity(String url, Object requestBody, Class<T> responseType, HttpHeaders headers);

    <T> T patch(String url, Object requestBody, Class<T> responseType, HttpHeaders headers);

    <T> ResponseEntity<T> patchEntity(String url, Object requestBody, Class<T> responseType, HttpHeaders headers);

    <T> T delete(String url, Class<T> responseType, HttpHeaders headers);

    <T> ResponseEntity<T> deleteEntity(String url, Class<T> responseType, HttpHeaders headers);

    String callSoap(String url, String rawSoapRequestXml);

}
