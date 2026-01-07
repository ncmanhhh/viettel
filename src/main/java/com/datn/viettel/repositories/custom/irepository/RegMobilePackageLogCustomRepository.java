package com.datn.viettel.repositories.custom.irepository;

import java.time.LocalDateTime;

public interface RegMobilePackageLogCustomRepository {

    void logRegisterMobilePackage(String phoneNumberSource,
                                  String phoneNumberDestination,
                                  LocalDateTime requestAt,
                                  String serviceCode,
                                  Short result,
                                  String resultMessage,
                                  Short registerType,
                                  Short paymentType,
                                  Integer price);

}
