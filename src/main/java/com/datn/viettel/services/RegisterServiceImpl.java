package com.datn.viettel.services;

import com.datn.viettel.common.Constants;
import com.datn.viettel.common.ResponseMessage;
import com.datn.viettel.dto.request.GiveDataRequest;
import com.datn.viettel.dto.request.RegisterDataRequest;
import com.datn.viettel.dto.request.search.MobilePackageLogSearch;
import com.datn.viettel.entities.core.MobilePackage;
import com.datn.viettel.entities.core.RegMobilePackageLog;
import com.datn.viettel.exceptions.LogicException;
import com.datn.viettel.repositories.core.MobilePackageRepository;
import com.datn.viettel.repositories.core.RegMobilePackageLogRepository;
import com.datn.viettel.services.iservice.HttpService;
import com.datn.viettel.services.iservice.RegisterService;
import com.datn.viettel.utils.DataUtils;
import com.datn.viettel.utils.IntegrationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
@Slf4j
@Service
public class RegisterServiceImpl implements RegisterService {

    private final RegMobilePackageLogRepository regMobilePackageLogRepository;
    private final MobilePackageRepository mobilePackageRepository;

    @Autowired
    public RegisterServiceImpl(
            RegMobilePackageLogRepository regMobilePackageLogRepository,
            MobilePackageRepository mobilePackageRepository
    ) {
        this.regMobilePackageLogRepository = regMobilePackageLogRepository;
        this.mobilePackageRepository = mobilePackageRepository;
    }

    /* =======================================================
     * REGISTER MOBILE PACKAGE (MOCK)
     * ======================================================= */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void registerMobilePackage(RegisterDataRequest request) {

        MobilePackage mobilePackage =
                mobilePackageRepository.findByCode(request.getServiceCode());

        if (mobilePackage == null) {
            throw new LogicException(
                    ResponseMessage.Common.PACKAGE_NOT_FOUND,
                    Constants.ExecutionCode.BUSINESS_ERROR
            );
        }

        log.info("[MOCK][REGISTER] phone={}, serviceCode={}, paymentType={}",
                request.getPhoneNumber(),
                request.getServiceCode(),
                request.getPaymentType()
        );

        regMobilePackageLogRepository.logRegisterMobilePackage(
                request.getPhoneNumber().trim(),
                request.getPhoneNumber().trim(),
                LocalDateTime.now(),
                request.getServiceCode().trim(),
                Constants.Status.Register.COMPLETED,
                ResponseMessage.Common.SUCCESS,
                Constants.RegisterType.REGISTER,
                request.getPaymentType(),
                Integer.parseInt(mobilePackage.getMoneyFee())
        );
    }

    /* =======================================================
     * GIVE MOBILE PACKAGE (MOCK)
     * ======================================================= */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void giveMobilePackage(GiveDataRequest request) {

        MobilePackage mobilePackage =
                mobilePackageRepository.findByCode(request.getServiceCode());

        if (mobilePackage == null) {
            throw new LogicException(
                    ResponseMessage.Common.PACKAGE_NOT_FOUND,
                    Constants.ExecutionCode.BUSINESS_ERROR
            );
        }

        log.info("[MOCK][GIFT] from={}, to={}, serviceCode={}",
                request.getPhoneNumberSource(),
                request.getPhoneNumberDestination(),
                request.getServiceCode()
        );

        regMobilePackageLogRepository.logRegisterMobilePackage(
                request.getPhoneNumberSource().trim(),
                request.getPhoneNumberDestination().trim(),
                LocalDateTime.now(),
                request.getServiceCode().trim(),
                Constants.Status.Register.COMPLETED,
                ResponseMessage.Common.SUCCESS,
                Constants.RegisterType.GIFT,
                request.getPaymentType(),
                Integer.parseInt(mobilePackage.getMoneyFee())
        );
    }

    /* =======================================================
     * REPORT
     * ======================================================= */
    @Override
    public Map<String, Object> getMobilePackageReport(
            MobilePackageLogSearch request) {

        int page = Optional.ofNullable(request.getPage()).orElse(0);
        int size = Optional.ofNullable(request.getSize()).orElse(20);

        Pageable pageable = PageRequest.of(page, size, Sort.by("request_at").descending());

        LocalDateTime fromDate = null;
        if (request.getFromDate() != null) {
            fromDate = request.getFromDate().atStartOfDay();
        }

        LocalDateTime toDate = null;
        if (request.getToDate() != null) {
            toDate = request.getToDate().atTime(23, 59, 59);
        }

        String code = DataUtils.isNullOrBlank(request.getCode()) ? "" : request.getCode().trim();
        String phoneNumber = DataUtils.isNullOrBlank(request.getPhoneNumber()) ? "" : request.getPhoneNumber().trim();

        Page<RegMobilePackageLog> pageData = regMobilePackageLogRepository.findAllReport(
                code,
                request.getResult(),
                request.getRegisterType(),
                request.getPaymentType(),
                phoneNumber,
                fromDate,
                toDate,
                pageable
        );

        Map<String, Object> result = new HashMap<>();
        result.put("data", pageData.getContent());
        result.put("total", pageData.getTotalElements());
        result.put("page", page);
        result.put("size", size);

        return result;
    }
}
