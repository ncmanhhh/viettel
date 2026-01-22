package com.datn.viettel.services.iservice;

import com.datn.viettel.dto.request.GiveDataRequest;
import com.datn.viettel.dto.request.RegisterDataRequest;
import com.datn.viettel.dto.request.search.MobilePackageLogSearch;
import com.datn.viettel.entities.core.RegMobilePackageLog;

import java.util.List;
import java.util.Map;

public interface RegisterService {

    void registerMobilePackage(RegisterDataRequest request);

    void giveMobilePackage(GiveDataRequest request);

    Map<String, Object> getMobilePackageReport(MobilePackageLogSearch request);

}
