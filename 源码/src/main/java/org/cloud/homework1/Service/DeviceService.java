package org.cloud.homework1.Service;

import org.cloud.homework1.Entity.Device;
import org.cloud.homework1.Repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeviceService {

    @Autowired
    private DeviceRepository deviceRepository;

    // 根据ID获取设备信息
    public Device getDeviceById(Long Id) {
        return deviceRepository.findById(Id).orElse(null);  // 直接返回 Device 对象
    }
}

