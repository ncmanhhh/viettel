package com.datn.viettel.services.iservice;

import com.datn.viettel.dto.request.RequestOtpRequest;
import com.datn.viettel.dto.request.VerifyOtpRequest;

import java.util.List;

public interface IntegrationService {

    boolean checkCustomerInfo(String phoneNumber);

    void requestOtp(RequestOtpRequest request);

    void verifyOtp(VerifyOtpRequest request);

    float[] embedding(String prompt);

    List<Object> getProvinces();

    List<Object> getDistricts(String provinceId);

    List<Object> getWards(String provinceId, String districtId);

}
